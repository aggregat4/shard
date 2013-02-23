package a4.shard

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler

/**
 * TODO: 
 * - define a Router() here with the Shard routes
 * - consider with what we service requests, instantiate classes here and use their methods? Go Guice or something similar directly?
 * - once implementing some real "controllers" we'll need parameter conversion (where to model these utilities?) and we'll need some kind of error
 *   handling, possibly as a cross cutting concern, possibly something like filters?
 */
object ShardServer {

  def main(args: Array[String]) : Unit = {
    val server = new Server(8080)
    val servletHandler = new ServletHandler()
    servletHandler.addServletWithMapping(classOf[a4.shard.ShardWikiServlet], "/");
    server.setHandler(servletHandler)
    server.start()
  }
  
}