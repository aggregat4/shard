package a4.shard.controller

import a4.shard.routing.Status._
import a4.shard.routing.{EmptyResponse, Request, Response, StringResponse}
import a4.shard.templating.PageRenderer
import a4.shard.transforming.PageContentTransformer
import a4.shard.{Content, ShardConfiguration}
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class Page(val config: ShardConfiguration, val pageRenderer: PageRenderer, val assetResolver: AssetResolver, val contentTransformer: PageContentTransformer) {
  
  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  // TODO: actually render the page!
  def apply(req: Request): Response = getPage(req) match {
    case Some(page) => StringResponse(Ok, "TODO", Some(MediaType.HTML_UTF_8))
    case _ => EmptyResponse(NotFound) // TODO: this not found can only mean that we can't find the actual WIKI, all other cases should theoretically be handled by the fallback logic in getPage(), so this means that in this case we should default to the root of all Wikis and show a flash message that we didn't find the wiki (possibly offer to create it?
  }

  private def getPage(req: Request): Option[Content] =
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
      wiki <- config.wikiById(wikiName)
      pageName <- Request.getSingleParam(req, "page").orElse(Some("/")) // default to the wiki root if no page was specified
      page <- Content.toContentWithFallback(wiki, pageName)
    } yield page

}