package a4.shard.templating

import com.github.mustachejava.DefaultMustacheFactory
import org.junit.Assert._
import org.junit.Test

class MustacheRendererTest {

  val renderer = MustacheRenderer(new DefaultMustacheFactory("mustachetestbase"))
  
  @Test(expected = classOf[RuntimeException])
  def renderNonExistingTemplate : Unit = renderer.renderString("doesnotexist")
  
  @Test def renderStaticTemplate : Unit = 
    assertEquals("Foo.", renderer.renderString("static.mustache"))
    
  @Test def renderWithPartial : Unit = 
    assertEquals("Foo\nBar", renderer.renderString("withpartial.mustache"))
    
}