package a4.util

import java.io.File
import java.nio.charset.Charset

import org.apache.commons.io.FileUtils

object FileUtil {
  def readAsUtf8(file: File) : String = FileUtils.readFileToString(file, Charset.forName("UTF-8"))
}