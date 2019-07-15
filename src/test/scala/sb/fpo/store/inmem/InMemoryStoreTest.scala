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
  }

}
