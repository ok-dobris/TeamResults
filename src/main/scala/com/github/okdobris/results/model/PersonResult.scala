package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
object PersonResult {
  implicit val pickleTo: To[PersonResult] = macroTo
}

case class PersonResult(
  person: Person,
  organisation: Option[Organisation],
  result: Result
)
