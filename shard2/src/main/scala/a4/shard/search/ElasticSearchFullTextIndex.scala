package a4.shard.search

import java.io.File

import a4.util.FileUtil
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.common.xcontent.XContentFactory._
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.NodeBuilder

class ElasticSearchFullTextIndex extends FullTextIndex {
  val esNode = NodeBuilder.nodeBuilder().local(true).node()
  val client : Client = esNode.client()

  val PAGE_INDEX_TYPE = "page"
  val TIMESTAMP_FIELD_NAME = "_timestamp"
  val CONTENT_FIELD_NAME = "content"

  def prepareIndex(name: String) : Unit = {
    client.admin().indices().prepareCreate(name)
      .addMapping("{ \"page\" : { \"_timestamp\" : { \"enabled\" : true, \"store\" : \"yes\" } } }")
  }

  private def indexExists(client: Client, indexName: String): Boolean = {
    client.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet().isExists
  }

  private def requiresReindexing(indexName: String, pageId: String, pageFile: File) : Boolean = {
    val getResponse = client.prepareGet(indexName, PAGE_INDEX_TYPE, pageId).execute().actionGet()
    ! getResponse.isExists || getResponse.getField(TIMESTAMP_FIELD_NAME).getValue.asInstanceOf[Long] < pageFile.lastModified
  }

  private def addToIndex(client: Client, indexName: String, pageId: String, pageFile: File) : Unit =
    client.prepareIndex(indexName, PAGE_INDEX_TYPE, pageId).setSource(
      jsonBuilder()
        .startObject()
        .field(CONTENT_FIELD_NAME, FileUtil.readAsUtf8(pageFile))
        .field(TIMESTAMP_FIELD_NAME, pageFile.lastModified.toString)
        .endObject()
    ).execute()

  // TODO test this timestamp checking logic in the unit test
  def index(indexName: String, pageId: String, pageFile: File) : Unit =
    if (! indexExists(client, indexName) || requiresReindexing(indexName, pageId, pageFile)) {
      addToIndex(client, indexName, pageId, pageFile)
    }

  def cleanIndex(indexName: String, pageIsValid: (String) => Boolean) : Unit = {
    if (indexExists(client, indexName)) {
      var scrollResponse = client.prepareSearch(indexName)
        .setSearchType(SearchType.SCAN)
        .setScroll(new TimeValue(60000))
        .setSize(100)
        .execute()
        .actionGet()
      for (hit <- scrollResponse.getHits.getHits) {
        while (scrollResponse.getHits.getHits.length != 0) {
          val id = hit.getId
          if (! pageIsValid(id)) {
            // TODO verify that just executing without waiting for the result with actionGet is ok
            client.prepareDelete(indexName, PAGE_INDEX_TYPE, id).execute()
          }
        }
        scrollResponse = client
          .prepareSearchScroll(scrollResponse.getScrollId)
          .setScroll(new TimeValue(60000))
          .execute()
          .actionGet()
      }
    }
  }

  def search(indexName: String, query: String) : List[String] = {
    val searchResponse = client.prepareSearch(indexName)
      .setTypes("page")
      .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      .setQuery(QueryBuilders.commonTerms(CONTENT_FIELD_NAME, query)
      .highFreqMinimumShouldMatch("100%")
      .lowFreqMinimumShouldMatch("100%"))
      .setFrom(0).setSize(60).setExplain(true)
      .execute()
      .actionGet()
    searchResponse.getHits.getHits.map(hit => hit.getId).toList
  }

  def close() : Unit = esNode.close()

}
