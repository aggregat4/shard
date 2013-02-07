package shard.server
import cc.spray.test.SprayTest

trait JunitSprayTest extends SprayTest {
  class SprayTestException(message: String) extends RuntimeException(message: String)
  
  def fail(message: String) : Nothing = {
    throw new SprayTestException(message) 
  }
}