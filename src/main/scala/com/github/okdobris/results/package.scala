package com.github.okdobris

package object results {
  import model._
  // https://obrozvoj.cz/Pages/PreborSkol/Pravidla.aspx

  implicit class ResultOps(private val data: ResultList) {

    def winnerPoints: Int = {
      // Již od okresních kol se body přidělují podle počtu zúčastněných družstev v nejvíce obsazené kategorii x 2.
      val mostTeams = data.classResult.map { cls =>
        // first three characters of person Id are team code
        val classTeams = cls.personResult.map(_.person.id.take(3)).distinct
        classTeams.distinct.size
      }.max

      mostTeams * 2
    }
  }
}
