package a4.shard

trait RequestHandler {
  
  def matches(path: String) : Boolean
  
}