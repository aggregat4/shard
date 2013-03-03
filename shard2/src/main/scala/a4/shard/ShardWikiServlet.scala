package a4.shard

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import a4.shard.routing._
import Status._
import a4.shard.routing.Response
import a4.util.StreamUtil

case class ShardWikiServlet(val router: Router) extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) : Unit = {  
    val request = new ServletRequest(req)
    val response = router.findRoute(request) match {
      case Some(r: Route) => r.service(request)
      case None => EmptyResponse(NotFound)
    }
    resp.setStatus(response.status.value)
    StreamUtil.copy(response.body, resp.getOutputStream)
  }
    
}