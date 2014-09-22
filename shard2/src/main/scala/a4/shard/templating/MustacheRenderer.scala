package a4.shard.templating

import java.io.{InputStream, StringWriter}

import a4.util.StreamUtil
import com.github.mustachejava.MustacheFactory

case class MustacheRenderer(val mf: MustacheFactory) extends PageRenderer {
  //lazy val mf = new NonCachingMustacheFactory(basePath)
  
  private def toJavaList(scalaList: List[AnyRef]) : java.util.List[Object] = {
    val list = new java.util.ArrayList[AnyRef]()
    for (o <- scalaList) list.add(o)
    return list
  }
  
  private def toJavaMap(scalaMap: Map[String, AnyRef]) : java.util.Map[String, Object] = {
    val map = new java.util.HashMap[String, Object]()
    for ((k, v) <- scalaMap) { 
      if (v.isInstanceOf[Map[String, AnyRef]]) { 
        map.put(k, toJavaMap(v.asInstanceOf[Map[String, AnyRef]]))
      } else if (v.isInstanceOf[List[AnyRef]]) {
        map.put(k, toJavaList(v.asInstanceOf[List[AnyRef]]))
      } else { 
        map.put(k, v)
      }
    }
    return map
  } 
  
  override def renderString(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String = {
    val mustache = mf.compile(template)
    val sw = new StringWriter
    mustache.execute(sw, toJavaMap(context))
    sw.toString
  }

  override def renderStream(template: InputStream, context: Map[String, AnyRef]) : String = {
    renderString(StreamUtil.toString(template, "UTF-8"), context)
  }

}