package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.util.PathUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer

case class Wiki(val id: String, val name: String, val location: File) {
  def urlPath : String = "/wiki/" + id
}

/**
 * TODO New idea: have a ContentHypothesis class taking a wiki and a relative path. 
 * It has a method test() that returns Either a Content or an Error (look Either up).
 * This class has the logic for mapping a relative URL to an actual object. The Content 
 * subclasses then become simpler since they would take a wiki and a 
 * File object as input and would assume that they are valid.
 */

object Content {
  def isRoot(name: String) = PathUtil.isEmpty(name.trim) || name.trim == "/"
  def toContent(wiki: Wiki, name: String) : Content = {
    if (isRoot(name)) Folder(wiki)
  }
}

trait Content {
  def wiki : Wiki
  def parent : Content
  def relativeUrl : String
  def name : String
  def file : File
//  def exists : Boolean  
}

//case class Root(val wiki: Wiki) extends Content {
//  override def relativeUrl : String = ""
//  override def name : String = ""
//  override def file : File = wiki.location
//  override def parent : Content = this // not sure about this, seems too clever, what else though? if I return Option then I need special casing in subclasses
//  override def exists : Boolean = true
//}

case class Folder(val wiki: Wiki, val file: File) extends Content {
  override def parent : Content = if (file.equals(wiki.location)) this else Folder(wiki, file.getParentFile()) 
  override def relativeUrl : String = name + "/"
//  override def exists : Boolean = file.exists && file.isDirectory
  
  private def filterFiles[T <: Content](ctor: File => T) : List[T] = file.listFiles.map(ctor).filter(_.exists).toList
    
  def pages : List[Page] 			 = filterFiles[Page](f => new Page(wiki, f)) 
  def attachments : List[Attachment] = filterFiles[Attachment](f => new Attachment(wiki, f))  
  def folders : List[Folder] 		 = filterFiles[Folder](f => new Folder(wiki, f)) 
}

case class Page(val wiki: Wiki, val name: String) extends Content {
  private val PAGE_SUFFIX : String = ".shard.md"
  private val shortName : String = if (name.endsWith(PAGE_SUFFIX)) name.substring(0, name.length - PAGE_SUFFIX.length) else name
  private val fullName : String = if (name.endsWith(PAGE_SUFFIX)) name else name + PAGE_SUFFIX
  
  override def parent : Content = Folder(wiki, PathUtil.parent(name))
  override def relativeUrl : String = shortName
  override def file : File = if (isRoot) wiki.location else new File(wiki.location, fullName)
//  override def exists : Boolean = file.exists && file.isFile 
//
//  def content : String = FileUtil.readAsUtf8(file)
}

case class Attachment(val wiki: Wiki, val name: String) extends Content {
  override def parent : Content = Folder(wiki, PathUtil.parent(name))
  override def relativeUrl : String = name
  override def file : File = if (isRoot) wiki.location else new File(wiki.location, name)
//  override def exists : Boolean = file.exists && file.isFile 
}

//case class RootPage(val wikis: List[Wiki]) {
//  private val template = "root.mustache"
//  def render(pageRenderer: PageRenderer) = pageRenderer.render(template, Map("pageTitle" -> "Shard", "wikis" -> wikis))
//}