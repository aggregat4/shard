package a4.shard.controller

import a4.shard.Configuration
import a4.shard.render.RootRenderer
import a4.shard.routing.Status._
import a4.shard.routing.{InputStreamResponse, Request, Response}
import com.google.common.net.MediaType

case class RootController(config: Configuration, rootRenderer: RootRenderer) {

  def apply(req: Request): Response = InputStreamResponse(Ok, rootRenderer.render(config.wikis), Some(MediaType.HTML_UTF_8))

}