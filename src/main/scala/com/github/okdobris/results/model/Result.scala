package com.github.okdobris.results.model

import java.time._

import com.rallyhealth.weepickle.v1.WeePickle._
object Result {
  implicit val pickleTo: To[Result] = macroTo
}

case class Result(
  startTime: String, // TODO: parse as ZonedDateTime
  finishTime: String,
  time: Int,
  position: Option[Int],
  status: String,
  splitTime: List[SplitTime]
)
