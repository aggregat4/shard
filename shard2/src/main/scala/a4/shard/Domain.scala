package a4.shard

import java.io.File
import a4.util.FileUtil
import a4.shard.transforming.PageContentTransformer
import a4.shard.templating.PageRenderer

case class Wiki(val id: String, val name: String, val location: String) {
  def urlPath : String = "/wiki/" + id
}

sealed trait Page {
  def render(pageRenderer: PageRenderer) : String  
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
 
  def exists = pageFile.exists() && pageFile.isFile()
  
  def render(pageRenderer: PageRenderer) = 
    pageRenderer.render(template, Map(
      "pageTitle" -> wiki.name, 
      "wikiPage" -> this, 
      "pageContent" -> contentTransformer.transform(this, content)))
}

case class FolderPage(val wiki: Wiki, val folder: String) extends WikiPage {
  private val template = "folder.mustache"
  private val folderFile = new File(wiki.location, folder)

  def exists = folderFile.exists() && folderFile.isDirectory()
  
  def render(pageRenderer: PageRenderer) = ???
}

case class RootPage(val wikis: List[Wiki]) extends Page {
  private val template = "root.mustache"
  def render(pageRenderer: PageRenderer) = pageRenderer.render(template, Map("pageTitle" -> "Shard", "wikis" -> wikis))
}