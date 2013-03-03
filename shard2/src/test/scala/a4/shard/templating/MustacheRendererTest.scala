package a4.shard.templating

import a4.util.ClasspathAssetResolver
import org.junit.Test
import org.junit.Assert._
import com.github.mustachejava.DefaultMustacheFactory

class MustacheRendererTest {

  val renderer = MustacheRenderer(new DefaultMustacheFactory("mustachetestbase"))
  
  @Test(expected = classOf[RuntimeException])
  def renderNonExistingTemplate : Unit = renderer.render("doesnotexist")
  
  @Test def renderStaticTemplate : Unit = 
    assertEquals("Foo.", renderer.render("static.mustache"))
    
  @Test def renderWithPartial : Unit = 
    assertEquals("Foo\nBar", renderer.render("withpartial.mustache"))
    
}