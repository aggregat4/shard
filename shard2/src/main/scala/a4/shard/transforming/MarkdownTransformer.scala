package a4.shard.transforming

import a4.shard.Page
import eu.henkelmann.actuarius.{Decorator, Transformer}

class DecoratingTransformer(val page: Page) extends Transformer with Decorator {
  override def deco() = this

  private def replaceWikiRoot(link: String) : String = link.replaceFirst("(self|SELF):/?", page.wiki.urlPath + "/")
  
  override def decorateLink(text: String, url: String, title: Option[String]): String = 
    replaceWikiRoot(super.decorateLink(text, url, title))
}

case class MarkdownTransformer extends PageContentTransformer {

  override def transform(page: Page, content: String): String = 
    new DecoratingTransformer(page)(content) // ActuariusTransformer is not threadsafe, therefore instantiating a new one each time

}