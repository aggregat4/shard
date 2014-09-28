package a4.shard.view

import a4.shard.{Attachment, Folder, Page, Wiki}

case class WikiViewModel(
  name: String,
  url: String)

case class ContentViewModel(
  name: String,
  url: String)

case class WikiPageViewModel(
  title: String,
  wiki: WikiViewModel,
  content: ContentViewModel,
  pages: List[ContentViewModel],
  files: List[ContentViewModel],
  folders: List[ContentViewModel])

object ViewDomain {

  def createViewModel(title: String, page: Page) : WikiPageViewModel =
    createViewModel(title, page.wiki, toContentViewModel(page), page.parent)

  def createViewModel(title: String, folder: Folder) : WikiPageViewModel =
    createViewModel(title, folder.wiki, toContentViewModel(folder), folder)

  def createViewModel(title: String, attachment: Attachment) : WikiPageViewModel =
    createViewModel(title, attachment.wiki, toContentViewModel(attachment), attachment.parent)

  private def createViewModel(title: String, wiki: Wiki, content: ContentViewModel, contextFolder: Folder) : WikiPageViewModel =
    WikiPageViewModel(
      title,
      WikiViewModel(wiki.name, wiki.urlPath),
      content,
      contextFolder.pages.map(toContentViewModel),
      contextFolder.attachments.map(toContentViewModel),
      contextFolder.folders.map(toContentViewModel))

  private def toContentViewModel(page: Page) : ContentViewModel = ContentViewModel(page.relativeUrl, "FIXME PAGE URL")
  private def toContentViewModel(folder: Folder) : ContentViewModel = ContentViewModel(folder.relativeUrl, "FIXME FOLDER URL")
  private def toContentViewModel(attachment: Attachment) : ContentViewModel = ContentViewModel(attachment.relativeUrl, "FIXME ATTACHMENT URL")

}

/*
case class PageViewModel() extends ContentViewModel

case class FolderViewModel(name: String, url: String) extends ContentViewModel

case class AttachmentViewModel() extends ContentViewModel
*/
