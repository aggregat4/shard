package a4.shard.render

import java.io.{ByteArrayInputStream, FileInputStream, InputStream}
import java.nio.charset.Charset

import a4.shard.templating.PageTemplateRenderer
import a4.shard.transforming.PageContentTransformer
import a4.shard.{Attachment, Folder, Page}
import a4.util.{AssetResolver, FileUtil}

object ContentRenderer {

  def render(folder: Folder, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) : InputStream = {
    new ByteArrayInputStream(
      pageTemplateRenderer.renderStream(
        assetResolver.getInputStream("templates/folder.mustache"),
        Map[String, AnyRef](
          "pageTitle" -> ("Folder " + folder.relativeUrl),
          "wikiPage" -> folder,
          "context" -> folder.parent,
          "pages" -> folder.pages,
          "files" -> folder.attachments,
          "folders" -> folder.folders
        )).getBytes(Charset.forName("UTF-8")))
  }

  def render(page: Page, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) : InputStream = {
    new ByteArrayInputStream(
      pageTemplateRenderer.renderStream(
        assetResolver.getInputStream("templates/page.mustache"),
        Map[String, AnyRef](
          "pageTitle" -> ("Page " + page.relativeUrl),
          "wikiPage" -> page,
          "context" -> page.parent,
          "pageContent" -> contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))
        )).getBytes(Charset.forName("UTF-8")))
  }

  def render(attachment: Attachment) : InputStream = new FileInputStream(attachment.file)

}