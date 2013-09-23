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
 * Kind of a controller, may need more than one in the future.
 * 
 * TODO:
 * - pull through the Content refactoring here as well: specifically I need a way to generate the parent-chain of Folders for all the candidate pages that I create in determining possible pages for a given URL
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
  private def determinePossiblePages(wiki: Wiki, components: List[String]) : List[Page] =
    if (components.isEmpty) List()
    else 
      List(
          new ConcretePage(wiki, toPath(List(components.head) ++ components), contentTransformer), // for each folder, try the folder's name as a page 
          new FolderPage(wiki, toPath(components), contentTransformer)
      ) ++ // and try the folder itself of course 
      determinePossiblePages(wiki, components.tail)
    
  private def determinePage(wiki: Wiki, pageName: String) : Content = {
    val nameComponents = pageName.split("/").toList.reverse  // reverse the path components to have the more specific one first
    val folderRequested = pageName.endsWith("/") || nameComponents.isEmpty
    val candidate = if (folderRequested) new Folder()
//    val possiblePages = 
//      (if (! folderRequested) List(new Page(wiki, pageName)) else List()) ++ // if the user requested an actual page, let's try to resolve it as a page, otherwise start the folder based fallback logic 
//      determinePossiblePages(wiki, nameComponents) ++ 
//      // second to last fallback: by convention if nothing could be resolved, we see if there is a page called "root" in the root directory of the wiki
//      List(new ConcretePage(wiki, "root", contentTransformer)) 
//    // and finally, finally (and this should always work) we just show the folder page for the root of this wiki
//    possiblePages.find(p => p.exists).getOrElse(Root(wiki))
////    // if no page was found, with fallback, render the folder page for the root of the wiki
////    foundPage match {
////      case Some(p) => p
////      case None => new FolderPage(wiki, "/", contentTransformer)
////    }
  }
    
  private def getPage(req: Request): Option[Content] =
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
      wiki <- config.wikiById(wikiName)
      pageName <- Request.getSingleParam(req, "page").orElse(Some("/")) // default to the wiki root if no page was specified    
    } yield determinePage(wiki, pageName)

  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def page(req: Request): Response = getPage(req) match {
    case Some(page) => StringResponse(Ok, page.render(pageRenderer), Some(MediaType.HTML_UTF_8))
    case _ => EmptyResponse(NotFound) // TODO: this not found can only mean that we can't find the actual WIKI, all other cases should theoretically be handled by the fallback logic in getPage(), so this means that in this case we should default to the root of all Wikis and show a flash message that we didn't find the wiki (possibly offer to create it?
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