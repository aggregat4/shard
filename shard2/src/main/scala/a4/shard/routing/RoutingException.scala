package a4.shard.routing

import java.lang.Exception

class RoutingException extends Exception

	class RequestParsingException extends RoutingException
	
		class UnknownMethodException(val method: String) extends RequestParsingException
	
	class UnknownRouteException extends RoutingException
	
	