package a4.shard.render

import java.io.{ByteArrayInputStream, FileInputStream, InputStream}
import java.nio.charset.Charset

import a4.shard.templating.PageTemplateRenderer
import a4.shard.transforming.PageContentTransformer
import a4.shard.view.ViewDomain
import a4.shard.{Attachment, Folder, Page}
import a4.util.{AssetResolver, FileUtil}

object ContentRenderer {

  def render(folder: Folder, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) : InputStream = {
    val vm = ViewDomain.createViewModel("Folder FIXME", folder)
    render(
      Map("vm" -> ViewDomain.createViewModel("Folder FIXME", folder)),
      "templates/folder.mustache",
      pageTemplateRenderer,
      assetResolver,
      contentTransformer)
  }

  def render(page: Page, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) : InputStream =
    render(
      Map("vm" -> ViewDomain.createViewModel("Folder FIXME", page), "pageContent" -> contentTransformer.transform(page, FileUtil.readAsUtf8(page.file))),
      "templates/page.mustache",
      pageTemplateRenderer,
      assetResolver,
      contentTransformer)

  private def render(context: Map[String, AnyRef], templateName: String, pageTemplateRenderer: PageTemplateRenderer, assetResolver: AssetResolver, contentTransformer: PageContentTransformer) : InputStream =
    new ByteArrayInputStream(
      pageTemplateRenderer
        .renderStream(assetResolver.getInputStream(templateName), context)
        .getBytes(Charset.forName("UTF-8")))

  def render(attachment: Attachment) : InputStream = new FileInputStream(attachment.file)

}