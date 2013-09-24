package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.util.PathUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer

case class Wiki(val id: String, val name: String, val location: File) {
  def urlPath : String = "/wiki/" + id
}

object Content {
  private def isRoot(name: String) = PathUtil.isEmpty(name.trim) || name.trim == "/"
  
  def validFile(file: File) : Boolean = file.exists && file.isFile 
  def validDirectory(file: File) : Boolean = file.exists && file.isDirectory
  def isPage(file: File) : Boolean = validFile(file) && file.getName.endsWith(Page.PAGE_SUFFIX)
  def isAttachment(file: File) : Boolean = validFile(file) && ! isPage(file)
  def isFolder(file: File) : Boolean = ! isPage(file) && ! isAttachment(file) && validDirectory(file) 
   
  def determineType(wiki: Wiki, file: File) : Option[Content] = 
    if (isPage(file)) Some(Page(wiki, file))
  	else if (isAttachment(file)) Some(Attachment(wiki, file))
  	else if (isFolder(file)) Some(Folder(wiki, file))
  	else None
  
  def toContent(wiki: Wiki, name: String) : Option[Content] =
    if (isRoot(name)) Some(Folder(wiki, wiki.location))
    else determineType(wiki, new File(wiki.location, name + Page.PAGE_SUFFIX)) match {
      case Some(c) => Some(c)
      case None => determineType(wiki, new File(wiki.location, name)) 
    }
}

trait Content {
  def wiki : Wiki
  def file : File
  def parent : Content
  def relativeUrl : String
  def exists : Boolean  
}

case class Folder(val wiki: Wiki, val file: File) extends Content {
  private def isRoot : Boolean = file.equals(wiki.location)
  
  override def parent : Content = if (isRoot) this else Folder(wiki, file.getParentFile()) 
  override def relativeUrl : String = file.getName + "/"
  override def exists : Boolean = file.exists && file.isDirectory
  
  // TODO possibly reconsider whether I want this logic inside of the Folder class and not outside such as the construction logic in Content
  private def filterFiles[T <: Content](ctor: File => T) : List[T] = file.listFiles.map(ctor).filter(_.exists).toList
    
  def pages : List[Page] 			 = filterFiles[Page](f => new Page(wiki, f)) 
  def attachments : List[Attachment] = filterFiles[Attachment](f => new Attachment(wiki, f))  
  def folders : List[Folder] 		 = filterFiles[Folder](f => new Folder(wiki, f)) 
}

object Page {
  val PAGE_SUFFIX : String = ".shard.md"
}

case class Page(val wiki: Wiki, val file: File) extends Content {  
  private val name = file.getName 
  private val shortName : String = if (name.endsWith(Page.PAGE_SUFFIX)) name.substring(0, name.length - Page.PAGE_SUFFIX.length) else name
  
  override def parent : Content = Folder(wiki, file.getParentFile())
  override def relativeUrl : String = shortName
  override def exists : Boolean = file.exists && file.isFile 
}

case class Attachment(val wiki: Wiki, val file: File) extends Content {
  override def parent : Content = Folder(wiki, file.getParentFile)
  override def relativeUrl : String = file.getName
  override def exists : Boolean = file.exists && file.isFile 
}
