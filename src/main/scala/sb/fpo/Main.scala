package sb.fpo

import sb.fpo.core.Domain
import sb.fpo.store.inmem.InMemoryStore

import scala.collection.mutable.Map

object Main extends App {
  val initMap = Map(
    1 -> Domain.QAndA(
      Domain.Question("What am I even doing here?", Domain.User("Florian")),
      Nil),
    2 -> Domain
      .QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
    3 -> Domain.QAndA(
      Domain.Question("And if yes how many?", Domain.User("Precht")),
      Nil))
  val db = InMemoryStore(initMap)
  val app = Application(db)
  app.bind
}
