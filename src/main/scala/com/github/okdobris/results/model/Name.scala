package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
object Name {
  implicit val pickleTo: To[Name] = macroTo
}
case class Name(family: String, given: String = "")
