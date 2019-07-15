package sb.fpo.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import sb.fpo.BaseSpec
import sb.fpo.core.Domain
import sb.fpo.store.inmem.InMemoryStore

import scala.collection.mutable.Map
import io.circe.generic.auto._
import io.circe.syntax._
import sb.fpo.server.Server.QAndAReply
import io.circe.generic.auto._
import io.circe.syntax._

class ServerTest extends BaseSpec with ScalatestRouteTest {

  "The listQuestions endpoint" when {

    "there are no questions" should {
      val store = InMemoryStore(Map.empty)
      val srv = new Server(store)

      "return an empty response" in {
        Get("/q") ~> srv.questionRoute ~> check {
          responseAs[String].trim should fullyMatch regex """\[\s*\]"""
        }
      }
    }

    "there are questions" should {
      val initMap = Map(
        1 -> Domain.QAndA(
          Domain.Question("What am I even doing here?", Domain.User("Florian")),
          Nil),
        2 -> Domain
          .QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
        3 -> Domain.QAndA(
          Domain.Question("And if yes how many?", Domain.User("Precht")),
          Nil))
      val store = InMemoryStore(initMap)
      val srv = new Server(store)

      "return all questions and their responses" in {
        Get("/q") ~> srv.questionRoute ~> check {
          responseAs[String].trim shouldEqual initMap.values.map(QAndAReply.fromDomainQandA(_)).asJson.toString
        }
      }
    }

  }

  "The showQuestion endpoint" when {
    val initMap = Map(
      1 -> Domain.QAndA(
        Domain.Question("What am I even doing here?", Domain.User("Florian")),
        Nil),
      2 -> Domain
        .QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
      3 -> Domain.QAndA(
        Domain.Question("And if yes how many?", Domain.User("Precht")),
        Nil))
    val store = InMemoryStore(initMap)
    val srv = new Server(store)

    "the requested question doesn't exist" should {
      "respond with Not Found" in {
        Get("/q/4") ~> srv.questionRoute ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }
    }
    "the requested question exists" should {
      "return that question" in {
        Get("/q/1") ~> srv.questionRoute ~> check {
          responseAs[String].trim shouldEqual Server.QAndAReply.fromDomainQandA(initMap(1)).asJson.toString.trim
        }
      }
    }
  }

}
