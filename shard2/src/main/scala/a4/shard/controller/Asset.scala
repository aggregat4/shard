package a4.shard.controller

import a4.shard.ShardConfiguration
import a4.shard.routing.Status._
import a4.shard.routing.{InputStreamResponse, Request, Response}
import a4.shard.templating.PageRenderer
import a4.util.AssetResolver
import com.google.common.net.MediaType

case class Asset(val config: ShardConfiguration, val pageRenderer: PageRenderer, val assetResolver: AssetResolver) {

  private def getAssetContentType(req: Request) : Option[MediaType] = 
    if (req.pathUrl.startsWith("/css")) Some(MediaType.CSS_UTF_8)
    else if (req.pathUrl.startsWith("/js")) Some(MediaType.TEXT_JAVASCRIPT_UTF_8)
    else if (req.pathUrl.toLowerCase.endsWith(".jpg")) Some(MediaType.JPEG)
    else if (req.pathUrl.toLowerCase.endsWith(".png")) Some(MediaType.PNG)
    else if (req.pathUrl.toLowerCase.endsWith(".gif")) Some(MediaType.GIF)
    else None
        
  def apply(req: Request): Response =
    InputStreamResponse(Ok, assetResolver.getInputStream(req.pathUrl), getAssetContentType(req))
  
}