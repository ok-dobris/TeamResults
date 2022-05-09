package com.github.okdobris.results
package model

case class ResultList(
  status: String,
  iofVersion: String,
  creator: String,
  event: Event
)