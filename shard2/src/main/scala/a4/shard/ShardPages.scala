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
  private val rootPage = new RootPage(config.wikis)
  
  def root(req: Request): Response =
    StringResponse(Ok, rootPage.render(pageRenderer), Some(MediaType.HTML_UTF_8))

  private def toPath(components: List[String]) : String = components.reverse.mkString("/")

  /**
   * Each element in this list is a folder path, but we have the requirement to show the "default" page
   * for a folder if there is one. The default page is a file with the same prefix as the folder name.
   * E.g.: for /foo/ the default file is "/foo/foo".
   */
  private def determinePossiblePages(wiki: Wiki, components: List[String]) : List[WikiPage] =
    if (components.isEmpty) List()
    else 
      List(new ConcretePage(wiki, toPath(List(components.head) ++ components), contentTransformer), new FolderPage(wiki, toPath(components)) ) ++ 
      determinePossiblePages(wiki, components.tail)
    
  private def determinePage(wiki: Wiki, pageName: String) : Page = {
    val nameComponents = pageName.split("/").toList.reverse  // reverse the path components to have the more specific one first
    val folderRequested = pageName.endsWith("/") || nameComponents.isEmpty
    val possiblePages = 
      if (! folderRequested) 
        List(new ConcretePage(wiki, nameComponents.head, contentTransformer)) ++ determinePossiblePages(wiki, nameComponents.tail)
      else 
        determinePossiblePages(wiki, nameComponents)
    possiblePages.find(p => p.exists).getOrElse(rootPage)
  }
    
  private def getPage(req: Request): Option[Page] =
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
      wiki <- config.wikiById(wikiName)
      pageName <- Request.getSingleParam(req, "page").orElse(Some("/")) // default to the wiki root if no page was specified    
    } yield determinePage(wiki, pageName)

  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def page(req: Request): Response = getPage(req) match {
    case Some(page) => StringResponse(Ok, page.render(pageRenderer), Some(MediaType.HTML_UTF_8))
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