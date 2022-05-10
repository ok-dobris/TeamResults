package com.github.okdobris.results

import com.rallyhealth.weejson.v1.jackson._
import com.rallyhealth.weejson.v1.xml._
import com.rallyhealth.weepickle.v1.WeePickle._
import model._

import java.io.File
import scala.util._


object Main {

  def main(args: Array[String]): Unit = {
    val inputFile = if (args.nonEmpty) args(0) else "data/results.xml"
    val outputFile = if (args.length > 1) args(1) else "data/report.csv"
    val xml = FromXml(new File(inputFile))

    println(xml.transform(ToPrettyJson.string))
    val data = xml.transform(ToScala[ResultList])

    println(data.toString)
  }
}
