package a4.shard.search

import java.io.File

/**
 * A generic trait that hides the implementation details of an indexing and search mechanism.
 */
trait FullTextIndex extends AutoCloseable {

  def prepareIndex(name: String) : Unit

  // TODO I'm not sure I like providing a File here, perhaps some higher level of abstraction? At the moment
  // it is used to determine attributes like lastmodified date for freshness.
  // Maybe create an artifical interface that abstracts just that? Indexable(id: String, lastModified: long) ?
  def index(name: String, pageId: String, pageFile: File) : Unit

  def cleanIndex(indexName: String, pageIsValid: (String) => Boolean) : Unit

  def search(indexName: String, query: String) : List[String]

  def close() : Unit

}
