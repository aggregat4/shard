package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.util.PathUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer


case class Wiki(val id: String, val name: String, val location: String) {
  def urlPath : String = "/wiki/" + id
}


trait Content {
  def wiki : Wiki
  def relativeUrl : String
  def name : String
  def file : File
  def parent : Content
  def exists : Boolean
}

case class Root(val wiki: Wiki) extends Content {
  override def relativeUrl : String = ""
  override def name : String = ""
  override def file : File = new File(wiki.location)
  override def parent : Content = this // not sure about this, seems too clever, what else though? if I return Option then I need special casing in subclasses
  override def exists : Boolean = true
}

case class Folder(val wiki: Wiki, val name: String, val parent: Folder) extends Content {
  override def file : File = new File(parent.file, name)
  override def relativeUrl : String = parent.relativeUrl + name + "/"
  override def exists : Boolean = file.exists && file.isDirectory

  private def filterFiles[T <: Content](ctor: File => T) : List[T] = file.listFiles.map(ctor).filter(_.exists).toList
    
  def pages : List[Page] = filterFiles[Page](f => new Page(wiki, f.getName, this)) 
  def attachments : List[Attachment] = filterFiles[Attachment](f => new Attachment(wiki, f.getName, this))  
  def folders : List[Folder] = filterFiles[Folder](f => new Folder(wiki, f.getName, this)) 
}

case class Page(val wiki: Wiki, val name: String, val parent: Folder) extends Content {
  private val PAGE_SUFFIX : String = ".shard.md"
  private val shortName : String = if (name.endsWith(PAGE_SUFFIX)) name.substring(0, name.length - PAGE_SUFFIX.length) else name
  
  override def file : File = new File(parent.file, name)
  override def relativeUrl : String = parent.relativeUrl + shortName
//
//  def content : String = FileUtil.readAsUtf8(file)
}

case class Attachment(val wiki: Wiki, val name: String, val parent: Folder) extends Content {
  override def file = new File(parent.file, name)
  override def relativeUrl : String = parent.relativeUrl + name
}

//case class RootPage(val wikis: List[Wiki]) {
//  private val template = "root.mustache"
//  def render(pageRenderer: PageRenderer) = pageRenderer.render(template, Map("pageTitle" -> "Shard", "wikis" -> wikis))
//}