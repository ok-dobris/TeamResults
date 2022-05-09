package com.github.okdobris.results

import model._

import org.json4s._

import org.json4s.Xml._


object Main {
  implicit val formats: Formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val inputFile = if (args.nonEmpty) args(0) else "data/results.xml"
    val outputFile = if (args.length > 1) args(1) else "data/report.csv"
    val xml = scala.xml.XML.loadFile(inputFile)
    val json = toJson(xml)

    val data = json match {
      case JObject(obj) =>
        obj.head._2.extract[ResultList]
    }
    println(data.toString)
  }
}
