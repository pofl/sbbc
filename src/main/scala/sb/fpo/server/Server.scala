package sb.fpo.server

import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import sb.fpo.store.IStorage
import sb.fpo.core.Domain
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._

class Server(db: IStorage) {
  import JsonFormats._

  lazy val questionRoute: Route =
    pathPrefix("q") {
      pathEnd {
        get {
          complete(getQs)
        } ~ post {
          entity(as[String]) { body =>
            complete(postQ(body))
          }
        }
      } ~ path(IntNumber) { questionId =>
        get {
          complete(getQ(questionId))
        } ~ post {
          complete("HI")
        }
      }
    }

  def getQ(id: Int) = db.getQuestion(id) match {
    case Some(q) => HttpResponse(entity = QAndAReply.fromDomainQandA(q).asJson.toString)
    case None => HttpResponse(StatusCodes.NotFound)
  }
  def getQs = db.allQuestions().map(QAndAReply.fromDomainQandA(_)).asJson.toString
  def postQ(payload: String) = {
    parse(payload) match {
      case Left(value) => HttpResponse(StatusCodes.BadRequest, entity = value.message)
      case Right(err) =>
        val parsedQuestion = err.as[JsonFormats.PostQuestionRequest]
        parsedQuestion match {
          case Right(q) =>
            val createdIdx = db.postQuestion(q.question, q.poster_id.toInt)
            createdIdx match {
              case None => HttpResponse(StatusCodes.BadRequest, entity = "User doesn't exist")
              case Some(id) => HttpResponse(StatusCodes.OK, entity = id.toString)
            }
          case Left(err) => HttpResponse(StatusCodes.BadRequest, entity = err.message)
        }
    }
  }
}
