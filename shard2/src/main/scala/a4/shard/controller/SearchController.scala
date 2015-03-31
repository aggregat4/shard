package a4.shard.controller

import a4.shard.Configuration
import a4.shard.render.SearchResultsRenderer
import a4.shard.routing._
import a4.shard.search.SearchService
import com.google.common.net.MediaType

// TODO: do error handling more structured, return some json object
case class SearchController(config: Configuration, searchService: SearchService, searchResultsRenderer: SearchResultsRenderer) {
  val QUERY_PARAM = "q"

  def apply(req: Request): Response =
    if (req.params(QUERY_PARAM) == null || req.params(QUERY_PARAM).isEmpty) {
      StringResponse(Status.BadRequest, "Require a query parameter called 'q'", Some(MediaType.PLAIN_TEXT_UTF_8))
    } else {
      val results = searchService.search(req.params(QUERY_PARAM)(0))
      InputStreamResponse(Status.Ok, searchResultsRenderer.render(results))
    }
}
