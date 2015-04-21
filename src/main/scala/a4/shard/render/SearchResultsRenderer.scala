package a4.shard.render

import java.io.InputStream

import a4.shard.SearchResults

import scalatags.Text.all._
import scalatags.Text.tags2

class SearchResultsRenderer extends AbstractContentRenderer {

  def render(searchResults: SearchResults) : InputStream =
    toInputStream(
      html(
        renderHead("Search Results for '" + searchResults.query + "'"),
        body(
          header(
            tags2.nav(
              a(href := "/")("Home"))),
          tags2.article(
            h1("Search Results for '" + searchResults.query + "'"),
            ol(
              searchResults.results.map(sr => li(toWikiPageLink(sr.content))))),
          renderFooter())))

}
