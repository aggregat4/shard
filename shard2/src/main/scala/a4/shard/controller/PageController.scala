package a4.shard.controller

import java.io.InputStream
import java.nio.file.Files

import a4.shard._
import a4.shard.render._
import a4.shard.routing.Status._
import a4.shard.routing._
import a4.util.StreamUtil
import com.google.common.net.MediaType

case class PageController(config: Configuration, contentRenderer: ContentRenderer) {
  
  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def view(req: Request): Response = getPage(req) match {
    case Some(page) if page.isInstanceOf[Folder] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Folder]), Some(MediaType.HTML_UTF_8))
    case Some(page) if page.isInstanceOf[Page] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Page]), Some(MediaType.HTML_UTF_8))
    case Some(page) if page.isInstanceOf[Attachment] => InputStreamResponse(Ok, contentRenderer.render(page.asInstanceOf[Attachment]), Some(MediaType.parse(Files.probeContentType(page.file.toPath))))
    case _ => EmptyResponse(NotFound) // TODO: this not found can only mean that we can't find the actual WIKI, all other cases should theoretically be handled by the fallback logic in getPage(), so this means that in this case we should default to the root of all Wikis and show a flash message that we didn't find the wiki (possibly offer to create it?
  }

  // TODO : should this be a for with yield and just return BadRequest in both failure cases?
  def edit(req: Request): Response = getPage(req) match {
    case page: Some[Page] => findNewContent(req.formData) match {
      case Some(newContent) => savePage(page.get, newContent)
      case _ => EmptyResponse(BadRequest)
    }
    case _ => EmptyResponse(NotFound)
  }

  private def getPage(req: Request): Option[Content] =
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
      wiki <- config.wikiById(wikiName)
      pageName <- Request.getSingleParam(req, "page").orElse(Some("/")) // default to the wiki root if no page was specified
      page <- Content.toContentWithFallback(wiki, pageName)
    } yield page

  private def savePage(page: Page, newContent: InputStream): Response = {
    // TODO: at some point in the future when we have GIT integration this may not be sufficient, however if we do the git commits using file watching, it may be enough
    StreamUtil.copy(newContent, page.file)
    RedirectResponse(SeeOther, Content.toLink(page))
  }

  private def findNewContent(parts: List[Part]): Option[InputStream] = parts.find(p => p.name == "newContent") match {
    case Some(part) => Some(part.inputStream)
    case None => None
  }

}