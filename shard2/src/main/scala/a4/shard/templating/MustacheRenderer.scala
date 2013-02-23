package a4.shard.templating

import com.github.mustachejava.DefaultMustacheFactory
import java.io.Writer
import a4.util.AssetResolver

class MustacheRenderer(val assetResolver: AssetResolver) extends PageRenderer {
  lazy val mf = new DefaultMustacheFactory
  
  override def render(template: String, output: Writer, context: Map[String, AnyRef] = Map[String, AnyRef]()) : Unit = {
    val mustache = mf.compile(assetResolver.getReader("templates/" + template), template)
    mustache.execute(output, context)
  }
}