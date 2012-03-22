/**
 * TODO:
 * - Add a simple Service implementation that is a "string" responder
 * - Build a test that can build a server with such a string responder on "/" and verify that it returns it
 */
package shard.server

import com.twitter.finagle.{ Service, SimpleFilter }
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.{ Server, ServerBuilder }
import com.twitter.finagle.http.Http


trait Handler {
  def method: HttpMethod
  def uri: String
  def service: Service[HttpRequest, HttpResponse]
}

/**
 * A URI based Router that will take a http method and the concrete URI of the request and find an appropriate Service to delegate to.
 */
class Router(val routes: List[Handler]) extends Service[HttpRequest, HttpResponse] {
  
  val routeMap = routes map {r => ((r.method, r.uri), r.service) } toMap

  val pageNotFoundService = new Service[HttpRequest, HttpResponse]() {
    val notFoundResponse = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND)
    def apply(request: HttpRequest) = Future.value(notFoundResponse)
  }

  def apply(request: HttpRequest) = {
    routeMap
      .getOrElse((request.getMethod(), request.getUri()), pageNotFoundService)
      .apply(request)
  }
}

object HttpServer {

  /**
   * Copied from twitter example, not sure if useful
   */
  class HandleExceptions extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      // "handle" asynchronously handles exceptions.
      service(request) handle {
        case error =>
          val statusCode = error match {
            case _: IllegalArgumentException => FORBIDDEN
            case _ => INTERNAL_SERVER_ERROR
          }
          val errorResponse = new DefaultHttpResponse(HTTP_1_1, statusCode)
          errorResponse.setContent(copiedBuffer(error.getStackTraceString, UTF_8))
          errorResponse
      }
    }
  }

  def build(service: Service[HttpRequest, HttpResponse]): Server =
    ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(8080))
      .name("httpserver")
      .build(new HandleExceptions andThen service) /*andThen authorize*/

  //  /**
  //   * A simple Filter that checks that the request is valid by inspecting the
  //   * "Authorization" header.
  //   */
  //  class Authorize extends SimpleFilter[HttpRequest, HttpResponse] {
  //    def apply(request: HttpRequest, continue: Service[HttpRequest, HttpResponse]) = {
  //      if ("open sesame" == request.getHeader("Authorization")) {
  //        continue(request)
  //      } else {
  //        Future.exception(new IllegalArgumentException("You don't know the secret"))
  //      }
  //    }
  //  }

  //  /**
  //   * The service itself. Simply echos back "hello world"
  //   */
  //  class Respond extends Service[HttpRequest, HttpResponse] {
  //    def apply(request: HttpRequest) = {
  //
  //      val response = new DefaultHttpResponse(HTTP_1_1, OK)
  //      response.setContent(copiedBuffer("hello world", UTF_8))
  //      Future.value(response)
  //    }
  //  }

}