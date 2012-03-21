# Dependencies
- [Scalatest](http://www.scalatest.org/) and the [FunSuite](http://www.scalatest.org/getting_started_with_fun_suite)
- [JGit](http://eclipse.org/jgit/)
- [JNotify](http://jnotify.sourceforge.net/)
- [Scalatra](https://github.com/scalatra/scalatra) (or rather Finagle?)
- [Scalate Jade](http://scalate.fusesource.org/documentation/jade.html)
- [TypeSafe Config](https://github.com/typesafehub/config)

# What it is:
- a shard repository is a directory
- a shard repository is a git repository
- multiple repositories can be managed
- shard-server watches the directory and subdirectories using jnotify and commits each change to git
- files are edited on the hard drive directly, outside of shard
- shard offers a web ui to manage repositories and view and edit shard wikis
  [manage functionality?]
  - new repository
  - view as wiki
  - view as files (tree, versions)
  - show sync status (last sync, merge conflicts if any, last updated files (?))
  - search files (someday, lucene Elasticsearch?)
  
# Shard Wiki
- repositories can contain files with a .shardwiki extension that are Jade+Markdown files
- shard wiki files must have the same name prefix as the directory they are contained in
- each wiki page requires a directory and each directory can have only 1 wiki page
- the toplevel shard wiki file is the root of the wiki hierarchy
- attachments are stored alongside the wiki page
  [syntax extensions? includes? constrain syntax? what? see offline wiki requirements in notes]

# Remote Repositories
- shard repositories can be synced with a remote master
  [how to handle merge conflicts? check out what git does with those]

# Configuration
- shard server configuration is kept in application.conf HOCON notation based config file
