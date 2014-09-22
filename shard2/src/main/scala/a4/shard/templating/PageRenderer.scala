package a4.shard.templating

import java.io.InputStream

trait PageRenderer {
  def renderString(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderStream(template: InputStream, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}