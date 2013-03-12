package a4.shard.transforming

import a4.shard.WikiPage
import eu.henkelmann.actuarius.ActuariusTransformer
import a4.util.FileUtil
import eu.henkelmann.actuarius.Decorator
import eu.henkelmann.actuarius.Transformer

class DecoratingTransformer(val page: WikiPage) extends Transformer with Decorator {
  override def deco() = this

  private def replaceWikiRoot(link: String) : String = link.replaceFirst("(self|SELF):/?", page.wiki.urlPath + "/")
  
  override def decorateLink(text: String, url: String, title: Option[String]): String = 
    replaceWikiRoot(super.decorateLink(text, url, title))  
}

case class MarkdownTransformer extends PageContentTransformer { 
  override def transform(page: WikiPage): String = 
    new DecoratingTransformer(page)(FileUtil.readAsUtf8(page.pageFile)) // ActuariusTransformer is not threadsafe, therefore instantiating a new one each time    
}