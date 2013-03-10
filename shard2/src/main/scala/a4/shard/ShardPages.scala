package a4.shard

import a4.shard.templating.PageRenderer
import a4.shard.routing.Request
import a4.shard.routing.Response
import a4.shard.routing.EmptyResponse
import a4.shard.routing.Status._
import a4.shard.routing.StringResponse
import a4.util.AssetResolver
import a4.shard.routing.InputStreamResponse
import a4.shard.routing.EmptyResponse
import eu.henkelmann.actuarius.ActuariusTransformer
import a4.shard.transforming.PageContentTransformer
import com.google.common.net.MediaType

/**
 * Kind of a controller, maycase _ =>  need more than one in the future.
 */
case class ShardPages(val config: ShardConfiguration, val pageRenderer: PageRenderer, val assetResolver: AssetResolver, val contentTransformer: PageContentTransformer) {
  private val ROOT_TEMPLATE = "root.mustache"
  private val PAGE_TEMPLATE = "page.mustache"

  def root(req: Request): Response =
    StringResponse(Ok, pageRenderer.render(ROOT_TEMPLATE, Map("pageTitle" -> "Shard", "wikis" -> config.wikis)), Some(MediaType.HTML_UTF_8))

  private def getPage(req: Request): Option[WikiPage] =
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
      wiki <- config.wikiById(wikiName)
      pageName <- Request.getSingleParam(req, "page").orElse(Some("/root")) // default to the wiki root if no page was specified    
    } yield WikiPage(wiki, pageName)

  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def page(req: Request): Response = getPage(req) match {
    // even if we can parse a wiki page from the URL that belongs to an actual wiki, it may not be an existing page
    case Some(wikiPage) if wikiPage.exists =>
      StringResponse(
          Ok, 
          pageRenderer.render(PAGE_TEMPLATE, Map("pageTitle" -> wikiPage.wiki.name, "wikiPage" -> wikiPage, "pageContent" -> contentTransformer.transform(wikiPage))), 
          Some(MediaType.HTML_UTF_8))
    case _ => EmptyResponse(NotFound) // TODO: make the 404 for wiki pages be a page where you can create a new page
  }
 
   private def getAssetContentType(req: Request) : Option[MediaType] = 
    if (req.pathUrl.startsWith("/css")) Some(MediaType.CSS_UTF_8)
    else if (req.pathUrl.startsWith("/js")) Some(MediaType.TEXT_JAVASCRIPT_UTF_8)
    else if (req.pathUrl.toLowerCase.endsWith(".jpg")) Some(MediaType.JPEG)
    else if (req.pathUrl.toLowerCase.endsWith(".png")) Some(MediaType.PNG)
    else if (req.pathUrl.toLowerCase.endsWith(".gif")) Some(MediaType.GIF)
    else None
        
  def classpathAsset(req: Request): Response =
    InputStreamResponse(Ok, assetResolver.getInputStream(req.pathUrl), getAssetContentType(req))

}