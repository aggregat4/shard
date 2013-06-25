package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.util.PathUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer

/**
 * TODO: 
 * Refactoring domain model to be purely structural: what is the tree like, what content does it have, what metadata
 * Rendering is completely outside of this. Cover this with unit tests
 */

case class Wiki(val id: String, val name: String, val location: String) {
  def urlPath : String = "/wiki/" + id
}


trait Content {
  def wiki : Wiki
  def relativeUrl : String
  def name : String
  def exists : Boolean // don't like this here
  def parent : Option[Content]
}

case class Folder(val wiki: Wiki, val folderUrl: String) extends Content {
  private val folderFile = new File(wiki.location, folderUrl)
  private def isRoot = folderUrl == "/"
  override def relativeUrl : String = wiki.urlPath + folderUrl
  override def name : String = if (isRoot) folderUrl else PathUtil.name(folderUrl) 
  override def exists : Boolean = folderFile.exists && folderFile.isDirectory
  override def parent : Option[Folder] = if (isRoot) None else Some(new Folder(wiki, PathUtil.parent(folderUrl))) 
  
  def pages : List[Page] = 
    folderFile
  		.listFiles
  		.filter(f => f.isFile && f.getName.endsWith(".shard.md"))
  		.map(f => new Page(wiki, PathUtil.toConcretePath(folderUrl, f.getName.substring(0, f.getName.length - ".shard.md".length))))
  		.toList
  	
//  def files : List[WikiFile] = List()
  		
  		
// I need a Page type for attachments
//    folderFile.listFiles
//  	.filter(f => f.isFile && ! f.getName.endsWith(".shard.md"))
//  	.map(f => Addressable(f.getName))
  
  def folders : List[Folder] = 
    (folderFile
    	.listFiles
    	.filter(f => f.isDirectory())
    	.map(f => new Folder(wiki, PathUtil.toFolderPath(folderUrl, f.getName))))
    .toList
}

case class Page(val wiki: Wiki, val pageUrl: String) extends Content {
  private val pageSuffix = ".shard.md"
  private val pageFile = new File(wiki.location, pageUrl + pageSuffix)
  
  override def relativeUrl : String = wiki.urlPath + pageUrl
  override def name : String = PathUtil.name(pageUrl) 
  override def exists : Boolean = pageFile.exists && pageFile.isFile
  override def parent : Option[Folder] = Some(new Folder(wiki, PathUtil.parent(pageUrl)))

  def content : String = FileUtil.readAsUtf8(pageFile)
}

//
//case class Attachment extends Content {
//  // TODO
//}

//case class ConcretePage(val wiki: Wiki, val page: String, val contentTransformer: PageContentTransformer) extends WikiPage {
//  private val template = "page.mustache"
//   
//  def render(pageRenderer: PageRenderer) = 
//    pageRenderer.render(template, Map(
//      "pageTitle" -> wiki.name, 
//      "wikiPage" -> this, 
//      "pageContent" -> contentTransformer.transform(this, content),
//      "context" -> this.parent
//    ))
//}


//case class FolderPage(val wiki: Wiki, val folder: String, val contentTransformer: PageContentTransformer) extends WikiPage {
//  private val template = "folder.mustache"
//   
//  	
//  def render(pageRenderer: PageRenderer) =
//    pageRenderer.render(template, Map(
//      "pageTitle" -> wiki.name, 
//      "wikiPage" -> this, 
//      "pages" -> pages,
//      "files" -> files,
//      "folders" -> folders))
//  }


case class RootPage(val wikis: List[Wiki]) {
  private val template = "root.mustache"
  def render(pageRenderer: PageRenderer) = pageRenderer.render(template, Map("pageTitle" -> "Shard", "wikis" -> wikis))
}