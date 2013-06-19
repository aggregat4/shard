package a4.util

import java.io.File

object PathUtil {
  private def makeFolderString(folder: String) : String = if (folder.endsWith("/")) folder else (folder + "/") 
  
  def parent(path: String) : String = makeFolderString(path.split("/").reverse.tail.reverse.mkString("/"))
    
  def toConcretePath(folder: String, page: String) : String = 
    makeFolderString(folder) + page
  
  def toFolderPath(folder: String, childFolder: String) : String = makeFolderString(folder) + makeFolderString(childFolder)
  
  def name(path: String) = path.split("/").reverse.head
}