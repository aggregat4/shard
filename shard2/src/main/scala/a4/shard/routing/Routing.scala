/**
 * TODO:
 * - requests can have parameters, they can be parsed from the URI (path fragments or query params) or they can be submitted form encoded
 * - requests can have a body
 * - requests can have an accept header (and other HTTP stuff that we should not all model at the same time)
 * - responses can have a body, this should probably be a Stream (if it is easy enough to use)
 */
package a4.shard.routing

import a4.shard.routing._
import Status._
import Stream._
import javax.servlet.http.HttpServletRequest
import java.io.OutputStream
import java.io.InputStream
import java.io.ByteArrayInputStream

sealed trait Method
case object GET extends Method
case object POST extends Method
case object PUT extends Method
case object DELETE extends Method

object Method {
  def from(method: String) = method match {
    case "GET" => GET
    case "POST" => POST
    case "PUT" => PUT
    case "DELETE" => DELETE
    case _ => throw new UnknownMethodException(method)
  }
}

trait Request {
  def method: Method
  def pathUrl: String
  def params: Map[String, List[String]]
}

/**
 * Current design decision is to provide my own Request type but at the moment to just wrap the 
 * original HttpServletRequest and dispatch all calls. I am probably going to need a bunch of 
 * the stuff in HttpServletRequest at the end but for now it seems weird leaving raw Java 
 * classes in the API.
 */
class ServletRequest(val originalRequest: HttpServletRequest) extends Request {  
  import collection.JavaConversions._
  implicit def convert(javaMap: java.util.Map[String, Array[String]]) = javaMap.entrySet.map(entry => (entry.getKey, entry.getValue.toList)).toMap
  lazy val originalParameters : Map[String, List[String]] = originalRequest.getParameterMap()
  
  override def method = Method.from(originalRequest.getMethod())
  override def pathUrl = originalRequest.getContextPath()
  override def params = originalParameters
}

case class Status(val value: Int)

object Status {
  def Ok = Status(200)
  def NotFound = Status(404)
  def Error = Status(500)
}

trait Response {
  def status : Status
  def body : InputStream
}

case class EmptyResponse(val status: Status) extends Response {
  lazy val body = new ByteArrayInputStream(Array[Byte]())
}

case class StringResponse(val status: Status, val stringBody: String) extends Response {
  lazy val body = new ByteArrayInputStream(stringBody.getBytes("UTF-8"))
}

case class Route(val path: Path, val method: Method, val action: Action) {
  // A simple first attempt, no variable support yet, static matching on URL path
  def accepts(request: Request) = (request.pathUrl == path.path && request.method == method)
  def service(request: Request) = action.apply(request)

}
case class Path(val path: String) {
  def routes(actions: (Method, Action)*) = actions.map { action: (Method, Action) => Route(this, action._1, action._2) }.toList
}

case class Router(val routes: List[Route]) {
  def findRoute(request: Request) = routes.find(r => r.accepts(request))
}

// ---- Some test classes and methods
object SomeControllerLikeThing {
  def doSomething(req: Request) = EmptyResponse(Ok)
}

class Foo {
  def bar(req: Request) = EmptyResponse(NotFound)
}

object Router {
  val routes =
    Path("/").routes(
      (GET, SomeControllerLikeThing.doSomething),
      (POST, new Foo().bar)) :::
    Path("/foo").routes(
      (GET, SomeControllerLikeThing.doSomething))
}