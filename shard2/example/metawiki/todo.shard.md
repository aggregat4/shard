# TODO List for Shard Wiki ✓

## Sooner
1. ☑ have a static asset renderer on some paths  (css/img/js or rather on "static" as an intermediate path?) that just feeds the files (some caching headers?)
1. ☑ use Twitter Bootstrap for the layout
1. ☑ get the first wiki page attempt going with read only (embedded markdown! use actuarius?)
1. ☑ Set the Content-Type for the response
1. ☑ allow linking
1. ☑ [navigation fallbacks within a wiki to try to recover gracefully with wrong URLs](self:/page/design/navigation)
1. ☑  auto-render children/siblings/attchments list
1. ☑ fix page and folder links
1. ☑ Remove Bootstrap crap, more basic styling (check https://megajs.github.io/ and possibly https://github.com/kriskowal/q if I need a bunch of async)
1. ☑ auto-render attachment list
1. ☑ figure out how to optimally do type detection and render correct mimetypes (library?)
1. ☑ render breadcrumbs to make navigation easier
1. ☑ simple editing of pages in a textarea (wrap textarea in form and provide a save button, requires new post handler on the server)
1. ☑ side by side live editing of markdown: edit in textarea on the left, have auto-refresh on the right, with scrolling to position. [Synchronized scrolling approach with percentage based scrolling](https://github.com/anru/rsted/blob/master/static/scripts/editor.js) see "syncScrollPosition", it uses the jquery scroll binding which I think mega.js should allow as well, probably also should debounce it.
1. ☑ full text search with elasticsearch
1. ☑ make the folder template (three columns: wiki pages, other files, directories)
1. ☐ allow adding new pages from the wiki: with a toolbar button and make links to non-existing pages open in the editor
1. ☐ write some rudimentary documentation, clean up project (shard/shard2, remove IDEA files?) and push to GitHub (backup!)
1. ☐ make it prett(y)(ier)
1. ☐ autoindexing (notice new files not created in the application) for full text search
1. ☐ implement flash messages, check the todos and use them where required (mostly in navigation fallbacks for now)
1. ☐ access-keys for keyboard shortcuts (e for edit most important)
1. ☐ Add feature to allow insertion of a table of contents: some metasyntactic character combination and some logic in the markdown renderer to extract a TOC
1. ☐ wiki is a git repository: auto init, auto recognize, auto add files, show status in pages, conflicts?
1. ☐ consider doing client side Markdown rendering only: at the moment there are 2 renderers at work. Once on the server with arctuarius for the page view and then while live editing with marked.js. Perhaps we should just always render the "preview" and just hide the textarea in cases where we just view the page
1. ☐ consider using the ["Ace" editor](http://ace.c9.io/#nav=higlighter) (uses contentEditable DIVs and has syntax highlighting). It is used by [Dillinger](http://dillinger.io/), StackEdit and IO9

## Later
1. ☐ Link to pages in other wikis (really? do we need this?)
1. ☐ I may know what I really want: a workflowy/wiki hybrid: each "shard" is a tree, left side of screen is editing and moving around the tree (a la Workflowy), the right side is editing the "content" like a dedicated attachment that is basically markdown content, allow dual pane editing like now. Technical implementation: extract out the http library and other utilities in a separate project, finish up the current wiki until it works with search and some prototypical git integration. For the new project perhaps also file storage, flat format with one directory per node in the tree (gathers node title/summary, the "content" and the attachments), this is no longer just a robust layer on top of an existing directory layout that you can edit with other tools. On the other hand it is still just files on a disk and the structure is kept separately so it is very flexible and easy to edit, so massive advantage there. 

## Notes
