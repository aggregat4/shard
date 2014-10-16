package a4.shard.server

import javax.servlet.MultipartConfigElement

import a4.shard.Configuration
import a4.shard.controller.{AssetController, PageController, RootController}
import a4.shard.render.CodeContentRenderer
import a4.shard.routing.{GET, POST, Path, Router}
import a4.shard.transforming.MarkdownTransformer
import a4.util.ClasspathAssetResolver
import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

object ShardServer {

  def main(args: Array[String]) : Unit = {
    // DI starts here
    val appConfig = ConfigFactory.load
    val config = Configuration(appConfig)
    val assetResolver = ClasspathAssetResolver("assets")
    val contentTransformer = MarkdownTransformer()
    val contentRenderer = new CodeContentRenderer(contentTransformer)
    // Controllers
    val assetController = AssetController(config, assetResolver)
    val rootController = RootController(config, contentRenderer)
    val pageController = PageController(config, contentRenderer)
    // Router
    val router = Router(
      Path("/").routes((GET, rootController.apply)) :::
	    Path("/wiki/{wiki}/page/{page}").routes((GET, pageController.view), (POST, pageController.edit)) :::
      Path("/wiki/{wiki}/page/").routes((GET, pageController.view)) :::
      Path("/wiki/{wiki}").routes((GET, pageController.view)) :::
      Path("/css/**").routes((GET, assetController.apply)) :::
      Path("/js/**").routes((GET, assetController.apply)) :::
      Path("/img/**").routes((GET, assetController.apply))
    )
    // Server infrastructure
    val shardWikiServlet = ShardWikiServlet(router)
    val servletHolder = new ServletHolder(shardWikiServlet)
    servletHolder.setInitParameter("cacheControl", "no-cache")
    servletHolder.getRegistration.setMultipartConfig(new MultipartConfigElement(System.getProperty("java.io.tmpdir")))
    val servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY)
    servletContextHandler.addServlet(servletHolder, "/*")
    val server = new Server(8080)
    server.setHandler(servletContextHandler)
    server.start()
  }
  
}