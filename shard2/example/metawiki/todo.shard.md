# TODO List for Shard Wiki ✓

## Sooner
1. ☑ have a static asset renderer on some paths (css/img/js or rather on "static" as an intermediate path?) that just feeds the files (some caching headers?)
1. ☑ use Twitter Bootstrap for the layout
1. ☑ get the first wiki page attempt going with read only (embedded markdown! use actuarius?)
1. ☑ Set the Content-Type for the response
1. ☑ allow linking
1. ☑ [navigation fallbacks within a wiki to try to recover gracefully with wrong URLs](self:/page/design/navigation)
1. ☐ Remove Bootstrap crap, more basic styling (check https://megajs.github.io/ and possibly https://github.com/kriskowal/q if I need a bunch of async)
1. ☐ make the folder template (three columns: wiki pages, other files, directories)
1. ☐ auto-render children/siblings/attchments list (one icon in toolbar, make the 
     folder page layout a reusable fragment and pack it in a hoverintent dropdown 
     for each page (context everywhere!). In order to do that: make the 
     Page/ConcretePage/FolderPage structure a real hierarchy in that ConcretePages 
     know what parent they have (a FolderPage) and a FolderPage has knowledge about 
     its context (the pages/files/folders thing) but with real Page objects.
1. ☐ implement flash messages, check the todos and use them where required (mostly in navigation fallbacks for now)
1. ☐ auto-render attachment list
1. ☐ In-browser editing of pages
1. ☐ access-keys for keyboard shortcuts (e for edit most important)
1. ☐ Side by side live editing of markdown: edit in textarea on the left, have auto-refresh on the right, with scrolling to position.
1. ☐ Add feature to allow insertion of a table of contents: some metasyntactic character combination and some logic in the markdown renderer to extract a TOC

## Later
1. ☐ Link to pages in other wikis (really? do we need this?)

## Notes
* use the FontAwesome thing for icons
