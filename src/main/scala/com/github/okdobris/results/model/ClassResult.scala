package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
import com.rallyhealth.weepickle.v1.implicits.key
object ClassResult {
  implicit val pickleTo: To[ClassResult] = macroTo
}

case class ClassResult(
  //@key("Class") `class`: Class,
  @key("PersonResult") personResult: List[PersonResult]
)
