package a4.shard.controller

import java.nio.file.Files

import a4.shard._
import a4.shard.render._
import a4.shard.routing.Status._
import a4.shard.routing._
import com.google.common.net.MediaType

case class PageController(config: Configuration, contentRenderer: ContentRenderer) {
  
  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def apply(req: Request): Response = getPage(req) match {
    case Some(page) if page.isInstanceOf[Folder] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Folder]), Some(MediaType.HTML_UTF_8))
    case Some(page) if page.isInstanceOf[Page] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Page]), Some(MediaType.HTML_UTF_8))
    // TODO: the Java 7 mediatype prober can be extended somehow, Apache Tika apparently does this
    case Some(page) if page.isInstanceOf[Attachment] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Attachment]), Some(MediaType.parse(Files.probeContentType(page.file.toPath))))
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