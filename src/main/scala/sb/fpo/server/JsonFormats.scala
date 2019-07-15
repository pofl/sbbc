package sb.fpo.server

import sb.fpo.core.Domain

/** These are the case classes that are converted to and from JSON. They define the REST API payloads. */
object JsonFormats {
  object QAndAReply {
    def fromDomainQandA(qa: Domain.QAndA) = QAndAReply(qa.question.text, qa.question.author.name, qa.answers)
  }
  case class QAndAReply(
    question: String,
    author: String,
    answers: Seq[Domain.Answer])
}
