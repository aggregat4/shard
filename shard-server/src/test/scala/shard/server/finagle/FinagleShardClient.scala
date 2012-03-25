package shard.server

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import java.net.InetSocketAddress
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

object FinagleHttpClient {
  class InvalidRequest extends Exception

  class HandleErrors extends SimpleFilter[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      // flatMap asynchronously responds to requests and can "map" them to both
      // success and failure values:
      service(request) flatMap { response =>
        response.getStatus match {
          case OK        => Future.value(response)
          case FORBIDDEN => Future.exception(new InvalidRequest)
          case _         => Future.exception(new Exception(response.getStatus.getReasonPhrase))
        }
      }
    }
  }

  def clientWithoutErrorHandling() : Service[HttpRequest, HttpResponse] = {
    return ClientBuilder()
      .codec(Http())
      .hosts(new InetSocketAddress(8080))
      .hostConnectionLimit(1)
      .build()
  }
  
  def clientWithErrorHandling() : Service[HttpRequest, HttpResponse] = {
    return new HandleErrors andThen clientWithoutErrorHandling
  }
  
}