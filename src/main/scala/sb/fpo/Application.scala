package sb.fpo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import sb.fpo.server.Server
import sb.fpo.store.IStorage

import scala.concurrent.ExecutionContext

object Application {
  def apply(db: IStorage): Application = new Application(db)
}
class Application(db: IStorage) {
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val srv = new Server(db)

  def bind = Http().bindAndHandle(srv.questionRoute, "localhost", 8080)
}
