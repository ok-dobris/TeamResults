package com.github.okdobris.results

import com.typesafe.config.ConfigFactory

import java.io.{File, FileInputStream, FileNotFoundException}
import java.util.Properties

case class Configuration(
  scoring_first: Int = 2
)

object Configuration {

  def apply(path: String): Configuration = {
    System.setProperty("config.file", path)
    val config = ConfigFactory.load()
    try {

      implicit class GetProp(c: Configuration) {
        def process(prop: String)(f: (Configuration, String) => Configuration): Configuration = {
          Option(config.getString(prop)).map { value =>
            f(c, value)
          }.getOrElse(c)
        }
      }

      Configuration()
        .process("scoring_first")((c, v) => c.copy(scoring_first = v.toInt))

    } catch {
      case ex: FileNotFoundException => // when file is not found, use defaults
        Configuration()
    }
  }
}
