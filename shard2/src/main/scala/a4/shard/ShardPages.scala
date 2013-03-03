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

/**
 * Kind of a controller, may need more than one in the future.
 */
case class ShardPages(val config: ShardConfiguration, val pageRenderer: PageRenderer, val assetResolver: AssetResolver, val contentTransformer: PageContentTransformer) {
  private val ROOT_TEMPLATE = "root.mustache"
  private val PAGE_TEMPLATE = "page.mustache"
    
  def root(req: Request) : Response = 
    StringResponse(Ok, pageRenderer.render(ROOT_TEMPLATE, Map("pageTitle" -> "Shard", "wikis" -> config.wikis)))
 
  private def getPage(req: Request) : Option[WikiPage] = 
    for {
      wikiName <- Request.getSingleParam(req, "wiki")
	  wiki <- config.wikiById(wikiName)
	  pageName <- Request.getSingleParam(req, "page").orElse(Some("/root")) // default to the wiki root if no page was specified    
	} yield WikiPage(wiki, pageName)

  // TODO: do special handling for runtimeexceptions on processing the content? In case of non-transformable, just show the quoted/escaped raw source?
  def page(req: Request) : Response = getPage(req) match {
	  case Some(wikiPage) if wikiPage.exists => 
	    StringResponse(Ok, pageRenderer.render(PAGE_TEMPLATE, 
	        Map("pageTitle" -> wikiPage.wiki.name, "pageContent" -> contentTransformer.transform(wikiPage) )))
	  case _ => EmptyResponse(NotFound) // TODO: make the 404 for wiki pages be a page where you can create a new page
  } 
    
  def classpathAsset(req: Request) : Response =
    InputStreamResponse(Ok, assetResolver.getInputStream(req.pathUrl))
    
}