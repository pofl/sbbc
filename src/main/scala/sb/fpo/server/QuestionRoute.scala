package sb.fpo.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import sb.fpo.predef_example.UserRegistryActor.{ ActionPerformed, CreateUser }
import sb.fpo.Users

import scala.concurrent.Future

object QuestionRoute {
  lazy val userRoutes: Route =
    pathPrefix("q") {
      pathEnd {
        get {
          complete("HI")
        } ~
          post {
            complete("HELLO")
          }
      } ~
        path(IntNumber) { questionId =>
          get {
            complete("CIAO")
          }
        }
    }
}
