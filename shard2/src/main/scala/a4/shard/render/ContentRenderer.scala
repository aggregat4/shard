package a4.shard.render

import java.io.{ByteArrayInputStream, FileInputStream, InputStream}
import java.nio.charset.Charset

import a4.shard._
import a4.shard.templating.PageTemplateRenderer
import a4.shard.transforming.PageContentTransformer
import a4.shard.view.ViewDomain
import a4.util.{AssetResolver, FileUtil}

import scalatags.Text.all._
import scalatags.Text.tags2

trait ContentRenderer {
  def render(folder: Folder) : InputStream
  def render(page: Page) : InputStream
  def render(attachment: Attachment) : InputStream = new FileInputStream(attachment.file)
}

class CodeContentRenderer(contentTransformer: PageContentTransformer) extends ContentRenderer {

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
        ul(items.map(p => li(a(href := p.relativeUrl)(p.relativeUrl))))) // TODO: real url, real name

  private def renderHeader(wiki: Option[Wiki], wikiPage: Content, contextFolder: Folder) : Modifier =
    header(
      tags2.nav(
        a(href := "/")("Home")),
        button("Context"),
        div(cls :=  "contextPopup")(
          renderContentList("Pages", contextFolder.pages)/* ++
          renderContentList("Files", contextFolder.attachments) ++
          renderContentList("Folders", contextFolder.folders)*/
        ))

  private def renderFooter() : Modifier =
    footer(
      p(
        "This page produced by ",
        a(href := "http://example.com")("Shard"),
        "."))

  private def toInputStream(root: Modifier) : InputStream =
    new ByteArrayInputStream((DOCTYPE + root.toString()).getBytes(Charset.forName("UTF-8")))

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

}

/**
 * Legacy renderer that uses a templating system to render the complete page (Mustache only so far). Did not actually
 * pull the variables out of the context reliably and I abandoned it for a pure code based renderer.
 */
class TemplateContentRenderer(pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) extends ContentRenderer {

  override def render(folder: Folder) : InputStream = {
    val vm = ViewDomain.createViewModel("Folder FIXME", folder)
    render(
      Map("vm" -> ViewDomain.createViewModel("Folder FIXME", folder)),
      "templates/folder.mustache")
  }

  override def render(page: Page) : InputStream =
    render(
      Map("vm" -> ViewDomain.createViewModel("Folder FIXME", page), "pageContent" -> contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))),
      "templates/page.mustache")

  private def render(context: Map[String, AnyRef], templateName: String) : InputStream =
    new ByteArrayInputStream(
      pageTemplateRenderer
        .renderStream(assetResolver.getInputStream(templateName), context)
        .getBytes(Charset.forName("UTF-8")))

}