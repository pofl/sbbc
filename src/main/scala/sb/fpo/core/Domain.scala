package sb.fpo.core

object Domain {
  case class QAndA(question: Question, answers: List[Answer])
  case class Question(text: String, author: User)
  case class Answer(text: String, author: User)
  case class User(name: String)
}
