package a4.shard

import java.io.File

import a4.util.PathUtil

case class Wiki(id: String, name: String, location: File) {
  def urlPath : String = "/wiki/" + id
}

object Content {
  private def isRoot(name: String) = PathUtil.isEmpty(name.trim) || name.trim == "/"
  
  def validFile(file: File) : Boolean = file.exists && file.isFile
  def validDirectory(file: File) : Boolean = file.exists && file.isDirectory

  private def determineType(wiki: Wiki, file: File) : Option[Content] =
    if (Page.isPage(file)) Some(Page(wiki, file))
  	else if (Attachment.isAttachment(file)) Some(Attachment(wiki, file))
  	else if (Folder.isFolder(file)) Some(Folder(wiki, file))
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
}

object Folder {
  def isFolder(file: File) : Boolean = Content.validDirectory(file)
}

case class Folder(wiki: Wiki, file: File) extends Content {
  private def isRoot : Boolean = file.equals(wiki.location)
  
  override def parent : Folder = if (isRoot) this else Folder(wiki, file.getParentFile)
  override def relativeUrl : String = if (isRoot) "" else file.getName + "/"
  override def isValid : Boolean = Folder.isFolder(file)

  // TODO possibly reconsider whether I want this logic inside of the Folder class and not outside such as the construction logic in Content
  private def filterFiles[T <: Content](ctor: File => T) : List[T] = file.listFiles.map(ctor).filter(_.isValid).toList
    
  def pages : List[Page] = filterFiles[Page](f => new Page(wiki, f))
  def attachments : List[Attachment] = filterFiles[Attachment](f => new Attachment(wiki, f))  
  def folders : List[Folder] = filterFiles[Folder](f => new Folder(wiki, f))
}

object Page {
  val PAGE_SUFFIX : String = ".shard.md"
  def isPage(file: File) : Boolean = Content.validFile(file) && file.getName.endsWith(Page.PAGE_SUFFIX)
}

case class Page(wiki: Wiki, file: File) extends Content {
  private val fileName = file.getName
  private val shortName : String = if (fileName.endsWith(Page.PAGE_SUFFIX)) fileName.substring(0, fileName.length - Page.PAGE_SUFFIX.length) else fileName
  
  override def parent : Folder = Folder(wiki, file.getParentFile)
  override def relativeUrl : String = parent.relativeUrl + shortName
  override def isValid : Boolean = Page.isPage(file)
}

object Attachment {
  def isAttachment(file: File) : Boolean = Content.validFile(file) && ! Page.isPage(file)
}

case class Attachment(wiki: Wiki, file: File) extends Content {
  override def parent : Folder = Folder(wiki, file.getParentFile)
  override def relativeUrl : String = parent.relativeUrl + file.getName
  override def isValid : Boolean = Attachment.isAttachment(file)
}
