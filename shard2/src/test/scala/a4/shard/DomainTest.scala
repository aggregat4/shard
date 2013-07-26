package a4.shard

import org.junit.Test
import org.junit.Assert._

class DomainTest {

  val fooWikiLocation = new java.io.File(getClass.getResource("/wikitestbasepath").toURI)
  val fooWiki = Wiki("foo", "Foo Wiki", fooWikiLocation)
  val fooRoot = Root(fooWiki)
  
  @Test def rootPageExistence() : Unit = assertTrue(fooRoot.exists)
  
  @Test def rootPageFile() : Unit = assertEquals(fooWikiLocation, fooRoot.file)
  
//  @Test def rootPageExistence() : Unit = assertTrue(fooRoot.exists)
  

}