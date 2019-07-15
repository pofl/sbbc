package sb.fpo.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import sb.fpo.store.IStorage

import io.circe.generic.auto._
import io.circe.syntax._

class Server(db: IStorage) {
  lazy val questionRoute: Route =
    pathPrefix("q") {
      pathEnd {
        get {
          complete(getQs)
        } ~ post {
          complete("HELLO")
        }
      } ~ path(IntNumber) { questionId =>
        get {
          complete(getQ(questionId))
        }
      }
    }
  def getQ(id: Int) = db.getQuestion(id).asJson.toString
  def getQs = db.allQuestions().asJson.toString
}
