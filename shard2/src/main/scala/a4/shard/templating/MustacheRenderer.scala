package a4.shard.templating

import com.github.mustachejava.DefaultMustacheFactory
import java.io.Writer
import a4.util.AssetResolver
import java.io.StringWriter
import com.github.mustachejava.NonCachingMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.apache.commons.io.FileUtils
import a4.util.StreamUtil
import java.io.InputStream

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
  
  override def render(template: String, context: Map[String, AnyRef] = Map[String, AnyRef]()) : String = {
    val mustache = mf.compile(template)
    val sw = new StringWriter
    mustache.execute(sw, toJavaMap(context))
    sw.toString
  }

  override def render(template: InputStream, context: Map[String, AnyRef]) : String = {
    render(StreamUtil.toString(template, "UTF-8"), context)
  }

}