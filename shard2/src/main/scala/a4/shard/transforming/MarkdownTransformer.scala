package a4.shard.transforming

import a4.shard.WikiPage
import eu.henkelmann.actuarius.ActuariusTransformer
import a4.util.FileUtil

case class MarkdownTransformer extends PageContentTransformer {  
  
  // ActuariusTransformer is not threadsafe, therefore instantiating a new one each time
  override def transform(page: WikiPage) : String = new ActuariusTransformer()(FileUtil.readAsUtf8(page.pageFile))
  
}