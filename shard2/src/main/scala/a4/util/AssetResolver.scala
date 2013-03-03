package a4.util

import java.io.InputStream

trait AssetResolver {
  def getInputStream(relativePath: String) : InputStream 
}