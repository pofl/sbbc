package sb.fpo.store.inmem

import sb.fpo.BaseSpec
import sb.fpo.core.Domain
import collection.mutable.Map

class InMemoryStoreTest extends BaseSpec {

  val qmap = Map(
    1 -> Domain.QAndA(Domain.Question("What am I even doing here?", Domain.User("Florian")), Nil),
    2 -> Domain.QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
    3 -> Domain.QAndA(Domain.Question("And if yes how many?", Domain.User("Precht")), Nil))
  val umap = Map(
    1 -> Domain.User("Florian"),
    2 -> Domain.User("Marvin"),
    3 -> Domain.User("Precht"))

  "in-memory store" should {
    "retrieve an existing QandA" in {
      val u = Domain.User("Florian")
      val q = Domain.QAndA(Domain.Question("What am I even doing here?", u), Nil)
      val store = InMemoryStore(Map(1 -> q), Map(1 -> u))
      store.getQuestion(1) shouldEqual Some(q)
    }

    "return all Q&As when asked for all Q&As" in {
      val store = InMemoryStore(qmap, umap)
      store.allQuestions() shouldEqual qmap.values.toSeq
    }

    "handle correct postQuestion calls" in {
      val store = InMemoryStore(qmap, umap)
      val idx: Option[Int] = store.postQuestion("How did I get here?", 1)
      assert(idx.isInstanceOf[Some[Int]])
      idx match {
        case Some(id) =>
          val qa: Option[Domain.QAndA] = store.getQuestion(id)
          qa.isInstanceOf[Some[Domain.QAndA]] shouldEqual true
        case None => fail()
      }
    }
  }

}
