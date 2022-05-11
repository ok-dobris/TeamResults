package com.github.okdobris

package object results {
  import model._
  // https://obrozvoj.cz/Pages/PreborSkol/Pravidla.aspx
  implicit class ClassResultOps(private val cls: ClassResult) {
    def isOpen: Boolean = cls.`class`.name.toLowerCase == "open"
  }

  implicit class PersonResultOps(private val personResult: PersonResult) {
    // skip last three characters of person Id to get a team code
    def team: String = personResult.person.id.dropRight(3)
    def fullName: String = personResult.person.name.family + " "  + personResult.person.name.given
  }
  implicit class ResultOps(private val data: ResultList) {

    def mostTeams: (String, Int) = {
      val classTeams = data.classResult.filterNot(_.isOpen).map { cls =>
        val classTeams = cls.personResult.map(_.team).distinct
        cls.`class`.name -> classTeams.size
      }
      classTeams.maxBy(_._2)
    }

    def teams: List[String] = {
      data.classResult.flatMap { cls =>
        cls.personResult.map(_.team).distinct
      }.distinct
    }

    def classes: List[Class] = data.classResult.map(_.`class`)
  }
}
