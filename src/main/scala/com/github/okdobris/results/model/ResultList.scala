package com.github.okdobris.results
package model

import com.rallyhealth.weepickle.v1.WeePickle._
import com.rallyhealth.weepickle.v1.implicits.key

case class ResultList(
  status: String,
  iofVersion: String,
  creator: String,
  @key("Event") event: Event,
  @key("ClassResult") classResult: List[ClassResult]
)

object ResultList {
  implicit val pickleTo: To[ResultList] = macroTo
}