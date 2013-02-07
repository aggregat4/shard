package a4.shard.routing

import a4.shard.routing._ // for the package objects

case class Request

case class Status(val status: Int)

object Status{
  def Ok = Status(200)
  def NotFound = Status(404)
  def Error = Status(500)
}

case class Response(val status: Status)

sealed trait Method
case class ANY extends Method
case class GET extends Method
case class POST extends Method
case class PUT extends Method
case class DELETE extends Method

case class Route(val path: Path, val method: Method, val action: Action)

case class Path(val path: String, val actions: Action*) {
  def routes(actions: (Method, Action)*) = actions.map{ action: (Method, Action) => Route(this, action._1, action._2) }.toList
}

import Status._ 

object SomeControllerLikeThing {
  def doSomething(req: Request) = Response(Ok)
}

class Foo {
  def bar(req: Request) = Response(NotFound)
}

object Router {
  val routes =
    Path("/").routes(
      (GET(),  SomeControllerLikeThing.doSomething),
      (POST(), new Foo().bar)
    ) :::
	Path("/foo").routes(
	  (GET(),  SomeControllerLikeThing.doSomething)
	)
}