package a4.shard.transforming

import a4.shard.Page

trait PageContentTransformer {
  def transform(page: Page, content: String) : String
}