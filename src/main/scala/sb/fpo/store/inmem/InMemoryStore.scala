package sb.fpo.store.inmem

import sb.fpo.core.Domain.QAndA
import sb.fpo.store.IStorage

import collection.mutable.Map
import scala.collection.mutable

object InMemoryStore {
  def apply() = new InMemoryStore(Map.empty)

  def apply(list: mutable.Map[Int, QAndA]): InMemoryStore =
    new InMemoryStore(list)
}
class InMemoryStore(private val list: Map[Int, QAndA]) extends IStorage {

  override def getQuestion(id: Int): Option[QAndA] = list.get(id)

  override def allQuestions(): Seq[QAndA] = list.values.toSeq

}
