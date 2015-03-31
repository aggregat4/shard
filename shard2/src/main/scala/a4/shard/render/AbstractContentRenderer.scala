package a4.shard.render

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.Charset

import a4.shard._

import scalatags.Text.all._
import scalatags.Text.tags2

abstract class AbstractContentRenderer {

  val DOCTYPE: String = "<!DOCTYPE html>"

  protected def renderHead(pageTitle: String) : Modifier =
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

  protected def cssLink(name: String) : Modifier =
    link(rel := "stylesheet", `type` := "text/css", href := name)

  protected def renderContentList(title: String, items: List[Content]) : Seq[Modifier] =
    if (items.isEmpty)
      Seq()
    else
      Seq(
        h3(title),
        ul(items.map(p => li(toWikiPageLink(p)))))

  protected def renderHeader(wiki: Option[Wiki], wikiPage: Content, contextFolder: Folder) : Modifier =
    header(
      tags2.nav(
        a(cls := "allwikis", href := "/")("All Wikis"),
        ol(cls := "breadcrumbs", toBreadCrumbs(wikiPage).map(c => li(toWikiPageLink(c)))),
        form(cls := "search", method := "GET", action := "/search",
          input(cls := "querytext", `type` := "text", name := "q"),
          input(cls := "searchbutton", `type` := "submit", value := "Search")
        ),
        button(cls := "context", "Context"),
        div(cls :=  "contextPopup")(
          (renderContentList("Pages", contextFolder.pages) ++
            renderContentList("Files", contextFolder.attachments) ++
            renderContentList("Folders", contextFolder.folders)):_*), // Note: I'm splatting the elements of the Seq since scalatags apparently doesn't deal with the Seq well
        renderClearerDiv()))

  protected def renderClearerDiv() : Modifier = div(cls := "clearer")

  protected def renderFooter() : Modifier =
    footer(
      i("This page produced by Shard."),
      script(src := "/js/aslovok.js"),
      script(src := "/js/mega.js"),
      script(src := "/js/main.js"),
      script(src := "/js/marked.js"),
      script(src := "/js/velocity.js"))

  protected def toInputStream(root: Modifier) : InputStream =
    new ByteArrayInputStream((DOCTYPE + root.toString).getBytes(Charset.forName("UTF-8")))

  protected def toBreadCrumbs(content: Content) : List[Content] =
    if (Content.isRoot(content)) List(content)
    else toBreadCrumbs(content.parent) ++ List(content)

  protected def toWikiPageLink(content: Content) : Modifier =
    a(href := Content.toLink(content))(getName(content))

  // TODO: real name, what is the name of a piece of content?
  protected def getName(content: Content) : Modifier = content match {
    case r if Content.isRoot(r) => span(cls := "root-link", r.wiki.name + " Root")
    case a: Attachment => span(cls := "attachment-link", a.file.getName)
    case p: Page => span(cls := "page-link", p.file.getName)
    case f: Folder => span(cls := "folder-link", f.file.getName)
    case c => c.relativeUrl
  }

}
