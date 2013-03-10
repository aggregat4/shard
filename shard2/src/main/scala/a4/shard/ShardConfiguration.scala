package a4.shard

import com.typesafe.config.Config
import com.typesafe.config.ConfigObject

case class ShardConfiguration(config: Config) {
	
  import scala.collection.JavaConversions._
  
  val wikis = config.getConfigList("wikis").toList.map{
    co: Config => Wiki(co.getString("id"), co.getString("name"), co.getString("location"))}
 
  def wikiById(id: String) : Option[Wiki] = wikis.find(w => w.id == id) 
  
}