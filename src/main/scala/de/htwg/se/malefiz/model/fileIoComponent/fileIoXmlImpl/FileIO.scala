package de.htwg.se.malefiz.model.fileIoComponent.fileIoXmlImpl

import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.malefiz.MalefizModule
import de.htwg.se.malefiz.controller.controllerComponent.{ControllerInterface, GameBoardChanged, State}
import de.htwg.se.malefiz.controller.controllerComponent.GameStates.{GameState, Roll, SelectFigure, SetFigure, SetWall}
import de.htwg.se.malefiz.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.malefiz.model.fileIoComponent.FileIOInterface
import de.htwg.se.malefiz.model.gameBoardComponent.GameboardInterface
import de.htwg.se.malefiz.model.gameBoardComponent.gameBoardBaseImpl.Cell
import de.htwg.se.malefiz.model.playerComponent.Player

import scala.xml.{Elem, NodeBuffer, PrettyPrinter}
class FileIO extends FileIOInterface{

  override def loadController: ControllerInterface = {
    val file = scala.xml.XML.loadFile("gameboardList.xml")
    val playersTurnNodes = file \\ "playersTurn"
    val gameStateNodes = file \\ "gameState"
    val contollerNeu = new Controller(load)

    println(load.getPlayer.mkString(" "))

    contollerNeu.s.nextState(SelectFigure(contollerNeu))
    println(contollerNeu.s.state)
    contollerNeu
  }


  override def load: GameboardInterface = {
    val injector = Guice.createInjector(new MalefizModule)
    var gameboard: GameboardInterface = injector.instance[GameboardInterface]
    var gameStateT: GameState = injector.instance[GameState]
    var controller: ControllerInterface = injector.instance[ControllerInterface]



    val file = scala.xml.XML.loadFile("gameboardList.xml")
    val cellNodes = file \\ "cell"
    val playerNodes = file \\"player"




    for (player <- playerNodes){
      val playerName: String = (player \ "@playername").text
      gameboard = gameboard.createPlayer(playerName)
    }


    for(cell <- cellNodes) {

      val cellNumber: Int = (cell \ "@cellnumber").text.toInt
      val playerNumber: Int = (cell \ "@playernumber").text.toInt
      val figureNumber: Int = (cell \ "@figurenumber").text.toInt
      val hasWall: Boolean = (cell \ "@haswall").text.toBoolean

      gameboard = gameboard.setPlayer(playerNumber,cellNumber)
      gameboard = gameboard.setFigure(figureNumber,cellNumber)

      if(hasWall){
        gameboard = gameboard.setWall(cellNumber)
      }
      if(!hasWall){
        gameboard = gameboard.rWall(cellNumber)
      }

    }

    gameboard
  }

  override def save(gameboard: GameboardInterface, controller: ControllerInterface): Unit = {
    saveString(gameboard,controller)
  }

  def saveString(gameboard: GameboardInterface, controller: ControllerInterface): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("gameboardList.xml"))
    val prettyPrinter = new PrettyPrinter(80,2)
    val xml = prettyPrinter.format(gameboardToXml(gameboard,controller))
    pw.write(xml)
    pw.close()
  }

  def gameboardToXml(gameboard: GameboardInterface, controller: ControllerInterface) : Elem= {


    <gameboard size={gameboard.getPlayer.size.toString}>
      {

        for {
          l1 <- gameboard.getCellList.indices
        } yield cellToXml(l1,gameboard.getCellList(l1))
      }
      <player>
        {
        for{
          l1 <- gameboard.getPlayer.indices
        } yield playerToXml(l1,gameboard.getPlayer(l1))
        }
      </player>
      <playersTurn>

        {
        playersTurnToXml(controller.getPlayersTurn)
        }

      </playersTurn>


      <dicedNumber>

        {
        println(controller.getDicedNumber)
        dicedNumberToXml(controller.getDicedNumber)

        }

      </dicedNumber>

      <gameState>

        {
        gameStateToXml(controller.getGameState)
        }

      </gameState>

    </gameboard>

  }


  def gameStateToXml(state: GameState): Elem = {
    <gameState state={state.state.toString}>
      {state}
    </gameState>
  }

  def dicedNumberToXml(dicedNumber: Int): Elem = {
    <dicedNumber dicednumber={dicedNumber.toString}>
      {
      dicedNumber}
    </dicedNumber>
  }

  def playersTurnToXml(player: Player): Elem = {
    <playersTurn playersturnname ={player.name}>
      {player}
    </playersTurn>
  }

  def playerToXml(i: Int, player: Player): Elem = {
  <player playernumber={player.playerNumber.toString} playername={player.name}>
  {player}
  </player>

  }

  def cellToXml(l1: Int, cell: Cell): Elem = {
    <cell cellnumber={cell.cellNumber.toString} playernumber={cell.playerNumber.toString}
          figurenumber={cell.figureNumber.toString} haswall={cell.hasWall.toString}>
      {cell}
    </cell>
  }
}