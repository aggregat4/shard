package a4.shard.transforming

import a4.shard.WikiPage

trait PageContentTransformer {
  def transform(page: WikiPage) : String
}