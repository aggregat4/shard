package a4.shard

import java.io.File

import org.junit.Assert._
import org.junit.Test

class DomainTest {

  val fooWikiLocation = new java.io.File(getClass.getResource("/wikitestbasepath").toURI)
  val fooWiki = Wiki("foo", "Foo Wiki", fooWikiLocation)
  val fooRoot = Folder(fooWiki, fooWiki.location)
  
  @Test def rootPageExistence = assertTrue(fooRoot.isValid)
  
  @Test def rootPageFile: Unit = assertEquals(fooWikiLocation, fooRoot.file)

  @Test def findExistingFolderWithSlash = {
    val folder = Content.toContentWithFallback(fooWiki, "folder1/").get
    assertTrue(folder.isInstanceOf[Folder])
    assertEquals(new File(fooWikiLocation, "folder1"), folder.file)
  }

  @Test def findExistingFolderWithoutSlash = {
    val folder = Content.toContentWithFallback(fooWiki, "folder1").get
    assertTrue(folder.isInstanceOf[Folder])
    assertEquals(new File(fooWikiLocation, "folder1"), folder.file)
  }

  @Test def findNonExistingPageInFolder = {
    val folder = Content.toContentWithFallback(fooWiki, "folder1/blablah").get
    assertTrue(folder.isInstanceOf[Folder])
    assertEquals(new File(fooWikiLocation, "folder1"), folder.file)
  }

  @Test def findExistingPage = {
    val page = Content.toContentWithFallback(fooWiki, "folder1/page2").get
    assertTrue(page.isInstanceOf[Page])
    assertEquals(new File(fooWikiLocation, "folder1/page2.shard.md"), page.file)
  }

}