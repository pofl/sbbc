package sb.fpo.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import sb.fpo.BaseSpec
import sb.fpo.core.Domain
import sb.fpo.store.inmem.InMemoryStore

import scala.collection.mutable.Map
import io.circe.generic.auto._
import io.circe.syntax._

class ServerTest extends BaseSpec with ScalatestRouteTest {

  val initQMap = Map(
    1 -> Domain.QAndA(
      Domain.Question("What am I even doing here?", Domain.User("Florian")),
      Nil),
    2 -> Domain
      .QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
    3 -> Domain.QAndA(
      Domain.Question("And if yes how many?", Domain.User("Precht")),
      Nil))

  val initUMap = Map(
    1 -> Domain.User("Florian"),
    2 -> Domain.User("Marvin"),
    3 -> Domain.User("Precht"))

  "The listQuestions endpoint" when {

    "there are no questions" should {
      val store = InMemoryStore(Map.empty, Map.empty)
      val srv = new Server(store)

      "return an empty response" in {
        Get("/q") ~> srv.questionRoute ~> check {
          responseAs[String].trim should fullyMatch regex """\[\s*\]"""
        }
      }
    }

    "there are questions" should {
      val store = InMemoryStore(initQMap, initUMap)
      val srv = new Server(store)

      "return all questions and their responses" in {
        Get("/q") ~> srv.questionRoute ~> check {
          responseAs[String].trim shouldEqual initQMap.values.map(JsonFormats.QAndAReply.fromDomainQandA(_)).asJson.toString
        }
      }
    }

  }

  "The showQuestion endpoint" when {
    val store = InMemoryStore(initQMap, initUMap)
    val srv = new Server(store)

    "the requested question doesn't exist" should {
      "respond with Not Found" in {
        Get("/q/100") ~> srv.questionRoute ~> check {
          status shouldEqual StatusCodes.NotFound
        }
      }
    }

    "the requested question exists" should {
      "return that question" in {
        Get("/q/1") ~> srv.questionRoute ~> check {
          responseAs[String].trim shouldEqual JsonFormats.QAndAReply.fromDomainQandA(initQMap(1)).asJson.toString.trim
        }
      }
    }
  }

  "The postQuestion route should work for the happy path" in {
    val store = InMemoryStore(initQMap, initUMap)
    val srv = new Server(store)
    Post(uri = "/q", content = """{"question": "How did I get here", "poster_id": "1"}""") ~> srv.questionRoute ~> check {
      status shouldEqual StatusCodes.OK
    }
  }

}
