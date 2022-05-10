package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
object Person {
  implicit val pickleTo: To[Person] = macroTo
}

case class Person(
  name: Name
)