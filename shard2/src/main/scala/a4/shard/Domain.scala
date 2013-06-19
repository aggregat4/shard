package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.util.PathUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer


case class Wiki(val id: String, val name: String, val location: String) {
  def urlPath : String = "/wiki/" + id
}


sealed trait Page {
  def render(pageRenderer: PageRenderer) : String
  def url : String
  def name : String
}


sealed trait WikiPage extends Page {
  def wiki : Wiki
  def exists : Boolean
}


case class ConcretePage(val wiki: Wiki, val page: String, val contentTransformer: PageContentTransformer) extends WikiPage {
  private val pageSuffix = ".shard.md"
  private val pageFile = new File(wiki.location, page + pageSuffix)
  private val template = "page.mustache"
  private def content = FileUtil.readAsUtf8(pageFile)
 
  def url = wiki.urlPath + page
  
  def name = PathUtil.name(page) 
  
  override def exists = pageFile.exists() && pageFile.isFile()

  def parent : FolderPage = {
    val p = new FolderPage(wiki, PathUtil.parent(page), contentTransformer)
    return p
  }
  
  def render(pageRenderer: PageRenderer) = 
    pageRenderer.render(template, Map(
      "pageTitle" -> wiki.name, 
      "wikiPage" -> this, 
      "pageContent" -> contentTransformer.transform(this, content),
      "context" -> this.parent
    ))
}


case class FolderPage(val wiki: Wiki, val folder: String, val contentTransformer: PageContentTransformer) extends WikiPage {
  private val template = "folder.mustache"
  private val folderFile = new File(wiki.location, folder)

  def url = wiki.urlPath + folder
  
  def name = if (folder == "/") folder else PathUtil.name(folder) 
  
  override def exists = folderFile.exists() && folderFile.isDirectory()
   
  def pages : List[WikiPage] = {
	  val genPages = folderFile.listFiles.filter(f => f.isFile && f.getName.endsWith(".shard.md"))
			  .map(f => new ConcretePage(wiki, PathUtil.toConcretePath(folder, f.getName.substring(0, f.getName.length - ".shard.md".length)), contentTransformer) ).toList
  	  val url1 = genPages.head.url
  	  val url2 = genPages.tail.head.url
	  return genPages
  }
  	
  def files = List()
// I need a Page type for attachments
//    folderFile.listFiles
//  	.filter(f => f.isFile && ! f.getName.endsWith(".shard.md"))
//  	.map(f => Addressable(f.getName))
  
  def folders = folderFile.listFiles
  	.filter(f => f.isDirectory())
  	.map(f => new FolderPage(wiki, PathUtil.toFolderPath(folder, f.getName), contentTransformer))
  	
  def render(pageRenderer: PageRenderer) =
    pageRenderer.render(template, Map(
      "pageTitle" -> wiki.name, 
      "wikiPage" -> this, 
      "pages" -> pages,
      "files" -> files,
      "folders" -> folders))
  }


case class RootPage(val wikis: List[Wiki]) extends Page {
  private val template = "root.mustache"
  def render(pageRenderer: PageRenderer) = pageRenderer.render(template, Map("pageTitle" -> "Shard", "wikis" -> wikis))
  
  def url = "/"
  def name = "Root"
}