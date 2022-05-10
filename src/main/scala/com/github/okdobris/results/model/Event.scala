package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
import com.rallyhealth.weepickle.v1.implicits.key

object Event {
  implicit val pickleTo: To[Event] = macroTo
}

case class Event(
  //@key("Id") id: Int,
  @key("Name") name: String,
  @key("StartTime") startTime: Time
)

