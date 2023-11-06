package com.github.okdobris.results.model

case class Result(
  startTime: String, // TODO: parse as ZonedDateTime
  finishTime: String,
  time: Int,
  position: Option[Int],
  status: Option[String],
  splitTime: List[SplitTime]
)
