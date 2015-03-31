package a4.shard.search

import a4.shard._

/**
 * TODO: I imagine I will need an "add" and "remove" call to deal with pages managed through the UI
 */
class SearchService(config: Configuration, fullTextIndex: FullTextIndex) {

  private def index(indexName: String, folder: Folder) : Unit = {
    for (page <- folder.pages) {
      fullTextIndex.index(indexName, page.relativeUrl, page.file)
    }
    for (childFolder <- folder.folders) {
      index(indexName, childFolder)
    }
  }

  def startIndexing() : Unit =
    for (wiki <- config.wikis) {
      fullTextIndex.prepareIndex(wiki.id)
      index(wiki.id, Content.getRootFolder(wiki))
      fullTextIndex.cleanIndex(wiki.id, (id: String) => {
        Content.toContentWithFallback(wiki, id) match {
          case Some(c) => c.isValid
          case None => false
        }
      })
    }

  def search(query: String) : SearchResults = {
    SearchResults(query,
      config.wikis
        .flatMap(wiki => fullTextIndex.search(wiki.id, query)
        .map(id => Content.toContentWithFallback(wiki, id))
        .flatten // remove Nones
        .map(content => SearchResult(wiki, content))
    ))
  }

  def close() : Unit = {
    fullTextIndex.close()
  }
}
