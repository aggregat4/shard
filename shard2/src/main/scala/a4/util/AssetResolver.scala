package a4.util

import java.io.Reader

trait AssetResolver {
  def getReader(relativePath: String) : Reader 
}