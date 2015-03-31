package a4.shard.controller

import a4.shard.Configuration
import a4.shard.render.ContentRenderer
import a4.shard.routing._
import com.google.common.net.MediaType

case class NotFoundController(config: Configuration, contentRenderer: ContentRenderer) {

  def apply(req: Request): Response =
    StringResponse(Status.NotFound, s"Could not find the page with the URL '${req.pathUrl}'", Some(MediaType.PLAIN_TEXT_UTF_8))
}
