package a4.shard.search

import java.io.File

import a4.shard.Configuration
import a4.util.FileUtil
import com.typesafe.config.ConfigFactory
import org.junit.Assert._
import org.junit.{After, Before, Test}

class SearchServiceTest {

  class MockFullTextIndex extends FullTextIndex {
    var index : scala.collection.mutable.Map[String, scala.collection.mutable.Map[String, String]] = scala.collection.mutable.Map()

    override def prepareIndex(name: String): Unit = {
      index = scala.collection.mutable.Map(name -> scala.collection.mutable.Map())
    }

    override def cleanIndex(indexName: String, pageIsValid: (String) => Boolean): Unit = {}

    override def close(): Unit = {
      index = null
    }

    override def index(name: String, pageId: String, pageFile: File): Unit = {
      index(name) += pageId -> FileUtil.readAsUtf8(pageFile)
    }

    override def search(indexName: String, query: String): List[String] = {
      index(indexName).map{case (pageId, pageContent) => if (pageContent.contains(query)) Some(pageId) else None}.flatten.toList
    }
  }

  var searchService : SearchService = null

  @Before def prepare() : Unit = {
    searchService = new SearchService(Configuration(ConfigFactory.load("testconfiguration")), new MockFullTextIndex)
    searchService.startIndexing()
  }

  @Test def searchForExistingString() : Unit = {
    val searchResults = searchService.search("aardvark")
    assertEquals(1, searchResults.results.size)
    assertEquals("folder1/page2", searchResults.results(0).content.relativeUrl)
  }

  @Test def searchForNonExistingString() : Unit = {
    val searchResults = searchService.search("thisstringdoesnotexist")
    assertEquals(0, searchResults.results.size)
  }

  @After def cleanup() : Unit = {
    searchService.close()
  }

}
