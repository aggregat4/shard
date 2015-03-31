package a4.shard.render

import java.io.{FileInputStream, InputStream}

import a4.shard.Attachment

class AttachmentRenderer extends AbstractContentRenderer {
  def render(attachment: Attachment) : InputStream = new FileInputStream(attachment.file)
}
