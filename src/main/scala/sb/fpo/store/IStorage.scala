package sb.fpo.store

import sb.fpo.core.Domain._

trait IStorage {
  def getQuestion(id: Int): Option[QAndA]
  def allQuestions(): Seq[QAndA]
}
