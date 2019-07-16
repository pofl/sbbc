package sb.fpo.store.inmem

import sb.fpo.core.Domain
import sb.fpo.store.IStorage

import collection.mutable.Map

object InMemoryStore {
  def apply() = {
    val users = Map(
      1 -> Domain.User("Florian"),
      2 -> Domain.User("Marvin"),
      3 -> Domain.User("Precht"),
      4 -> Domain.User("Jesus"))
    new InMemoryStore(Map.empty, users)
  }

  def apply(questions: Map[Int, Domain.QAndA], users: Map[Int, Domain.User]): InMemoryStore =
    new InMemoryStore(questions, users)
}
class InMemoryStore(
  private val questions: Map[Int, Domain.QAndA],
  private val users: Map[Int, Domain.User]) extends IStorage {

  override def getQuestion(id: Int): Option[Domain.QAndA] = questions.get(id)

  override def allQuestions(): Seq[Domain.QAndA] = questions.values.toSeq

  override def postQuestion(question: String, authorID: Int): Option[Int] = {
    val author: Option[Domain.User] = users.get(authorID)
    author match {
      case None => None
      case Some(author) =>
        val idx = questions.keys.max + 1
        questions(idx) = Domain.QAndA(Domain.Question(question, author), List.empty)
        Some(idx)
    }
  }

  override def postAnswer(questionId: String, answer: String, authorId: String): Either[String, Unit] = {
    val questionIdx = questionId.toInt
    val question: Either[String, Domain.QAndA] = questions.get(questionIdx).toRight("Question doesn't exist")
    val author: Either[String, Domain.User] = users.get(authorId.toInt).toRight("Author doesn't exist")
    for {
      q <- question
      a <- author
    } yield {
      val updatedQA: Domain.QAndA = q.copy(answers = q.answers :+ Domain.Answer(answer, a))
      questions(questionIdx) = updatedQA
      ()
    }
  }

}
