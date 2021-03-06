package com.github.okdobris.results

import java.io.Writer

import scala.io.Source

sealed trait TableWriter {
  // use HTML terminology, as CSV can be handled easily as a subset
  def hr(): TableWriter

  def page(): TableWriter
  def _page(): TableWriter


  def table(): TableWriter
  def _table(): TableWriter

  def tr(cls: String = ""): TableWriter
  def _tr(): TableWriter

  def th(s: String): TableWriter
  def td(s: String): TableWriter

  def th(x: Int): TableWriter = th(x.toString)
  def td(x: Int): TableWriter = td(x.toString)

  def label(s: String): TableWriter

  def close(): Unit
}


object TableWriter {

  class CSV(w: Writer) extends TableWriter {

    private def write(s: String): TableWriter = {
      w.write(s)
      this
    }

    private var trOpen = false

    private def cellDelim(): this.type = {
      if (trOpen) {
        write(",")
      } else {
        trOpen = true
      }
      this
    }


    def hr(): TableWriter = this

    def page(): TableWriter = this
    def _page(): TableWriter = this

    def table(): TableWriter = this
    def _table(): TableWriter = this

    def tr(cls: String = ""): TableWriter = this
    def _tr(): TableWriter = {
      trOpen = false
      write("\n")
    }

    def th(s: String): TableWriter = cellDelim().write(s"$s")

    def td(s: String): TableWriter = cellDelim().write(s"$s,")

    def label(s: String): TableWriter = write(s"\n$s\n\n")

    def close(): Unit = w.close()
  }

  class HTML(val w: Writer) extends TableWriter {
    def write(s: String): TableWriter = {
      w.write(s)
      this
    }

    val htmlTemplate = {
      val resource = Source.fromResource("template.html")
      resource.getLines.mkString("\n")
    }.split("\\$\\$\\$")
    val htmlPrefix = htmlTemplate.head
    val htmlPostfix = htmlTemplate.last

    write(htmlPrefix)

    def hr(): TableWriter = write("<hr/>\n")

    def page(): TableWriter = write("<div class='page'>\n")
    def _page(): TableWriter = write("</div>\n")

    def table(): TableWriter = write("<table>\n")
    def _table(): TableWriter = write("</table>\n")

    def tr(cls: String): TableWriter = write(if (cls.nonEmpty) s"<tr class='$cls'>" else "<tr>")
    def _tr(): TableWriter = write("</tr>\n")

    def th(s: String): TableWriter = write(s"<th>$s</th>")

    def td(s: String): TableWriter = write(s"<td>$s</td>")

    def label(s: String): TableWriter = write(s"<h2>$s</h2>\n")

    def close(): Unit = {
      write(htmlPostfix)
      w.close()
    }
  }

  class Multi(writers: TableWriter*) extends TableWriter {

    def map(f: TableWriter => TableWriter): Multi = new Multi(writers.map(f):_*)

    def close(): Unit = writers.foreach(_.close())

    def hr(): TableWriter = this.map(_.hr())

    def page(): TableWriter = this.map(_.page())
    def _page(): TableWriter = this.map(_._page())

    def table(): TableWriter = this.map(_.table())
    def _table(): TableWriter = this.map(_._table())

    def tr(cls: String = ""): TableWriter = this.map(_.tr(cls))
    def _tr(): TableWriter = this.map(_._tr())

    def th(s: String): TableWriter = this.map(_.th(s))
    def td(s: String): TableWriter = this.map(_.td(s))
    def label(s: String): TableWriter =  this.map(_.label(s))

  }
}