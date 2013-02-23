package a4.util

import org.junit.Test
import org.junit.Assert._
import java.io.BufferedReader

class ClasspathAssetResolverTest {

  val resolver = new ClasspathAssetResolver("assettestbasepath")
 
  @Test(expected = classOf[IllegalArgumentException]) 
  def readNonExistingFile : Unit = 
    resolver.getReader("nonexistingfile")
  
  @Test def readExistingFileNakedBasePath : Unit =
    assertEquals("Foo.", new BufferedReader(resolver.getReader("foo.bar")).readLine)
  
  @Test def readExistingFileInSubdirectoryWithNakedBasePath : Unit =
    assertEquals("Bar.", new BufferedReader(resolver.getReader("foo/bar")).readLine)
  
}