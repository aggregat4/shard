package a4.shard.server

import a4.shard.Configuration
import a4.shard.controller.{Asset, Page, Root}
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
 * - consider with what we service requests, instantiate classes here and use their methods? Go Guice or something similar directly?
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
    // Controllers
    val assetController = Asset(config, templateRenderer, assetResolver)
    val rootController = Root(config, templateRenderer, assetResolver)
    val pageController = Page(config, templateRenderer, assetResolver, contentTransformer)
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