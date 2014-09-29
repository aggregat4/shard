package a4.shard.server

import a4.shard.Configuration
import a4.shard.controller.{AssetController, PageController, RootController}
import a4.shard.render.CodeContentRenderer
import a4.shard.routing.{GET, Path, Router}
import a4.shard.templating.MustacheTemplateRenderer
import a4.shard.transforming.MarkdownTransformer
import a4.util.ClasspathAssetResolver
import com.github.mustachejava.NonCachingMustacheFactory
import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

/**
 * TODO: 
 * - once implementing some real "controllers" we'll need parameter conversion (where to model these utilities?) and we'll need some kind of error
 *   handling, possibly as a cross cutting concern, possibly something like filters?
 */
object ShardServer {

  def main(args: Array[String]) : Unit = {
    // DI starts here
    val appConfig = ConfigFactory.load
    val config = Configuration(appConfig)
    val templateRenderer = MustacheTemplateRenderer(new NonCachingMustacheFactory("assets/templates"))
    val assetResolver = ClasspathAssetResolver("assets")
    val contentTransformer = MarkdownTransformer()
    val contentRenderer = new CodeContentRenderer(contentTransformer)
//    val contentRenderer = new TemplateContentRenderer(templateRenderer, assetResolver, contentTransformer)
    // Controllers
    val assetController = AssetController(config, assetResolver)
    val rootController = RootController(config, contentRenderer)
    val pageController = PageController(config, contentRenderer)
    // Router
    val router = Router(
      Path("/").routes((GET, rootController.apply)) :::
	    Path("/wiki/{wiki}/page/{page}").routes((GET, pageController.apply)) :::
      Path("/wiki/{wiki}/page/").routes((GET, pageController.apply)) :::
      Path("/wiki/{wiki}").routes((GET, pageController.apply)) :::
      Path("/css/**").routes((GET, assetController.apply)) :::
      Path("/js/**").routes((GET, assetController.apply)) :::
      Path("/img/**").routes((GET, assetController.apply))
    )
    // Server infrastructure
    val shardWikiServlet = ShardWikiServlet(router)
    val server = new Server(8080)
    val servletHandler = new ServletHandler()
    servletHandler.addServletWithMapping(new ServletHolder(shardWikiServlet), "/*")
    server.setHandler(servletHandler)
    server.start()
  }
  
}