package com.github.okdobris.results

import model._
import org.json4s._
import org.json4s.Xml._

import scala.util._


object Main {
  implicit val formats: Formats = DefaultFormats ++ org.json4s.ext.JavaTimeSerializers.all

  /**
   * there may be multiple id fields, we want to replace the first one before type->ORIS entry, as that is the field represented in XML as "<Id type="ORIS">"
   */
  object HandleOrisId {
    def unapply(fields: Seq[(String, JValue)]): Option[Seq[(String, JValue)]] = {
      val idIndex = fields.zip(fields.drop(1)).indexWhere {
        case ("id" -> _, "type" -> JString("ORIS")) =>
          true
        case _ =>
          false
      }
      if (idIndex >=0) {
        val id = fields(idIndex)._2
        Some(fields.patch(idIndex, Seq("idORIS" -> id), 2))
      } else {
        None
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val inputFile = if (args.nonEmpty) args(0) else "data/results.xml"
    val outputFile = if (args.length > 1) args(1) else "data/report.csv"
    val xml = scala.xml.XML.loadFile(inputFile)
    // some fields should be interpreted as numbers if possible

    val numericFields = Set("time", "position", "controlCode")
    val json = toJson(xml).camelizeKeys.transformField {
      case originalValue@(name, JString(value)) =>
        if (numericFields.contains(name)) {
          Try(value.toInt) match {
            case Success(value) =>
              (name, JInt(value))
            case Failure(exception) =>
              originalValue
          }
        } else originalValue
    }.transform {
      case JObject(HandleOrisId(fields)) =>
        JObject(fields:_*)
    }

    val data = json match {
      case JObject(obj) =>
        obj.head._2.extract[ResultList]
    }

    val winPoints = data.winnerPoints

    val teams = data.teams

    val teamNames = data.classResult.flatMap(_.personResult.map(pr => pr.team -> pr.organisation.map(_.name).getOrElse(""))).distinct.toMap

    val clsResults = data.classResult.filterNot(_.isOpen).map { cls =>
      val teamResults = teams.flatMap { team =>
        val teamInClass = cls.personResult.filter(_.team == team)
        Option.when(teamInClass.nonEmpty) {
          val countedResults = teamInClass.flatMap(person => person.result.position.map(_ -> person)).take(2)
          // další závodníci družstva, kteří již nebodují body neberou, ale ani body neumořují.
          val scoredPlaces = countedResults.map(place => place._2 -> (winPoints - (place._1 - 1) max 0)).map(ps => ps._1.fullName -> ps._2)
          (team, scoredPlaces.map(_._2).sum, scoredPlaces)
        }
      }
      cls.`class`.name -> teamResults.sortBy(_._2).reverse
    }

    println(s"Bodů za 1. místo: $winPoints")


    for ((cls, clsTeams) <- clsResults) {
      println(s"\n\nKategorie $cls\n")
      for ((team, score, teamResults) <- clsTeams) {
        println(s"$team (${teamNames(team)}): $score (${teamResults.map(ps => ps._1 + ":" + ps._2).mkString(",")})")
      }
    }
  }
}
