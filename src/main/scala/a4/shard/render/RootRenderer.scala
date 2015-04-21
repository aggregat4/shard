package a4.shard.render

import java.io.InputStream

import a4.shard.Wiki

import scalatags.Text.all._
import scalatags.Text.tags2

class RootRenderer extends AbstractContentRenderer {

  def render(wikis: List[Wiki]): InputStream =
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
            ul(wikis.map(w => li(a(href := "wiki/" + w.id)(w.name))))),
          renderFooter())))

}
