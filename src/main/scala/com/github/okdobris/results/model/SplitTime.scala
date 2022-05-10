package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
object SplitTime {
  implicit val pickleTo: To[SplitTime] = macroTo
}

case class SplitTime(
  controlCode: Int,
  time: Option[Int]
)