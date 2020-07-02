package de.htwg.se.malefiz.model.gameBoardComponent.gameBoardBaseImpl

import com.google.inject.{Guice, Inject}
import de.htwg.se.malefiz.MalefizModule
import de.htwg.se.malefiz.model.gameBoardComponent.{CreatorInterface, gameBoardBaseImpl}

import scala.collection.mutable.Map
import scala.io.Source
import scala.util.{Failure, Success, Try}

case class Creator /*@Inject()*/ ()  extends CreatorInterface {

  def readTextFile(filename: String): Try[Iterator[String]] = {
    Try(Source.fromFile(filename).getLines)
  }

  //val injector = Guice.createInjector(new MalefizModule)

  def getCellList(inputFile: String): List[Cell] = {

   readTextFile(inputFile) match {
        case Success(line) => println("Welcome")

          val list = Source.fromFile(inputFile)
          val inputData = list.getLines

            .map(line => line.split(" "))
            .map { case Array(cellNumber, playerNumber, figureNumber, destination, wallPermission, hasWall, x, y, possibleFigures, possibleCells) =>
              gameBoardBaseImpl.Cell(cellNumber.toInt,
                playerNumber.toInt,
                figureNumber.toInt,
                destination.toBoolean,
                wallPermission.toBoolean,
                hasWall.toBoolean,
                Point(x.toInt, y.toInt),
                possibleFigures.toBoolean,
                possibleCells.toBoolean)
            }
            .toList
          list.close()

         inputData
        case Failure(f) => println(f)
          System.exit(0)
          Nil
      }


  }

  def getCellGraph(fileInput: String): Map[Int, Set[Int]] = {
    readTextFile(fileInput) match {
      case Success(line) => println("to Malefiz!")


    val source = Source.fromFile(fileInput)
    val lines = source.getLines()
    val graph : Map[Int, Set[Int]] = Map.empty
    while (lines.hasNext) {
      val input = lines.next()
      val inputArray: Array[String] = input.split(" ")
      for (i <- 1 until inputArray.length) {
        updateCellGraph(inputArray(0).toInt, inputArray(i).toInt, graph)
      }
    }
    graph

    case Failure(f) => println(f)
      System.exit(0)
        Map.empty

    }
  }

  def updateCellGraph(key: Int, value: Int, map: Map[Int, Set[Int]]) : Map[Int, Set[Int]] = {
    map.get(key)
      .map(_=> map(key) += value)
      .getOrElse(map(key) = Set[Int](value))
    map
  }

  def execute(callback:(String) => List[Cell], y: String) = callback(y)
  def execute1(callback:String => Map[Int, Set[Int]], y:String)= callback(y)


}