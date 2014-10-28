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
1. ☐ side by side live editing of markdown: edit in textarea on the left, have auto-refresh on the right, with scrolling to position
1. ☐ implement flash messages, check the todos and use them where required (mostly in navigation fallbacks for now)
1. ☐ access-keys for keyboard shortcuts (e for edit most important)
1. ☐ Add feature to allow insertion of a table of contents: some metasyntactic character combination and some logic in the markdown renderer to extract a TOC
1. ☐ make the folder template (three columns: wiki pages, other files, directories)
1. ☐ make it prett(y)(ier)
1. ☐ full text search with elasticsearch (autoindexing, notice new files)
1. ☐ wiki is a git repository: auto init, auto recognize, auto add files, show status in pages, conflicts?
1. ☐ consider doing client side Markdown rendering only: at the moment there are 2 renderers at work. Once on the server with arctuarius for the page view and then while live editing with marked.js. Perhaps we should just always render the "preview" and just hide the textarea in cases where we just view the page
1. ☐ consider using the "Ace" editor (http://ace.c9.io/#nav=higlighter) (uses contentEditable DIVs and has syntax highlighting). It is used by Dillinger (http://dillinger.io/), StackEdit and IO9. StackEdit does the nicest live scrolling. Dillinger does something that is perhaps passable. Figure out what.

## Later
1. ☐ Link to pages in other wikis (really? do we need this?)

## Notes
* use the FontAwesome thing for icons?
