package com.github.okdobris

package object results {
  import model._
  // https://obrozvoj.cz/Pages/PreborSkol/Pravidla.aspx
  implicit class ClassResultOps(private val cls: ClassResult) {
    def isOpen: Boolean = cls.`class`.name.toLowerCase == "open"
  }

  implicit class PersonResultOps(private val personResult: PersonResult) {
    // first three characters of person Id are team code
    def team: String = personResult.person.id.take(3)
    def fullName: String = personResult.person.name.family + " "  + personResult.person.name.given
  }
  implicit class ResultOps(private val data: ResultList) {

    def winnerPoints: Int = {
      // Již od okresních kol se body přidělují podle počtu zúčastněných družstev v nejvíce obsazené kategorii x 2.
      val mostTeams = data.classResult.filterNot(_.isOpen).map { cls =>
        val classTeams = cls.personResult.map(_.team).distinct
        classTeams.size
      }.max

      mostTeams * 2
    }

    def teams: List[String] = {
      data.classResult.flatMap { cls =>
        cls.personResult.map(_.team).distinct
      }.distinct
    }

    def classes: List[Class] = data.classResult.map(_.`class`)
  }
}
