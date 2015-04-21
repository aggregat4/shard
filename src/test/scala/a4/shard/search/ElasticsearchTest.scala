package a4.shard.search

import org.elasticsearch.action.search.SearchType
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.{Node, NodeBuilder}
import org.junit.Assert._
import org.junit.{After, Before, Test}

class ElasticsearchTest {

  val json = "{ body: \"This is a **Markdown** page.\" }" // later consider structure of pages, what metadata
  val INDEX_NAME: String = "pageindex"
  val PAGE_ID: String = "pageid"

  private var node : Node = null

  @Before def prepare() = {
    node = NodeBuilder.nodeBuilder().local(true).node()
    node.client()
      .prepareIndex(INDEX_NAME, "page", PAGE_ID)
      .setSource(json)
      .execute()
      .actionGet()
    Thread.sleep(500)
    // TODO this index is already persistent and needs to be cleaned up before the test
    // TODO sleeping is shit, figure out how to wait for everything to sync up, see https://github.com/elasticsearch/elasticsearch/blob/master/src/test/java/org/elasticsearch/count/query/SimpleQueryTests.java and https://github.com/elasticsearch/elasticsearch/blob/master/src%2Ftest%2Fjava%2Forg%2Felasticsearch%2Ftest%2FElasticsearchIntegrationTest.java
  }

  @Test def emptyQueryForAllPages() = {
    val searchResponseAll = node.client().prepareSearch().execute().actionGet()
    println(searchResponseAll.getHits.totalHits())
    assertEquals(1, searchResponseAll.getHits.totalHits())
  }

  @Test def gettingASpecificExistingPage() = {
    val getResponse = node.client().prepareGet(INDEX_NAME, "page", PAGE_ID).execute().actionGet()
    assertTrue(getResponse.isExists)
  }

  @Test def gettingASpecificNonExistingPage() = {
    val getResponse = node.client().prepareGet(INDEX_NAME, "page", "DOESNOTEXIST").execute().actionGet()
    assertFalse(getResponse.isExists)
  }

  @Test def queryingForExistingWords() = {
    val searchResponse = node.client().prepareSearch(INDEX_NAME)
      .setTypes("page")
      .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      .setQuery(QueryBuilders.commonTerms("body", "a markdown page").highFreqMinimumShouldMatch("100%").lowFreqMinimumShouldMatch("100%"))
      //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))
      .setFrom(0).setSize(60).setExplain(true)
      .execute()
      .actionGet()
    assertEquals(1, searchResponse.getHits.totalHits)
  }

  @Test def queryingForNonExistingWords() = {
    val searchResponse2 = node.client().prepareSearch(INDEX_NAME)
      .setTypes("page")
      .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      //      .setQuery(QueryBuilders.commonTerms("body", "foo"))
      .setQuery(QueryBuilders.commonTerms("body", "foo page").highFreqMinimumShouldMatch("100%").lowFreqMinimumShouldMatch("100%"))
      //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))
      .setFrom(0).setSize(60).setExplain(true)
      .execute()
      .actionGet()
    assertEquals(0, searchResponse2.getHits.totalHits)
  }

  @Test def scrollingForAllPages() = {
    val searchResponse3 = node.client().prepareSearch(INDEX_NAME)
      .setSearchType(SearchType.SCAN)
      .setScroll(new TimeValue(60000))
      .setSize(100)
      .execute()
      .actionGet()
    assertEquals(1, searchResponse3.getHits.totalHits)
  }

  @After def cleanup() = {
    node.close()
  }

}