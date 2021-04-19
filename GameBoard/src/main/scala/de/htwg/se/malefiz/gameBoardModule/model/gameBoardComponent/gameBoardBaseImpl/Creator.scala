package de.htwg.se.malefiz.gameBoardModule.model.gameBoardComponent.gameBoardBaseImpl

import de.htwg.se.malefiz.gameBoardModule.model.gameBoardComponent.CreatorInterface
import de.htwg.se.malefiz.gameBoardModule.model.gameBoardComponent.gameBoardBaseImpl

import scala.collection.mutable.Map
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

case class Creator() extends CreatorInterface {

  def readTextFile(filename: String): Try[Option[BufferedSource]] =
    Try(Option(Source.fromFile(filename)))

  def getCellList(inputFile: String): List[Cell] =
    readTextFile(inputFile) match {
      case Success(lines) =>
        val listOfCells = lines
          .get
          .getLines()
          .map(line => line.split(" "))
          .map {
                case Array(
                cellNumber,
                playerNumber,
                figureNumber,
                wallPermission,
                hasWall,
                x,
                y,
                possibleFigures,
                possibleCells
                ) =>
                  gameBoardBaseImpl.Cell(
                    cellNumber.toInt,
                    playerNumber.toInt,
                    figureNumber.toInt,
                    wallPermission.toBoolean,
                    hasWall.toBoolean,
                    Point(x.toInt, y.toInt),
                    possibleFigures.toBoolean,
                    possibleCells.toBoolean
                  )
              }
          .toList
        lines.get.close()
        listOfCells

      case Failure(f) =>
        println(f)
        List.empty
    }

  def getCellGraph(fileInput: String): Map[Int, Set[Int]] =
    readTextFile(fileInput) match {
      case Success(line) =>
        println("to Malefiz!")

        val source = Source.fromFile(fileInput)
        val lines = source.getLines()
        val graph: Map[Int, Set[Int]] = Map.empty
        while (lines.hasNext) {
          val input = lines.next()
          val inputArray: Array[String] = input.split(" ")
          val keyValue = inputArray(0)
          inputArray.update(0, "")
          inputArray.map(input =>
            if (input != "") {
              updateCellGraph(keyValue.toInt, input.toInt, graph)
            })
        }
        graph
      case Failure(f) =>
        println(f)
        Map.empty
    }

  def updateCellGraph(key: Int, value: Int, map: Map[Int, Set[Int]]): Map[Int, Set[Int]] = {
    map
      .get(key)
      .map(_ => map(key) += value)
      .getOrElse(map(key) = Set[Int](value))
    map
  }

}