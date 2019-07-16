package sb.fpo.store

import sb.fpo.core.Domain._

trait IStorage {
  def getQuestion(id: Int): Option[QAndA]

  def allQuestions(): Seq[QAndA]

  /** @return The ID of the created question */
  def postQuestion(question: String, authorID: Int): Option[Int]
}
