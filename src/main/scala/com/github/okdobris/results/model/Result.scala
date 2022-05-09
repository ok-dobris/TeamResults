package com.github.okdobris.results.model

import java.time._

case class Result(
  startTime: String, // TODO: parse as ZonedDateTime
  finishTime: String,
  time: Int,
  position: Option[Int],
  status: String,
  splitTime: List[SplitTime]
)
