package shard.server

import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.util.CharsetUtil.UTF_8

import com.twitter.finagle.Service
import com.twitter.util.Future

import org.junit.Test
import org.junit.Assert._

class FinagleHttpServerTest {
  val helloWorldMessage = "Hello, world."
  val helloWorldRouter = new Router(List(new HelloWorldHandler())) 

  class HelloWorldHandler extends Handler {
    val method = HttpMethod.GET
    val uri = "/"

    def service = new Service[HttpRequest, HttpResponse]() {
      val okResponse = new DefaultHttpResponse(HTTP_1_1, OK)
      def apply(request: HttpRequest) = {
        Future.value(okResponse)
      }
    }
  }

  private[this] def aGETRequest(uri: String) = new DefaultHttpRequest(HTTP_1_1, HttpMethod.GET, uri)
  
  private[this] def toString(response: HttpResponse) = response.getContent.toString(UTF_8)
  
  private[this] def GET(uri: String, service: Service[HttpRequest, HttpResponse]) : (HttpResponseStatus, String) = {
    val server = FinagleHttpServer.build(service)
    try {
      val client = FinagleHttpClient.clientWithErrorHandling()
      val response: HttpResponse = client(aGETRequest(uri)).get
      return (response.getStatus(), toString(response))
    } finally {
      server.close()
    }    
  }
  
//  @Test def helloWorldServerAnswersGetRequestToRoot: Unit = {
//    assertEquals((OK, helloWorldMessage), GET("/", helloWorldRouter))    
//  }

  @Test def helloWorldserverFailsToAnswerToWrongPath: Unit = {
    assertEquals((NOT_FOUND, ""), GET("/foo", helloWorldRouter))
  }
  
  
}
