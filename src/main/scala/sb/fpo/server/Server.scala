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
          entity(as[String]) { body =>
            complete(postA(body))
          }
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
          case Left(err) => HttpResponse(StatusCodes.BadRequest, entity = err.message)
          case Right(q) =>
            val createdIdx = db.postQuestion(q.question, q.poster_id.toInt)
            createdIdx match {
              case None => HttpResponse(StatusCodes.BadRequest, entity = "User doesn't exist")
              case Some(id) => HttpResponse(StatusCodes.Created, entity = id.toString)
            }
        }
    }
  }
  def postA(payload: String) = {
    parse(payload) match {
      case Left(err) => HttpResponse(StatusCodes.BadRequest, entity = err.message)
      case Right(value) =>
        val parsedQuestion = value.as[JsonFormats.PostAnswerRequest]
        parsedQuestion match {
          case Left(err) => HttpResponse(StatusCodes.BadRequest, entity = err.message)
          case Right(q) =>
            val result = db.postAnswer(q.question_id, q.text, q.author_id)
            result match {
              case Left(err) => HttpResponse(StatusCodes.BadRequest, entity = err)
              case Right(()) => HttpResponse(StatusCodes.Created)
            }
        }
    }
  }
}
