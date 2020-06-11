package de.htwg.se.malefiz
import scala.collection.mutable.Map
import de.htwg.se.malefiz.aview.Tui
import de.htwg.se.malefiz.controller.Controller
import de.htwg.se.malefiz.model.{Cell, Creator, Cube, GameBoard, Player}

import scala.io.StdIn.readLine

object Malefiz {

  val cellConfigFile = "C:\\Users\\ALBAN\\Desktop\\AIN\\STUDIUM\\3.Semester\\Software Engineering\\de.htwg.se.malefiz\\src\\main\\scala\\de\\htwg\\se\\malefiz\\model\\mainCellConfiguration"
  val cellLinksFile = "C:\\Users\\ALBAN\\Desktop\\AIN\\STUDIUM\\3.Semester\\Software Engineering\\de.htwg.se.malefiz\\src\\main\\scala\\de\\htwg\\se\\malefiz\\model\\mainCellLinks"

  val players : List[Player] = List().empty
  val cellList : List[Cell] = Creator().getCellList(cellConfigFile)
  val cellGraph : Map[Int, Set[Int]] = Creator().getCellGraph(cellLinksFile)


  val controller = new Controller(GameBoard(cellList, players, cellGraph))
  val tui = new Tui(controller)

  def main(args: Array[String]): Unit = {

    println("Willkommen bei Malefiz!\nUm eine neues")

    var input: String = ""

    do {
      input = readLine()
      tui.processInput(input)

    } while(input != "end")
  }
}
