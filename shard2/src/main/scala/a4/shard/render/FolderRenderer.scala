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
          tags2.article(
            div(cls := "page-content",
              h1(getName(folder)),
              div(cls := "folder-column")(renderContentList("Pages", folder.pages):_*),
              div(cls := "folder-column")(renderContentList("Files", folder.attachments):_*),
              div(cls := "folder-column")(renderContentList("Folders", folder.folders):_*)
            )
          ),
          renderFooter())))

}
