package a4.shard.templating

import java.io.{File, InputStream, Reader}

trait PageRenderer {
  def renderFile(template: File, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderString(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderReader(template: Reader, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderStream(template: InputStream, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}