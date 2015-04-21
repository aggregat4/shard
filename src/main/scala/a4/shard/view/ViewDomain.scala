package a4.shard.view

import a4.shard._

/**
 * TODO: not sure this abstraction is needed, it separates out the model from the viewmodel and may make
 * the renderer easier to write on the one hand (less logic) and it may take pressure off of the domain model
 * to contain all sorts of crap but at the moment this is not clearly a win and rather just a bunch of
 * shoveling stuff around. Postponing this until iut is clearly a win.
 */

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

case class SearchResultViewModel(
  wiki: WikiViewModel,
  content: ContentViewModel
  // TODO: excerpt?
  )

case class SearchResultsViewModel(
  query: String,
  results: List[SearchResultViewModel])

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
