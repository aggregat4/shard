# Scala usage
- I have a bunch of instances where I have some kind of collection being generated after a few map invocations where
  I then explicitly convert it to a list with toList to have some sensible return type. Is this a good idea? Are there
  better approaches?