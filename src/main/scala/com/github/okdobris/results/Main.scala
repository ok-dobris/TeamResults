package com.github.okdobris.results

import model._
import org.json4s._
import org.json4s.Xml._

import java.io.{File, FileOutputStream, FileWriter, OutputStreamWriter}
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

  def processOneInput(inputFile: String): Unit = {
    val dot = inputFile.lastIndexOf('.')
    val shortInputName = if (dot >= 0) inputFile.take(dot) else inputFile
    val outputFile = shortInputName + ".csv"
    val outputHtmlFile = shortInputName + ".html"

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
      case _ =>
        throw new UnsupportedOperationException("Bad input file format")
    }

    val mostTeams = data.mostTeams
    // Ji?? od okresn??ch kol se body p??id??luj?? podle po??tu z????astn??n??ch dru??stev v nejv??ce obsazen?? kategorii x 2.
    val winPoints = mostTeams._2 * 2

    val teams = data.teams

    val teamNames = data.classResult.flatMap(_.personResult.map(pr => pr.person.teamCode -> pr.organisation.map(_.name).getOrElse(""))).distinct.toMap

    def teamFullName(team: String) = team

    val clsResults = data.classResult.filterNot(_.isOpen).map { cls =>

      // dal???? z??vodn??ci dru??stva, kte???? ji?? neboduj?? body neberou, ale ani body neumo??uj??.
      val scoringPlaces = cls.personResult.filter(_.result.position.nonEmpty).groupBy(_.team).toList.flatMap(_._2.take(2)).sortBy(_.result.position)
      val scoresPrelim = scoringPlaces.zipWithIndex.map { case (result, scoreRank) =>
        result -> (winPoints - scoreRank max 0)
      }
      // when two racers have both the same place and they both score, they need to get the same (higher) score
      val scoreFromPosition = scoresPrelim.groupBy(_._1.result.position).map { case (pos, scores) =>
        pos -> scores.map(_._2).max
      }

      val scores = scoresPrelim.map { case (person, score) =>
        person -> scoreFromPosition(person.result.position)
      }
      val teamResults = teams.flatMap { team =>
        // list only teams participating in the class
        Option.when(cls.personResult.exists(_.team == team)) {
          val teamInClass = scores.filter(_._1.team == team)
          val totalScore = teamInClass.map(_._2).sum
          val totalTime = teamInClass.map(_._1.result.time).sum
          (team, (totalScore, totalTime), teamInClass)
        }
      }
      cls.`class`.name -> teamResults.sortBy { case (_, (score, time), _) =>
        (-score, time)
      }
    }

    def openFileWriter(file: String) = {
      // new FileWriter(outputFile, StandardCharsets.UTF_8) requires Java 11
      val os = new FileOutputStream(file)
      new OutputStreamWriter(os, StandardCharsets.UTF_8)
    }

    val writerCSV = openFileWriter(outputFile)
    val writerHTML = openFileWriter(outputHtmlFile)


    val writer = new TableWriter.Multi(new TableWriter.CSV(writerCSV), new TableWriter.HTML(writerHTML))
    val clsMap = clsResults.toMap

    try {
      writer.page()
      // print any warnings first

      // print warnings: missing ID
      val missingId = data.classResult.filterNot(_.isOpen).flatMap(_.personResult.filter(pr => pr.person.id.isEmpty && pr.organisation.isEmpty))
      for (m <- missingId) {
        writer.label(s"Chyb?? informace o dru??stvu: ${m.person.fullName}")
      }

      writer._page()


      // print category groups
      val groups = Seq(
        "DH3+DH5" -> Seq("D3", "H3", "D5", "H5", "DI", "HI", "DII", "HII"),
        "DH7+DH9" -> Seq("D7", "H7", "D9", "H9", "DIII", "HIII", "DIV", "HIV"),
        "DS+HS" -> Seq("DS", "HS", "DV", "HV")
      )

      for ((groupName, group) <- groups) {
        val g = group.flatMap { clsName =>
          clsMap.get(clsName).toList.flatten
        }

        val teamGroupResults = g.groupBy(_._1).toList
        val teamScores = teamGroupResults.map(kv => (kv._1, kv._2.map(_._2._1).sum, kv._2.map(_._2._2).sum)).sortBy(kv => (-kv._2, kv._3)) // sort by score reversed, then by time

        writer.page()
        writer.label(s"Kategorie $groupName")

        writer.table()
        writer.tr().th("Body").th("Dru??stvo").th("Celk.??as")._tr()

        for ((team, score, time) <- teamScores) {
          writer.tr().td(score).td(teamFullName(team)).td(time)._tr()
        }
        writer._table()
        writer._page()


      }
      writer.hr()

      // print team results report
      for ((cls, clsTeams) <- clsResults) {
        writer.page()
        writer.label(s"Kategorie $cls")
        writer.table()
        writer.tr().th("Body").th("Dru??stvo").th("Kdo bodoval").th("Celk.??as")._tr()
        for ((team, score, teamResults) <- clsTeams) {
          writer
            .tr()
            .td(score._1)
            .td(teamFullName(team))
            .td(teamResults.map(ps => s"${ps._1.fullName}:${ps._2}b-${ps._1.result.time}s").mkString(" "))
            .td(score._2)
            ._tr()
        }
        writer._table()
        writer._page()
      }

      writer.hr()

      // print general information
      writer.page()
      writer.label(s"Nejobsazen??j???? kategorie: ${mostTeams._1} ${mostTeams._2}")
      writer.label(s"Bod?? za 1. m??sto: $winPoints")

      writer._page()


      // print points assigned in each category
      for (cls <- data.classResult if !cls.isOpen) {
        val clsPoints = clsMap(cls.`class`.name).flatMap(_._3).map(kv => kv._1.person -> kv._2).toMap
        val clsPersons = cls.personResult.map { personResult =>
          personResult -> clsPoints.getOrElse(personResult.person, 0)
        }.sortBy(_._1.result.position.getOrElse(Int.MaxValue))

        writer.page()
        writer.label(s"\nKategorie ${cls.`class`.name}\n")
        writer.table()
        writer.tr().th("Um??st??n??").th("Dru??stvo").th("Body").th("Z??vodn??k")._tr()
        for (p <- clsPersons) {
          writer.tr(if (p._2 != 0) "score" else "").td(s"${p._1.result.position.map(_.toString).getOrElse("DISK")}").td(teamFullName(p._1.team)).td(p._2).td(p._1.fullName)._tr()
        }
        writer._table()
        writer._page()
      }

      writer.hr()




    } finally {
      writer.close()
    }


  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      val inputFiles = new File(".").listFiles.filter(_.isFile).filter(_.getName.toLowerCase.endsWith(".xml"))
      for (file <- inputFiles) {
        processOneInput(file.getPath)
      }
    } else {
      processOneInput(args.head)
    }
  }

}
