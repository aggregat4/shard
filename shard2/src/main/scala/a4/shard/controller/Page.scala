package a4.shard.controller

import a4.shard.routing.Status._
import a4.shard.routing._
import a4.shard.templating.PageTemplateRenderer
import a4.shard.transforming.PageContentTransformer
import a4.shard.{Configuration, Content}
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class Page(val config: Configuration, val pageTemplateRenderer: PageTemplateRenderer, val assetResolver: AssetResolver, val contentTransformer: PageContentTransformer) {
  
  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  // TODO: actually render the page!
  // TODO: render the content of the page
      // need context! with the markdown rendered content
      // need fallback for Folder (list of pages + attachments or something)
      // need attempt to find root page (root.shard.md)
  def apply(req: Request): Response = getPage(req) match {
    case Some(page) => InputStreamResponse(Ok, ContentRenderer., Some(MediaType.HTML_UTF_8))
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