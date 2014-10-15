package a4.shard.server

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import a4.shard.routing.Status._
import a4.shard.routing._
import a4.util.StreamUtil

case class ShardWikiServlet(router: Router) extends HttpServlet {

  override def doGet(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) : Unit = {
    val request = new ServletRequest(httpRequest)
    val response = router.findRoute(request) match {
      case Some(r: Route) => r.service(request)
      case None => EmptyResponse(NotFound)
    }
    httpResponse.setStatus(response.status.value)
    for (header <- response.headers) httpResponse.setHeader(header.name, header.value)
    response.contentType match {
      case Some(mediaType) => httpResponse.setContentType(mediaType.toString)
      case None => None
    }
    // TODO proper logging
    println("url: " + request.pathUrl + ", Content-Type: " + response.contentType)
    StreamUtil.copy(response.body, httpResponse.getOutputStream)
  }
}