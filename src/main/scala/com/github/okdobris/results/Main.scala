package com.github.okdobris.results

import model._
import org.json4s._
import org.json4s.Xml._

import scala.util.Try


object Main {
  implicit val formats: Formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val inputFile = if (args.nonEmpty) args(0) else "data/results.xml"
    val outputFile = if (args.length > 1) args(1) else "data/report.csv"
    val xml = scala.xml.XML.loadFile(inputFile)
    val json = toJson(xml).camelizeKeys.transformField {
      case ("id", JString(s)) if Try(s.toInt).isSuccess => ("id", JInt(s.toInt))
    }

    val data = json match {
      case JObject(obj) =>
        obj.head._2.extract[ResultList]
    }
    println(data.toString)
  }
}
