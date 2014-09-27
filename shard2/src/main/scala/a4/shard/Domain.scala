package a4.shard

import java.io.File

import a4.util.PathUtil

case class Wiki(id: String, name: String, location: File) {
  def urlPath : String = "/wiki/" + id
}

object Content {
  private def isRoot(name: String) = PathUtil.isEmpty(name.trim) || name.trim == "/"
  
  private def validFile(file: File) : Boolean = file.exists && file.isFile
  private def validDirectory(file: File) : Boolean = file.exists && file.isDirectory
  private def isPage(file: File) : Boolean = validFile(file) && file.getName.endsWith(Page.PAGE_SUFFIX)
  private def isAttachment(file: File) : Boolean = validFile(file) && ! isPage(file)
  private def isFolder(file: File) : Boolean = ! isPage(file) && ! isAttachment(file) && validDirectory(file)
   
  private def determineType(wiki: Wiki, file: File) : Option[Content] =
    if (isPage(file)) Some(Page(wiki, file))
  	else if (isAttachment(file)) Some(Attachment(wiki, file))
  	else if (isFolder(file)) Some(Folder(wiki, file))
  	else None
  
  private def toContent(wiki: Wiki, name: String) : Option[Content] =
    if (isRoot(name)) Some(Folder(wiki, wiki.location))
    else determineType(wiki, new File(wiki.location, name + Page.PAGE_SUFFIX)) match {
      case Some(c) => Some(c)
      case None => determineType(wiki, new File(wiki.location, name))
    }

  def toContentWithFallback(wiki: Wiki, name: String) : Option[Content] = {
    if (isRoot(name)) Content.toContent(wiki, name)
    else Content.toContent(wiki, name) match {
      case Some(page) => Some(page)
      case _ => toContentWithFallback(wiki, PathUtil.parent(name))
    }
  }

}

trait Content {
  def wiki : Wiki
  def file : File
  def parent : Content
  def relativeUrl : String
  def isValid : Boolean
  def url : String = wiki.urlPath + "/" + relativeUrl // TODO: make this a utility method that makes sure we have a slash at the end of the urlpath
  def name : String = relativeUrl // TODO: make this more sensible (what is sensible?)
}

case class Folder(wiki: Wiki, file: File) extends Content {
  private def isRoot : Boolean = file.equals(wiki.location)
  
  override def parent : Content = if (isRoot) this else Folder(wiki, file.getParentFile()) 
  override def relativeUrl : String = file.getName + "/"
  override def isValid : Boolean = file.exists && file.isDirectory

  // TODO possibly reconsider whether I want this logic inside of the Folder class and not outside such as the construction logic in Content
  private def filterFiles[T <: Content](ctor: File => T) : List[T] = file.listFiles.map(ctor).filter(_.isValid).toList
    
  def pages : List[Page] = filterFiles[Page](f => new Page(wiki, f))
  def attachments : List[Attachment] = filterFiles[Attachment](f => new Attachment(wiki, f))  
  def folders : List[Folder] = filterFiles[Folder](f => new Folder(wiki, f))
}

object Page {
  val PAGE_SUFFIX : String = ".shard.md"
}

case class Page(val wiki: Wiki, val file: File) extends Content {  
  private val fileName = file.getName
  private val shortName : String = if (fileName.endsWith(Page.PAGE_SUFFIX)) fileName.substring(0, fileName.length - Page.PAGE_SUFFIX.length) else fileName
  
  override def parent : Content = Folder(wiki, file.getParentFile())
  override def relativeUrl : String = shortName
  override def isValid : Boolean = file.exists && file.isFile && file.getName.endsWith(Page.PAGE_SUFFIX)
}

case class Attachment(val wiki: Wiki, val file: File) extends Content {
  override def parent : Content = Folder(wiki, file.getParentFile)
  override def relativeUrl : String = file.getName
  override def isValid : Boolean = file.exists && file.isFile && ! file.getName.endsWith(Page.PAGE_SUFFIX)
}
