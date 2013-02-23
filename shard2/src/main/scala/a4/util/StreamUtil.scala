package a4.util

import java.io.{InputStream, OutputStream}

object StreamUtil {
  def copy(is: InputStream, os: OutputStream) : Unit = {
    var buf = new Array[Byte](8192)
    var read = 0
    while (read >= 0) {
      read = is.read(buf)
      if (read > 0) {
        os.write(buf, 0, read)
      }      
    } 
  }
}