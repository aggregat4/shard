package a4.shard.templating

import java.io.Writer

trait PageRenderer {
  def render(template: String, output: Writer, context: Map[String, AnyRef] = Map[String, AnyRef]()) : Unit
}