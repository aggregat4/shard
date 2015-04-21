package a4.shard

package object routing {
  type Action = (Request => Response)
}