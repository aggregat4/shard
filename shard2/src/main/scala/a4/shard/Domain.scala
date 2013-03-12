package a4.shard

import java.io.File

case class Wiki(val id: String, val name: String, val location: String) {
  def urlPath : String = "/wiki/" + id
}

case class WikiPage(val wiki: Wiki, val page: String) {
  val pageSuffix = ".shard.md"
  lazy val pageFile = new File(wiki.location, page + pageSuffix)
  
  def exists : Boolean = pageFile.exists() && pageFile.isFile()
}