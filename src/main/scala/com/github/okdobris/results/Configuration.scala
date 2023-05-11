package com.github.okdobris.results

import pureconfig._
import pureconfig.error.{CannotReadFile, ConfigReaderFailures, ConvertFailure}
import pureconfig.generic.semiauto._

import java.io.FileNotFoundException

case class Configuration(
  scoringFirst: Int = 2,
  categories: Map[String, Seq[String]] = Map(
    "DH3+DH5" -> Seq("D3", "H3", "D5", "H5", "DI", "HI", "DII", "HII"),
    "DH7+DH9" -> Seq("D7", "H7", "D9", "H9", "DIII", "HIII", "DIV", "HIV"),
    "DS+HS" -> Seq("DS", "HS", "DV", "HV")
  )
)

object Configuration {

  implicit val deriverReader: ConfigReader[Configuration] = deriveReader

  def apply(path: String): Configuration = {

    val read = ConfigSource.file(path).load[Configuration]

    read.left.foreach { err =>
      err.head match {
        case CannotReadFile(_,  _) =>
          // file not found is expected - continue normally
        case _ =>
          throw new UnsupportedOperationException(err.toList.mkString(","))
      }
    }

    read.getOrElse(Configuration(2))
  }
}
