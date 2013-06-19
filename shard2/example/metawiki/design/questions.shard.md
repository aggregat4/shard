# Design Questions

## Is this application single-user?
Can we assume that the application is always single user, i.e. we can make the assumption that each request comes
from the same client? This simplifies some logic for tracking things like flash messages but it also prohibits 
running Shard from a central server only.

Even though the basic point of shard is to be able to take the wiki, the files, documentation, whatever offline, 
it may be nice to have a central instance (optionally maybe even read-only?) that allows people to just access it.

## Pages have no title
What is a page's title? Currently we can't show anthing in lists (context list) or trees for navigation. Is that
a problem? Should we attempt to parse titles on the fly?