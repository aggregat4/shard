# Dependencies
- -[Scalatest](http://www.scalatest.org/) and the [FunSuite](http://www.scalatest.org/getting_started_with_fun_suite)-
-- nope, using JUnit with FEST, I found it easier to get started
- [JGit](http://eclipse.org/jgit/)
- [JNotify](http://jnotify.sourceforge.net/)
- -[Scalatra](https://github.com/scalatra/scalatra) (or rather Finagle?)-
-- -nope, Using Spray-
--- nope, using home brew solution, Spray DSl was horrible to use at the time
- -[Scalate Jade](http://scalate.fusesource.org/documentation/jade.html)-
-- -Using Scalate Moustache-
--- -nope, using mustache-java-
---- nope, using Scalatags, scala based library for code-based templating.
- [TypeSafe Config](https://github.com/typesafehub/config)

# What it is
- a shard repository is a directory
- a shard repository is a git   repository
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
## File and Directory Structure
There are some possibilities with designing a wiki as a set of files and directories. Assuming there is a root directory with our repository then we can have 2 basic schemes (as far as I can see):
1. Each page is a file called "<filename>.shardwiki" and all the page's attachments and its children are stored in a subdirectory called "<filename>/"
2. The directory itself contains both the page and its attachments, child-pages are modeled as subdirectories. So we would have a "shardwiki" file (explicitly no specific filename, this is the convention), all attachments are parallell to it and child pages are under "<childpage>/" directories.
3. Each ".shard.md" file is a wiki page, they can be named whatever but need to end in ".shard.md", 
   all binary files in a certain directory _can_ be attachments. Each directory can have an entry file called "shard.md" 
   and when it exists it is used to render the directory itself. 

I opt for the latter design. In both cases some thought needs to go into how we identify pages and refer to the from other pages. How to guarantee a unique name? I tend towards using the page's relative path in the hierarchy for this. This is naturally unique, but it is of course also subject to change. But since we need to deal with change anyhow with renames we need to implement replaces after renames.

Wiki pages are explicitly not called ".shardwiki" so they are easily visible for editing.

Wiki pages are Markdown or similar plain text markup.
- [syntax extensions? includes? constrain syntax? what? see offline wiki requirements in notes]
example of file based syntax:
	/home/boris/foowiki
		shardwiki
		attachment1.pdf
		attachment2.png
		myfirstrootchild/
			shardwiki
		mysecondrootchild/
			shardwiki

## Configuration

* shard server configuration is kept in application.conf HOCON notation based config file
