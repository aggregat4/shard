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
          tags2.article(h1("This is a Folder, fill me with a context page!")),
          renderFooter())))

  override def render(page: Page) : InputStream =
    toInputStream(
      html(
        renderHead("Page FIXME"),
        body(
          renderHeader(Some(page.wiki), page, page.parent),
          tags2.article(
            div(cls := "page-content",
              a(cls := "edit", "accesskey".attr := "e", href := "#", "Edit"),
              div(raw(contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))))),
            div(cls := "page-editor",
              form(id := "page-editor-form", method := "POST", action := Content.toLink(page), "enctype".attr := "multipart/form-data",
                textarea(cls := "page-editor-textarea", name := "newContent", FileUtil.readAsUtf8(page.file)),
                input(`type` := "submit", value := "Save")
              )),
            div(cls := "page-preview")),
          renderFooter())))

  override def renderRoot(wikis: List[Wiki]): InputStream =
    toInputStream(
      html(
        renderHead("Shard Root"),
        body(
          header(
            tags2.nav(
              a(href := "/")("Home"))),
          tags2.article(
            h1("Shard"),
            p("You have the following wikis configured:"),
            ul(wikis.map(w => a(href := "wiki/" + w.id)(w.name)))),
          renderFooter())))

  private val DOCTYPE: String = "<!DOCTYPE html>"

  private def renderHead(pageTitle: String) : Modifier =
    head(
      meta(charset := "utf-8"),
      meta(httpEquiv := "X-UA-Compatible", content := "IE=edge,chrome=1"),
      meta(name := "viewport", content := "width=device-width"),
      tags2.title(pageTitle),
      // todo: need a  dev mode that makes random urls and normal stable urls in normal mode
      cssLink("/css/normalize.css"),
      cssLink("/css/main.css"),
      cssLink("/css/header.css"),
      cssLink("/css/page.css"),
      cssLink("/css/footer.css"))

  private def cssLink(name: String) : Modifier =
    link(rel := "stylesheet", `type` := "text/css", href := name)

  private def renderContentList(title: String, items: List[Content]) : Seq[Modifier] =
    if (items.isEmpty)
      Seq()
    else
      Seq(
        h3(title),
        ul(items.map(p => li(toWikiPageLink(p)))))

  private def renderHeader(wiki: Option[Wiki], wikiPage: Content, contextFolder: Folder) : Modifier =
    header(
      tags2.nav(
        a(cls := "allwikis", href := "/")("All Wikis"),
        ol(cls := "breadcrumbs", toBreadCrumbs(wikiPage).map(c => li(toWikiPageLink(c)))),
        button(cls := "context", "Context"),
        div(cls :=  "contextPopup")(
          (renderContentList("Pages", contextFolder.pages) ++
          renderContentList("Files", contextFolder.attachments) ++
          renderContentList("Folders", contextFolder.folders)):_*), // Note: I'm splatting the elements of the Seq since scalatags apparently doesn't deal with the Seq well
        renderClearerDiv()))

  private def renderClearerDiv() : Modifier = div(cls := "clearer")

  private def renderFooter() : Modifier =
    footer(
      i("This page produced by Shard."),
      script(src := "/js/aslovok.js"),
      script(src := "/js/mega.js"),
      script(src := "/js/main.js"),
      script(src := "/js/marked.js"),
      script(src := "/js/velocity.js"))

  private def toInputStream(root: Modifier) : InputStream =
    new ByteArrayInputStream((DOCTYPE + root.toString).getBytes(Charset.forName("UTF-8")))

  private def toBreadCrumbs(content: Content) : List[Content] =
    if (Content.isRoot(content)) List(content)
    else toBreadCrumbs(content.parent) ++ List(content)

  private def toWikiPageLink(content: Content) : Modifier =
    a(href := Content.toLink(content))(getName(content))

  // TODO: real name, what is the name?
  private def getName(content: Content) : Modifier = content match {
    case c if Content.isRoot(c) => span(cls := "root-link", c.wiki.name + " Root")
    case a: Attachment => span(cls := "attachment-link", a.file.getName)
    case p: Page => span(cls := "page-link", p.file.getName)
    case f: Folder => span(cls := "folder-link", f.file.getName)
    case c => c.relativeUrl
  }

}