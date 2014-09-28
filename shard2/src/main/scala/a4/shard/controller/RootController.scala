package a4.shard.controller

import a4.shard.Configuration
import a4.shard.routing.Status._
import a4.shard.routing.{Request, Response, StringResponse}
import a4.shard.templating.PageTemplateRenderer
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class RootController(val config: Configuration, val pageRenderer: PageTemplateRenderer, val assetResolver: AssetResolver) {

  private val rootPageFile = "templates/root.mustache"

  // TODO: replace with ContentRenderer, specifically CodeContentRenderer
  def apply(req: Request): Response =
    StringResponse(Ok, pageRenderer.renderStream(assetResolver.getInputStream(rootPageFile), Map("wikis" -> config.wikis)), Some(MediaType.HTML_UTF_8))

}