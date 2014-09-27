package a4.shard.controller

import a4.shard._
import a4.shard.render._
import a4.shard.routing.Status._
import a4.shard.routing._
import a4.shard.templating.PageTemplateRenderer
import a4.shard.transforming.PageContentTransformer
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class PageController(config: Configuration, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) {
  
  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def apply(req: Request): Response = getPage(req) match {
    case Some(page) if page.isInstanceOf[Folder] => InputStreamResponse(Ok, ContentRenderer.render(page.asInstanceOf[Folder], pageTemplateRenderer, assetResolver, contentTransformer), Some(MediaType.HTML_UTF_8))
    case Some(page) if page.isInstanceOf[Page] => InputStreamResponse(Ok, ContentRenderer.render(page.asInstanceOf[Page], pageTemplateRenderer, assetResolver, contentTransformer), Some(MediaType.HTML_UTF_8))
    // TODO: be cleverer with the mediatype, in the case of attachments we should attempt to cover some common cases (extension based? maybe there is a library for this? mediatype sniffer?)
    case Some(page) if page.isInstanceOf[Attachment] => InputStreamResponse(Ok, ContentRenderer.render(page.asInstanceOf[Attachment]), Some(MediaType.APPLICATION_BINARY))
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