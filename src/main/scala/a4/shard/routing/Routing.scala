/**
 * TODO:
 * - requests can have parameters, they can be parsed from the URI (path fragments or query params) or they can be submitted form encoded
 * - requests can have a body
 * - requests can have an accept header (and other HTTP stuff that we should not all model at the same time)
 * - responses can have a body, this should probably be a Stream (if it is easy enough to use)
 */
package a4.shard.routing

import java.io.{ByteArrayInputStream, InputStream}
import javax.servlet.http.HttpServletRequest

import com.google.common.net.MediaType

import scala.util.matching.Regex

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

trait Part {
  def inputStream : InputStream
  def contentType : String
  def name : String
  def size : Long
  // a Part can also have headers, haven't mapped that yet
}

trait Request {
  def method: Method
  def pathUrl: String
  def params: Map[String, List[String]]  
  def withPathParams(urlParams: Map[String, List[String]]) : Request
  def formData : List[Part] = List()
}

object Request {
  def getSingleParam(req: Request, name: String) : Option[String] = req.params.get(name) match {
    case Some(list) if list.nonEmpty => Some(list(0))
    case _ => None
  }
}


/**
 * Current design decision is to provide my own Request type but at the moment to just wrap the 
 * original HttpServletRequest and dispatch all calls. I am probably going to need a bunch of 
 * the stuff in HttpServletRequest at the end but for now it seems weird leaving raw Java 
 * classes in the API.
 */

class ServletPart(val originalPart: javax.servlet.http.Part) extends Part {
  def inputStream = originalPart.getInputStream
  def contentType = originalPart.getContentType
  def name = originalPart.getName
  def size = originalPart.getSize
}

class ServletRequest(val originalRequest: HttpServletRequest, val urlParams: Map[String, List[String]] = Map()) extends Request {
  import scala.collection.JavaConversions._
  implicit def convert(javaMap: java.util.Map[String, Array[String]]) = javaMap.entrySet.map(entry => (entry.getKey, entry.getValue.toList)).toMap
  
  lazy val params : Map[String, List[String]] = originalRequest.getParameterMap ++ urlParams
  override def method = Method.from(originalRequest.getMethod)
  override def pathUrl = originalRequest.getPathInfo + (if (originalRequest.getQueryString == null) "" else "?" + originalRequest.getQueryString)
  override def withPathParams(urlParams: Map[String, List[String]]) : Request = new ServletRequest(originalRequest, urlParams)
  override lazy val formData : List[Part] = originalRequest.getParts.map(p => new ServletPart(p)).toList
}

case class Status(value: Int)

object Status {
  def Ok = Status(200)
  def SeeOther = Status(303)
  def BadRequest = Status(400)
  def NotFound = Status(404)
  def Error = Status(500)
}

case class Header(name: String, value: String)

trait Response {
  def status : Status
  def body : InputStream
  def contentType : Option[MediaType]
  def headers : List[Header] = List()
}

case class EmptyResponse(status: Status, contentType: Option[MediaType] = None) extends Response {
  lazy val body = new ByteArrayInputStream(Array[Byte]())
}

case class RedirectResponse(status: Status, location: String) extends Response {
  lazy val body = new ByteArrayInputStream(Array[Byte]())
  override val contentType = None
  override val headers = List(Header("Location", location))
}

case class StringResponse(status: Status, stringBody: String, contentType: Option[MediaType] = None) extends Response {
  lazy val body = new ByteArrayInputStream(stringBody.getBytes("UTF-8"))
}

case class InputStreamResponse(status: Status, body: InputStream, contentType: Option[MediaType] = None) extends Response

case class Route(path: Path, method: Method, action: Action) {
  
  def accepts(request: Request) : Boolean = request.method == method && path.matches(request.pathUrl)

  /**
   *  If any variables were defined in the path of the URL itself we need to make sure 
   *  to pass them on to the Request object so that the callee can use them.
   */ 
  def service(request: Request) = 
    action.apply(request.withPathParams(
      path.getVariables(request.pathUrl).toIterable.map( t => (t._1, List(t._2)) ).toMap 
    ))
}

case class Path(path: String) {
  private val variableRegexPattern = "\\{([a-zA-Z]+)\\}" 
  // Note that for the actual variable *values* we also match on forward slashes, this means that
  // URL variables can also be path fragments, e.g. "foo/bar/baz", we match on periods since they can occur in filenames
  private val valueRegexPattern = "(.+)" // Used to have a constrained pattern here: "a-zA-Z/\.\-", I wonder whether I'll get trouble with this globbing pattern?
  private val variableRegex = variableRegexPattern.r
  val variableNames = variableRegex.findAllIn(path).matchData.map(group => group.group(1)).toList
  // ": _*" seems to be a way to convert a List to a varargs. Magic. Fucking scala.
  val pathMatcher = new Regex(path.replace("**", ".*").replaceAll(variableRegexPattern, valueRegexPattern), variableNames : _*)
  
  def matches(url: String) : Boolean = pathMatcher.findFirstIn(url) match {    
  	case Some(urlMatch) => urlMatch.equals(url) // only full matches are matches
    case None => false
  } 
    
  def getVariables(url: String) : Map[String, String] = pathMatcher.findFirstMatchIn(url) match {
    case Some(m) => variableNames.zip(m.subgroups).toMap
    case _ => Map()
  }
  
  // this is like a factory method, should be on the object?
  def routes(actions: (Method, Action)*) : List[Route] = actions.map { action: (Method, Action) => Route(this, action._1, action._2) }.toList
}

case class Router(routes: List[Route]) {
  def findRoute(request: Request) = routes.find(r => r.accepts(request))
}