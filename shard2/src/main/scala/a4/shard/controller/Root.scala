package a4.shard.controller

import a4.shard.ShardConfiguration
import a4.shard.routing.Status._
import a4.shard.routing.{Request, Response, StringResponse}
import a4.shard.templating.PageRenderer
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class Root(val config: ShardConfiguration, val pageRenderer: PageRenderer, val assetResolver: AssetResolver) {

  private val rootPageFile = "templates/root.mustache"

  def apply(req: Request): Response =
    StringResponse(Ok, pageRenderer.renderStream(assetResolver.getInputStream(rootPageFile), Map("wikis" -> config.wikis)), Some(MediaType.HTML_UTF_8))

}