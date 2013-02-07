package a4.shard

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ShardWikiServlet extends HttpServlet {

  val router= Router(List())
   
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) : Unit = {
    
    
    req.getParts()
    val os = resp.getOutputStream()
    os.print("Hello world")
    os.close()
    resp.setStatus(200)
  }
    
}