package a4.shard.templating

import com.github.mustachejava.DefaultMustacheFactory
import org.junit.Assert._
import org.junit.Test

class MustacheRendererTest {

  val renderer = MustacheTemplateRenderer(new DefaultMustacheFactory("mustachetestbase"))
  
  @Test(expected = classOf[RuntimeException])
  def renderNonExistingTemplate : Unit = renderer.renderByContent("doesnotexist")
  
  @Test def renderStaticTemplate : Unit = 
    assertEquals("Foo.", renderer.renderByContent("static.mustache"))
    
  @Test def renderWithPartial : Unit = 
    assertEquals("Foo\nBar", renderer.renderByContent("withpartial.mustache"))
    
}