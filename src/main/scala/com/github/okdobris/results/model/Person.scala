package com.github.okdobris.results.model

case class Person(
  idORIS: Option[String],
  id: String = "",
  name: Name
)