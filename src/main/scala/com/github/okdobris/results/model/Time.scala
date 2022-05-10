package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
import com.rallyhealth.weepickle.v1.implicits.key
object Time {
  implicit val pickleTo: To[Time] = macroTo
}

case class Time(@key("Date") date: String)
