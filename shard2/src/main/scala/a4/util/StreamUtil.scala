package a4.util

import java.io.{File, FileOutputStream, InputStream, OutputStream}

object StreamUtil {

  private def consume(is: InputStream, consumer: (Array[Byte], Integer) => Unit) : Unit = {
    val buf = new Array[Byte](8192)
    var read = 0
    while (read >= 0) {
      read = is.read(buf)
      if (read > 0) {
        consumer(buf, read)
      }      
    }     
  }
  
  def copy(is: InputStream, os: OutputStream) : Unit = {
    consume(is, (buf, read) => os.write(buf, 0, read))
  }

  def copy(is: InputStream, file: File) : Unit = {
    import resource._
    for (fos <- managed(new FileOutputStream(file))) {
      copy(is, fos)
    }
  }


  def toString(is: InputStream, encoding: String) : String = {
    val sb = new StringBuilder()
    consume(is, (buf, read) => sb.append(new String(buf, 0, read, encoding)))
    sb.toString()
  }
  
}