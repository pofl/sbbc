package sb.fpo.store.inmem

import sb.fpo.BaseSpec
import sb.fpo.core.Domain
import collection.mutable.Map

class InMemoryStoreTest extends BaseSpec {

  "in-memory store" should {
    "retrieve an existing QandA" in {
      val q = Domain.QAndA(Domain.Question("What am I even doing here?", Domain.User("Florian")), Nil)
      val store = InMemoryStore(Map(1 -> q))
      store.getQuestion(1) shouldEqual (Some(q))
    }

    "return all Q&As when asked for all Q&As" in {
      val map = Map(
        1 -> Domain.QAndA(Domain.Question("What am I even doing here?", Domain.User("Florian")), Nil),
        2 -> Domain.QAndA(Domain.Question("Who am I?", Domain.User("Marvin")), Nil),
        3 -> Domain.QAndA(Domain.Question("And if yes how many?", Domain.User("Precht")), Nil))
      val store = InMemoryStore(map)
      store.allQuestions() shouldEqual (map.values.toSeq)
    }
  }

}
