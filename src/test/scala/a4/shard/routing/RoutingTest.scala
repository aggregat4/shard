package a4.shard.routing

import org.junit.Test
import org.junit.Assert._

class RoutingTest {
  val pathWithVariable = Path("/foo/{bar}/baz")
  val urlWithSimpleVariable = "/foo/thisisbar/baz"
  val urlWithPathFragmentVariable = "/foo/this/is/bar/baz"
  
  @Test def staticPathMatchingWithNonsense : Unit =
    assertFalse(Path("nonsense").matches("/foo/bar"))
    
  @Test def staticPathMatching : Unit =
    assertTrue(Path("/foo/bar").matches("/foo/bar"))
    
  @Test def suffixWildcardMatchingWithWrongPrefix : Unit =
    assertFalse(Path("/foo/**").matches("/bar/foo/"))
   
  @Test def suffixWildcardMatching : Unit =
    assertTrue(Path("/foo/**").matches("/foo/bar"))
    
  @Test def suffixWildcardMatchingOnMinimalUrl : Unit =
    assertTrue(Path("/foo/**").matches("/foo/"))

  @Test def patternWithVariables : Unit = {
    assertEquals(List("bar"), pathWithVariable.variableNames)  
  }
  
  @Test def variableMatchingWithOneVariable : Unit = {    
    assertTrue(pathWithVariable.matches(urlWithSimpleVariable))
    assertEquals(Map("bar" -> "thisisbar"), pathWithVariable.getVariables(urlWithSimpleVariable))
   }

  @Test def variableMatchingWithOnePathFragmentVariable : Unit = {    
    assertTrue(pathWithVariable.matches(urlWithPathFragmentVariable))
    assertEquals(Map("bar" -> "this/is/bar"), pathWithVariable.getVariables(urlWithPathFragmentVariable))
   }
  
}