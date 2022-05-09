package com.github.okdobris.results.model

case class PersonResult(
  person: Person,
  organisation: Option[Organisation],
  result: Result
)
