package sb.fpo.core

object Domain {
  case class QAndA(q: Question, a: List[Answer])
  case class Question(q: String, user: User)
  case class Answer(text: String, user: User)
  case class User(name: String)
}
