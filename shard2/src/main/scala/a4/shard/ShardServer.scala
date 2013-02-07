package a4.shard

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler

object ShardServer {

  def main(args: Array[String]) : Unit = {
    val server = new Server(8080)
    val servletHandler = new ServletHandler()
    servletHandler.addServletWithMapping(classOf[a4.shard.ShardWikiServlet], "/");
    server.setHandler(servletHandler)
    server.start()
  }
  
}