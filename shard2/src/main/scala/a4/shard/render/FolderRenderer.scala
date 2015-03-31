package a4.shard.render

import java.io.InputStream

import a4.shard.Folder

import scalatags.Text.all._
import scalatags.Text.tags2

class FolderRenderer extends AbstractContentRenderer {

  def render(folder: Folder) : InputStream =
    toInputStream(
      html(
        renderHead("Folder FIXME"),
        body(
          renderHeader(Some(folder.wiki), folder, folder),
          tags2.article(h1("This is a Folder, fill me with a context page!")),
          renderFooter())))

}
