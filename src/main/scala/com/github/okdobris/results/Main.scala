package com.github.okdobris.results

import model._
import org.json4s._
import org.json4s.Xml._

import java.io.{Writer, FileWriter}
import scala.util._
import java.nio.charset.StandardCharsets


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
    val inputFile = if (args.nonEmpty) args(0) else "results.xml"
    val outputFile = if (args.length > 1) args(1) else "report.csv"
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

    val mostTeams = data.mostTeams
    // Již od okresních kol se body přidělují podle počtu zúčastněných družstev v nejvíce obsazené kategorii x 2.
    val winPoints = mostTeams._2 * 2

    val teams = data.teams

    val teamNames = data.classResult.flatMap(_.personResult.map(pr => pr.team -> pr.organisation.map(_.name).getOrElse(""))).distinct.toMap

    val clsResults = data.classResult.filterNot(_.isOpen).map { cls =>
      val teamResults = teams.flatMap { team =>
        val teamInClass = cls.personResult.filter(_.team == team)
        Option.when(teamInClass.nonEmpty) {
          val countedResults = teamInClass.flatMap(person => person.result.position.map(_ -> person)).take(2)
          // další závodníci družstva, kteří již nebodují body neberou, ale ani body neumořují.
          val scoredPlaces = countedResults.map(place => place._2 -> (winPoints - (place._1 - 1) max 0))
          (team, scoredPlaces.map(_._2).sum, scoredPlaces)
        }
      }
      cls.`class`.name -> teamResults.sortBy(_._2).reverse
    }


    implicit class WriterOps(private val writer: Writer) {
      def println(s: String): Unit = {
        writer.write(s)
        writer.write("\n")
      }
    }
    val writer = new FileWriter(outputFile, StandardCharsets.UTF_8)
    try {
      // print general header
      writer.println(s"Nejobsazenější kategorie,${mostTeams._1},${mostTeams._2}")
      writer.println(s"Bodů za 1. místo,$winPoints")

      // print warnings: missing ID
      val missingId = data.classResult.flatMap(_.personResult.filter(_.person.id.isBlank).map(_.person))
      for (m <- missingId) {
        writer.println(s"Chybějící id,${m.name}")
      }
      writer.println("\n")

      // print points assigned in each category
      val clsMap = clsResults.toMap
      for (cls <- data.classResult if !cls.isOpen) {
        val clsPoints = clsMap(cls.`class`.name).flatMap(_._3).map(kv => kv._1.person -> kv._2).toMap
        val clsPersons = cls.personResult.map { personResult =>
          personResult -> clsPoints.getOrElse(personResult.person, 0)
        }.sortBy(_._1.result.position.getOrElse(Int.MaxValue))

        writer.println(s"\nKategorie ${cls.`class`.name}\n")
        writer.println("Umístění,Družstvo,Body,Závodník")
        for (p <- clsPersons) {
          writer.println(s"${p._1.result.position.map(_.toString).getOrElse("DISK")},${p._1.team},${p._2},${p._1.fullName}")
        }
      }


      // print team results report
      for ((cls, clsTeams) <- clsResults) {
        writer.println(s"\n\nKategorie $cls\n")
        writer.println("Body,Družstvo,Kdo bodoval")
        for ((team, score, teamResults) <- clsTeams) {
          writer.println(s"$score,\"$team (${teamNames(team)})\",\"${teamResults.map(ps => ps._1.fullName + ":" + ps._2).mkString(" ")}\"")
        }
      }

    } finally {
      writer.close()
    }


  }
}
