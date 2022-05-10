package com.github.okdobris.results.model

import com.rallyhealth.weepickle.v1.WeePickle._
import com.rallyhealth.weepickle.v1.implicits.key

object Class {
  implicit val pickleTo: To[Class] = macroTo
}
case class Class(
  @key("Sex") sex: String,
  @key("Id") id: Int,
  @key("Name") name: String
)

