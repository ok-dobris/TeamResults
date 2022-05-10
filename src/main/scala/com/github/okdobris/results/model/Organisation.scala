package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
object Organisation {
  implicit val pickleTo: To[Organisation] = macroTo
}

case class Organisation(name: String)