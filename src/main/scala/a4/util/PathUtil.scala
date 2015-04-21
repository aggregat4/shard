package a4.util

import java.util.StringTokenizer

object PathUtil {

  private def makeFolderString(folder: String) : String = if (folder.endsWith("/")) folder else folder + "/"

  private def splitAndReverse(input: String, delimiter: String) : List[String] = {
    val tokenizer: StringTokenizer = new StringTokenizer(input, delimiter, true)
    var tokens = List[String]()
    while (tokenizer.hasMoreTokens) {
      tokens = tokenizer.nextToken() :: tokens
    }
    return tokens
  }

  def parent(path: String) : String = splitAndReverse(path, "/") match {
    case components if components.isEmpty => path
    case components => components.tail.reverse.mkString("")
  }

  //def parent(path: String) : String = makeFolderString(path.split("/").reverse.tail.reverse.mkString("/"))
    
  def toConcretePath(folder: String, page: String) : String = 
    makeFolderString(folder) + page
  
  def toFolderPath(folder: String, childFolder: String) : String = makeFolderString(folder) + makeFolderString(childFolder)
  
  def name(path: String) = path.split("/").reverse.head
  
  // Consider more aggressive string cleaning (just collapsing slashes for example)
  def isEmpty(path: String) : Boolean = path.trim() == ""

}