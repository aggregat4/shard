package a4.shard

import java.io.File

import com.typesafe.config.Config

case class Configuration(config: Config) {
	
  import scala.collection.JavaConversions._
  
  val wikis = config.getConfigList("wikis").toList.map{
    co: Config => Wiki(co.getString("id"), co.getString("name"), new File(co.getString("location")))
  }
 
  def wikiById(id: String) : Option[Wiki] = wikis.find(w => w.id == id) 
  
}