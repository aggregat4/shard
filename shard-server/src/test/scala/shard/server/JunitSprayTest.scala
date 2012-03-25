package shard.server
import cc.spray.test.SprayTest
import org.junit.Assert

trait JunitSprayTest extends SprayTest {
  class SprayTestException(message: String) extends RuntimeException(message)
  
  def fail(msg: String) : Nothing = {
    throw new SprayTestException(msg)
  }
}