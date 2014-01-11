package a4.shard.templating

import java.io.Writer
import java.io.InputStream

trait PageRenderer {
  def render(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def render(template: InputStream, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}