package a4.shard.render

import java.io.InputStream

import a4.shard.transforming.PageContentTransformer
import a4.shard.{Content, Page}
import a4.util.FileUtil

import scalatags.Text.all._
import scalatags.Text.tags2

class PageRenderer(contentTransformer: PageContentTransformer) extends AbstractContentRenderer {

  def render(page: Page) : InputStream =
    toInputStream(
      html(
        renderHead("Page FIXME"),
        body(
          renderHeader(Some(page.wiki), page, page.parent),
          tags2.article(
            div(cls := "page-content",
              button(cls := "edit", "accesskey".attr := "e", "Edit"),
              div(raw(contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))))),
            div(cls := "page-editor",
              form(id := "page-editor-form", method := "POST", action := Content.toLink(page), "enctype".attr := "multipart/form-data",
                textarea(cls := "page-editor-textarea", name := "newContent", FileUtil.readAsUtf8(page.file)),
                input(cls := "save-edit", `type` := "submit", value := "Save")
              ),
              a(cls := "cancel-edit", href := Content.toLink(page), "Cancel")),
            div(cls := "page-preview")),
          renderFooter())))

}
