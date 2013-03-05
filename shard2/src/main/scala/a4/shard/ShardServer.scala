package a4.shard

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.eclipse.jetty.servlet.ServletHolder
import com.typesafe.config.ConfigFactory
import a4.shard.routing.GET
import a4.shard.routing.Path
import a4.shard.routing.Router
import a4.shard.templating.MustacheRenderer
import a4.util.ClasspathAssetResolver
import com.github.mustachejava.NonCachingMustacheFactory
import a4.shard.transforming.MarkdownTransformer

/**
 * TODO: 
 * - consider with what we service requests, instantiate classes here and use their methods? Go Guice or something similar directly?
 * - once implementing some real "controllers" we'll need parameter conversion (where to model these utilities?) and we'll need some kind of error
 *   handling, possibly as a cross cutting concern, possibly something like filters?
 */
object ShardServer {

  def shardRouter(pages: ShardPages) : Router = 
    Router(
      Path("/").routes((GET, pages.root)) :::
      Path("/wiki/{wiki}/page/{page}").routes((GET, pages.page)) :::
      Path("/wiki/{wiki}").routes((GET, pages.page)) :::
      Path("/css/**").routes((GET, pages.classpathAsset)) :::
      Path("/js/**").routes((GET, pages.classpathAsset)) :::
      Path("/img/**").routes((GET, pages.classpathAsset))
    )
  
  def main(args: Array[String]) : Unit = {
    val appConfig = ConfigFactory.load
    // DI starts here
    val config = ShardConfiguration(appConfig)
    val templateRenderer = MustacheRenderer(new NonCachingMustacheFactory("assets/templates"))
    val assetResolver = ClasspathAssetResolver("assets")
    val contentTransformer = MarkdownTransformer()
    val shardPages = ShardPages(config, templateRenderer, assetResolver, contentTransformer)
    val shardWikiServlet = ShardWikiServlet(shardRouter(shardPages))
    // Server infrastructure
    val server = new Server(8080)
    val servletHandler = new ServletHandler()
    servletHandler.addServletWithMapping(new ServletHolder(shardWikiServlet), "/*");
    server.setHandler(servletHandler)
    server.start()
  }
  
}