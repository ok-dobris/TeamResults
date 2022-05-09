package com.github.okdobris.results

import model._
import org.json4s._
import org.json4s.Xml._

import scala.util._


object Main {
  implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JavaTimeSerializers.all

  def main(args: Array[String]): Unit = {
    val inputFile = if (args.nonEmpty) args(0) else "data/results.xml"
    val outputFile = if (args.length > 1) args(1) else "data/report.csv"
    val xml = scala.xml.XML.loadFile(inputFile)
    // some fields should be interpreted as numbers if possible
    val numericFields = Set("id", "time", "position", "controlCode")
    val json = toJson(xml).camelizeKeys.transformField { case originalValue@(name, JString(value)) =>
      if (numericFields.contains(name)) {
        Try(value.toInt) match {
          case Success(value) =>
            (name, JInt(value))
          case Failure(exception) =>
            originalValue
        }
      } else originalValue
    }

    val data = json match {
      case JObject(obj) =>
        obj.head._2.extract[ResultList]
    }
    println(data.toString)
  }
}
