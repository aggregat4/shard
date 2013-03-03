package a4.util

import org.junit.Test
import org.junit.Assert._
import java.io.BufferedReader
import java.io.InputStreamReader

class ClasspathAssetResolverTest {

  val resolver = new ClasspathAssetResolver("assettestbasepath")
 
  @Test(expected = classOf[IllegalArgumentException]) 
  def readNonExistingFile : Unit = 
    resolver.getInputStream("nonexistingfile")
  
  @Test def readExistingFileNakedBasePath : Unit =
    assertEquals("Foo.", new BufferedReader(new InputStreamReader(resolver.getInputStream("foo.bar"), "utf-8")).readLine)
  
  @Test def readExistingFileInSubdirectoryWithNakedBasePath : Unit =
    assertEquals("Bar.", new BufferedReader(new InputStreamReader(resolver.getInputStream("foo/bar"), "utf-8")).readLine)
  
}