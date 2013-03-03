package a4.shard.templating

import java.io.Writer

trait PageRenderer {
  def render(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}