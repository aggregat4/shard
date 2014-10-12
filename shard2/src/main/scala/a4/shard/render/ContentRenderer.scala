package a4.shard.render

import java.io.{ByteArrayInputStream, FileInputStream, InputStream}
import java.nio.charset.Charset

import a4.shard._
import a4.shard.transforming.PageContentTransformer
import a4.util.FileUtil

import scalatags.Text.all._
import scalatags.Text.tags2

trait ContentRenderer {
  def render(folder: Folder) : InputStream
  def render(page: Page) : InputStream
  def render(attachment: Attachment) : InputStream = new FileInputStream(attachment.file)
  def renderRoot(wikis: List[Wiki]) : InputStream
}

class CodeContentRenderer(contentTransformer: PageContentTransformer) extends ContentRenderer {

  override def render(folder: Folder) : InputStream =
    toInputStream(
      html(
        renderHead("Folder FIXME"),
        body(
          renderHeader(Some(folder.wiki), folder, folder),
          renderFooter())))

  override def render(page: Page) : InputStream =
    toInputStream(
      html(
        renderHead("Page FIXME"),
        body(
          renderHeader(Some(page.wiki), page, page.parent),
          raw(contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))),
          renderFooter())))

  override def renderRoot(wikis: List[Wiki]): InputStream =
    toInputStream(
      html(
        renderHead("Shard Root"),
        body(
          header(
            tags2.nav(
              a(href := "/")("Home"))),
          h1("Shard"),
          p("You have the following wikis configured:"),
          ul(wikis.map(w => a(href := "wiki/" + w.id)(w.name))),
          renderFooter())))

  private val DOCTYPE: String = "<!DOCTYPE html>"

  private def renderHead(pageTitle: String) : Modifier =
    head(
      meta(charset := "utf-8"),
      meta(httpEquiv := "X-UA-Compatible", content := "IE=edge,chrome=1"),
      meta(name := "viewport", content := "width=device-width"),
      tags2.title(pageTitle))

  private def renderContentList(title: String, items: List[Content]) : Seq[Modifier] =
    if (items.isEmpty)
      Seq()
    else
      Seq(
        h3(title),
        ul(items.map(p => li(a(href := "/wiki/" + p.wiki.id + "/page/" + p.relativeUrl)(p.relativeUrl))))) // TODO: real name

  private def renderHeader(wiki: Option[Wiki], wikiPage: Content, contextFolder: Folder) : Modifier =
    header(
      tags2.nav(
        a(href := "/")("Home")),
        button("Context"),
        div(cls :=  "contextPopup")(
          (renderContentList("Pages", contextFolder.pages) ++
          renderContentList("Files", contextFolder.attachments) ++
          renderContentList("Folders", contextFolder.folders)):_*),  // Note: I'm splatting the elements of the Seq since scalatags apparently doesn't deal with the Seq well
        ol( // TODO: map a list of paths from root to a list of lis
          ))

  private def renderFooter() : Modifier =
    footer(
      p(i("This page produced by Shard.")))

  private def toInputStream(root: Modifier) : InputStream =
    new ByteArrayInputStream((DOCTYPE + root.toString()).getBytes(Charset.forName("UTF-8")))

}