package a4.shard.templating

import java.io.{File, InputStream, Reader}

trait PageTemplateRenderer {
  def renderReader(template: Reader, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderFile(template: File, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderStringContent(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderStream(template: InputStream, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}