TODO: make this documentation self hosting, start a design wiki in src/test/resources or something, can try out the 

# Dependencies
- -[Scalatest](http://www.scalatest.org/) and the [FunSuite](http://www.scalatest.org/getting_started_with_fun_suite)-
-- using JUnit with FEST
- [JGit](http://eclipse.org/jgit/)
- [JNotify](http://jnotify.sourceforge.net/)
- -[Scalatra](https://github.com/scalatra/scalatra) (or rather Finagle?)-
-- Using Spray
- -[Scalate Jade](http://scalate.fusesource.org/documentation/jade.html)-
-- Using Scalate Moustache
- [TypeSafe Config](https://github.com/typesafehub/config)

# What it is
- a shard repository is a directory
- a shard repository is a git repository
- multiple repositories can be managed
- shard-server watches the directory and subdirectories using jnotify and commits each change to git
- files are edited on the hard drive directly, outside of shard
- shard offers a web ui to manage repositories and view and edit shard wikis
  [manage functionality?]
  - list repositories
  - new repository
  - view repository as wiki
  - view repository as files (tree, versions)
  - show repository sync status (last sync, merge conflicts if any, last updated files (?))
  - search files (someday, lucene Elasticsearch?)
- the system must be extremely robust w.r.t out of band editing, external editing, creating, moving and deleting of files is absolutely encouraged
- the system must degrade very gracefully: no invalid states, always try to render something, fall back to "just" files in the end
- wiki functionality is extra: in the end it is a tree of versioned files
- externally edited files should be auto-versioned based on filesystem notifications 
- Remote Repositories
  - shard repositories can be synced with a remote master
    [how to handle merge conflicts? check out what git does with those]
  
# Shard Wiki design
- repositories can contain files with a .shardwiki extension that are Jade+Markdown files
- when wiki pages have children, a directory alongside a shardwiki file with the same name but without the extension is used to keep the children
- the toplevel shard wiki file is the root of the wiki hierarchy
- attachments for a page are stored are stored in the page's directory, all files in that directory that are not .shardwiki files are attachments
- wiki filenames are their "key", can be used to link to them as well
? how to handle collisions?
  [syntax extensions? includes? constrain syntax? what? see offline wiki requirements in notes]
example of file based syntax:
	/home/boris/foowiki
		myrootpage.shardwiki
    myrootpage/
		  attachment1.pdf
		  attachment2.png
		  myfirstrootchild.shardwiki
		  mysecondrootchild.shardwiki
- Configuration
  - shard server configuration is kept in application.conf HOCON notation based config file
