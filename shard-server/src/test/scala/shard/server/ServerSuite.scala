package shard.server

import org.scalatest.FunSuite
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http.HttpMethod
import com.twitter.util.Future
import com.twitter.util.Duration

class HelloWorldHandler extends Handler {
  def method = HttpMethod.GET
  
  def uri = "/"

  def service = new Service[HttpRequest, HttpResponse]() {
    def apply(request: HttpRequest) = {
      val okResponse = new DefaultHttpResponse(HTTP_1_1, OK)
      okResponse.setContent(copiedBuffer("hello world", UTF_8))
      Future.value(okResponse)
    }
  }
}

class ServerSuite extends FunSuite { 
  test ("Server can answer a request") {
	  val server = HttpServer.build(new Router(List(new HelloWorldHandler())))
	  
	  server.close()
  }
}