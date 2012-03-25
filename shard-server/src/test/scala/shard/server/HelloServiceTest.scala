package shard.server

import cc.spray._
import test._
import http._
import HttpMethods._
import StatusCodes._

import org.junit.Assert._
import org.junit.Test
import org.fest.assertions.Assertions.assertThat

class HelloServiceTest extends SprayTest with HelloService {

  @Test def helloServiceResponds() : Unit = {
    val response = testService(HttpRequest(GET, "/")) {
      helloService
    }.response.content.as[String]
    assertEquals(Right("Say hello to Spray!"), response)
  }

  @Test def helloServiceResponds2() : Unit = {
    assertThat(testService(HttpRequest(GET, "/")) { helloService }.response.content.as[String])
      .isEqualTo(Right("Say hello to Spray!"))
  }

}