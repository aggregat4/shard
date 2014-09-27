package a4.shard.templating

import java.io.{File, InputStream, Reader}

trait PageTemplateRenderer {
  def renderFile(template: File, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderByContent(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderByName(templateName: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderReader(template: Reader, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
  def renderStream(template: InputStream, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String
}