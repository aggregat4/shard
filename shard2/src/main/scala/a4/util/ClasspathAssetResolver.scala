package a4.util

import java.io.Reader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.InputStream

case class ClasspathAssetResolver(val basePath: String) extends AssetResolver {

  val cleanBasePath = basePath match {
    case "" => ""
    case path if path.endsWith("/") => path
    case _ => basePath + "/"
  }
  
  private def normalizeRelativePath(relativePath: String) = relativePath match {
    case path if path.startsWith("/") => path
    case _ => relativePath
  } 
  
  private def fullPath(relativePath: String) = cleanBasePath + normalizeRelativePath(relativePath)
  
  // Note: the "getClassLoader" interjection appears to be necessary to be able to actually load resources from the classpath, especially in tests
  override def getInputStream(relativePath: String) : InputStream = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(fullPath(relativePath))
    if (inputStream == null) 
      throw new IllegalArgumentException("The ClasspathAssetResolver treats missing files as programming errors, this file can not be found: " + fullPath(relativePath))
    return inputStream
  } 
    
}