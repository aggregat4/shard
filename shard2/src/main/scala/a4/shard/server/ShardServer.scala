package a4.shard.server

import javax.servlet.MultipartConfigElement

import a4.shard.Configuration
import a4.shard.controller._
import a4.shard.render._
import a4.shard.routing.{GET, POST, Path, Router}
import a4.shard.search.{ElasticSearchFullTextIndex, SearchService}
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
    val searchService = new SearchService(config, new ElasticSearchFullTextIndex())
    // these renderers could also just be static calls on an Object, only if the lifecycle is important (caching?) would this approach maybe make sense
    val pageRenderer = new PageRenderer(contentTransformer)
    val attachmentRenderer = new AttachmentRenderer()
    val folderRenderer = new FolderRenderer()
    val rootRenderer = new RootRenderer()
    val searchResultsRenderer = new SearchResultsRenderer()
    // Controllers
    val assetController = AssetController(config, assetResolver)
    val rootController = RootController(config, rootRenderer)
    val pageController = PageController(config, pageRenderer, folderRenderer, attachmentRenderer)
    val searchController = SearchController(config, searchService, searchResultsRenderer)
    val notFoundController = NotFoundController(config)
    // Router
    val router = Router(
      Path("/").routes((GET, rootController.apply)) :::
	    Path("/wiki/{wiki}/page/{page}").routes((GET, pageController.view), (POST, pageController.edit)) :::
      Path("/wiki/{wiki}/page/").routes((GET, pageController.view)) :::
      Path("/wiki/{wiki}").routes((GET, pageController.view)) :::
      Path("/search?**").routes((GET, searchController.apply)) :::
      Path("/css/**").routes((GET, assetController.apply)) :::
      Path("/js/**").routes((GET, assetController.apply)) :::
      Path("/img/**").routes((GET, assetController.apply)) :::
      Path("/**").routes((GET, notFoundController.apply))
    )
    // Server infrastructure
    val shardWikiServlet = ShardWikiServlet(router)
    val servletHolder = new ServletHolder(shardWikiServlet)
    servletHolder.setInitParameter("cacheControl", "no-cache")
    // make uploads work
    servletHolder.getRegistration.setMultipartConfig(new MultipartConfigElement(System.getProperty("java.io.tmpdir")))
    val servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY)
    servletContextHandler.addServlet(servletHolder, "/*")
    val server = new Server(8080)
    server.setHandler(servletContextHandler)
    server.start()
    // "background processes"
    searchService.startIndexing()
  }

}