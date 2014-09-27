package a4.shard.templating

import java.io._
import java.nio.charset.Charset

import a4.util.FileUtil
import com.github.mustachejava.MustacheFactory

case class MustacheTemplateRenderer(mf: MustacheFactory) extends PageTemplateRenderer {

  private def toJavaList(scalaList: List[AnyRef]) : java.util.List[Object] = {
    val list = new java.util.ArrayList[AnyRef]()
    for (o <- scalaList) list.add(o)
    return list
  }
  
  private def toJavaMap(scalaMap: Map[String, AnyRef]) : java.util.Map[String, Object] = {
    val map = new java.util.HashMap[String, Object]()
    for ((k, v) <- scalaMap) {
      v match {
        case _: Map[String, AnyRef] => map.put(k, toJavaMap(v.asInstanceOf[Map[String, AnyRef]]))
        case _: List[AnyRef] => map.put(k, toJavaList(v.asInstanceOf[List[AnyRef]]))
        case _ => map.put(k, v)
      }
    }
    return map
  } 

  // Josh Suereth's Automatic Resource Management Library
  import resource._

  override def renderReader(template: Reader, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String = {
    val mustache = mf.compile(template, "")
    val sw = new StringWriter
    mustache.execute(sw, toJavaMap(context))
    sw.toString
  }

  override def renderStringContent(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String = {
    managed(new StringReader(template)).acquireAndGet( sr => renderReader(sr, context))
  }

  override def renderStream(template: InputStream, context: Map[String, AnyRef]) : String = {
    managed(new InputStreamReader(template, Charset.forName("UTF-8"))).acquireAndGet(isr => renderReader(isr, context))
  }

  override def renderFile(template: File, context: Map[String, AnyRef]): String = {
    managed(FileUtil.toUtf8Reader(template)).acquireAndGet(fr => renderReader(fr, context))
  }

}