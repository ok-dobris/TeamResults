package com.github.okdobris.results
package model

case class ResultList(
  iofVersion: String,
  creator: String,
  event: Event,
  classResult: List[ClassResult]
)
