package a4.shard

case class SearchResult(wiki: Wiki, content: Content) // ranking?

case class SearchResults(query: String, results: List[SearchResult])
