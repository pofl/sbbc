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

object Server {
  object QAndAReply {
    def fromDomainQandA(qa: Domain.QAndA) = QAndAReply(qa.question.text, qa.question.author.name, qa.answers)
  }
  case class QAndAReply(
    question: String,
    author: String,
    answers: Seq[Domain.Answer])
}
class Server(db: IStorage) {
  import Server._

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
}
