# This is Shard
I like wikis as a means for publishing, managing and creating content. I got my first limited exposure with
the [c2 wiki](http://c2.com/cgi/wiki) and [Mediawiki](https://www.mediawiki.org/wiki/MediaWiki) but grew to
love them for actually managing a knowledge base using [Confluence](https://www.atlassian.com/software/confluence)
at a previous job. Though not perfect by any means, it is a nice balance between power, ease of use and getting out
of your way.

As I have been dabbling in Scala for a while, and wanting to gather more experience with the language I decided to
tackle a "real" project, scratch a personal itch and do something bigger with the language. Shard is a first attempt
at tackling some ideas I had about wikis and managing information and it seemed like an ideal project to start with.

As this is a learning project for a programming language, I didn't want to get bogged down in learning frameworks and
fiddling with configuration. Easy bootstrapping is paramount and less black boxes help intensely. This doesn't mean that
I reimplemented everything myself (see the Dependencies section below) but it does mean that Shard contains a tiny
homebrew "web framework". Yes, I know. Learning, remember.

As I write Shard, I am starting to realize that the actual product I want for information management isn't quite this
thing but something similar. I will try to finish some core features in Shard, but may move onto Shard 2 at some point.

Some key ideas behind Shard:
- Natural text based markup language (Markdown in this case, but the exact dialect is not that important): it's the only
way. Rich text editors have never worked and will never work.
- Live preview: rich text editors are crap, but seeing what the finished product looks like is nice. Therefore
auto-updating, auto-scrolling live preview side-by-side with the source.
- All information is available offline, but it can sync to a central store: it seemed strange to me that almost no one
combines distributed version control with wikis. It seems like a natural fit. Merge conflicts may present a UI problem
but I think it's solvable. And something like Confluence runs into this anyway with its concurrent editing detection.
- Everything is just files: I want to make the threshold to adding information or starting with an existing mountain
of directories as low as possible. A Wiki is just a directory (and its subdirectories) in the filesystem.
- Robustness against out of band editing: If we are a tree of files on disk, it makes sense that someone can edit those
files outside of Shard. This should not bother Shard, it should always try to deal with such changes gracefully, in the
end they are just files.
- Good search: full text search that is fast, up to date and accurate is essential.

# Running Shard

TODO

# Dependencies, planned and current
I retained some of the original choices and ideas I had in mind for some of the libraries I would need. They may be of
historic interest, or serve as a warning to future readers:
- Unit testing with ~~[Scalatest](http://www.scalatest.org/) and the [FunSuite](http://www.scalatest.org/getting_started_with_fun_suite)~~
  - nope, using plain old JUnit with FEST, I found it easier to get started
- Git integration with [JGit](http://eclipse.org/jgit/)
  - not yet
- Getting file system notifications with [JNotify](http://jnotify.sourceforge.net/)
  - not yet
- ~~[Scalatra](https://github.com/scalatra/scalatra) (or rather Finagle?)~~ as my tiny web framework
  - ~~nope, Using Spray~~
    - nope, using a simple home brew solution, Spray DSl was horrible to use at the time, Scalatra seemed overkill
- ~~[Scalate Jade](http://scalate.fusesource.org/documentation/jade.html)~~ as a template library
  - ~~Using [Scalate Mustache](https://scalate.github.io/scalate/documentation/mustache.html#features)~~
    - ~~nope, using [Mustache-java](https://github.com/spullara/mustache.java)~~
      - nope, using [Scalatags](https://github.com/lihaoyi/scalatags), scala based library for code-based templating
- [TypeSafe Config](https://github.com/typesafehub/config) for configuration

# What Shard will be
These were my original ideas, before starting the project about what I wanted to accomplish. This may no longer be the
actual state of the project. I will add a "What Shard currently is" section later on:
- a shard repository is a directory
- a shard repository is a git   repository
- multiple repositories can be managed
- shard-server watches the directory and subdirectories using jnotify and commits each change to git
- files are edited on the hard drive directly, outside of shard
- shard offers a web ui to manage repositories and view and edit shard wikis
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
  - shard repositories can be synced with a remote master (how to handle merge conflicts? check out what git does with those)

# Shard Wiki design
## File and Directory Structure
There are some possibilities with designing a wiki as a set of files and directories. Assuming there is a root directory with our repository then we can have 2 basic schemes (as far as I can see):

1. Each page is a file called "<filename>.shardwiki" and all the page's attachments and its children are stored in a subdirectory called "<filename>/"
2. The directory itself contains both the page and its attachments, child-pages are modeled as subdirectories. So we would have a "shardwiki" file (explicitly no specific filename, this is the convention), all attachments are parallell to it and child pages are under "<childpage>/" directories.
3. Each ".shard.md" file is a wiki page, they can be named whatever but need to end in ".shard.md", 
   all binary files in a certain directory _can_ be attachments. Each directory can have an entry file called "shard.md" 
   and when it exists it is used to render the directory itself. 

I opt for the third design. In both cases some thought needs to go into how we identify pages and refer to the from other pages. How to guarantee a unique name? I tend towards using the page's relative path in the hierarchy for this. This is naturally unique, but it is of course also subject to change. But since we need to deal with change anyhow with renames we need to implement replaces after renames.

## Configuration
* shard server configuration is kept in an application.conf, HOCON notation based config file
