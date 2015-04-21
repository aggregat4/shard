package a4.util

import org.junit.Assert._
import org.junit.Test

class PathUtilTest {

  @Test def parentOfEmpty : Unit = assertEquals("", PathUtil.parent(""))

  @Test def parentOfSlash : Unit  = assertEquals("", PathUtil.parent("/"))

  @Test def parentOfNonSlash : Unit  = assertEquals("", PathUtil.parent("a"))

  @Test def parentOfComponentWithTrailingSlash : Unit  = assertEquals("a", PathUtil.parent("a/"))

  @Test def parentOfTwoPathComponents : Unit  = assertEquals("a/", PathUtil.parent("a/b"))

  @Test def parentOfTwoPathComponentsWithTrailingSlash : Unit  = assertEquals("a/b", PathUtil.parent("a/b/"))

}
