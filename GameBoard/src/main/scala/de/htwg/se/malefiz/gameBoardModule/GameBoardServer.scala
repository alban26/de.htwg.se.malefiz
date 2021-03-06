package de.htwg.se.malefiz.gameBoardModule

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.google.inject.{Guice, Injector}
import de.htwg.se.malefiz.gameBoardModule.aview.Tui
import de.htwg.se.malefiz.gameBoardModule.aview.gui.SwingGui
import de.htwg.se.malefiz.gameBoardModule.controller.controllerComponent.ControllerInterface
import spray.json.DefaultJsonProtocol

import scala.io.StdIn

object GameBoardServer extends SprayJsonSupport with DefaultJsonProtocol {

  val cellConfigFile = "/configuration/mainCellConfiguration"
  val cellLinksFile = "/configuration/mainCellLinks"

  val injector: Injector = Guice.createInjector(new GameBoardServerModule)
  val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  val tui = new Tui(controller)
  val swingGui = new SwingGui(controller)

  def openGameBoardGui(): Unit = {
    swingGui.visible = true
    swingGui.drawGameBoard()
    swingGui.updatePlayerArea()
    swingGui.updatePlayerTurn()
    swingGui.updateInformationArea()
  }

  val route = concat(
    (path("players") & post) {
      entity(as[List[String]]) { playerList => //
        playerList.foreach(player => tui.processInput("n " + player))
        complete(StatusCodes.Created, controller.gameBoard.players.toString())
      }
    },
    path("newGame") {
      tui.processInput("n start")
      openGameBoardGui()
      complete(StatusCodes.Created, "Spiel wurde erfolgreich gestartet")
    },
    path("loadGame") {
      tui.processInput("load")
      complete(StatusCodes.Created, "Spielbrett wurde erfolgreich geoeffnet")
    },
    path("loadGameFromDB") {
      tui.processInput("loadFromDB")
      complete(StatusCodes.Created, "Spiel wurde erfolgreich geladen")

    },
    path("gameBoard") {
      get {
        openGameBoardGui()
        complete(StatusCodes.Accepted, HttpEntity("Spiel wurde geöffnet"))
      }
    },
    (path("gameBoardJson") & post) {
      entity(as[String]) { gameJsonString =>
        controller.evalJson(gameJsonString)
        complete(StatusCodes.Created, HttpEntity("Laden von Json war erfolgreich!"))
      }
    },
    (path("process") & post) {
      entity(as[String]) { input =>
        tui.processInput(input)
        complete(StatusCodes.Accepted, HttpEntity(controller.gameBoard.toString))
      }
    },
    (path("save") & post) {
      entity(as[String]) { input =>
        tui.processInput(input)
        complete(StatusCodes.Accepted, HttpEntity("Speichere in Datenbank"))
      }
    },
    (path("roll") & post) {
      entity(as[String]) { input =>
        tui.processInput(input)
        complete(HttpEntity(controller.gameBoard.dicedNumber.getOrElse("0").toString))
      }
    },
    (path("gameBoardXml") & post) {
      entity(as[String]) { gameXmlString => //
        controller.evalXml(gameXmlString)
        complete(HttpEntity("Laden von Xml war erfolgreich!"))
      }
    }
  )

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "GameBoard")
    implicit val executionContext = system.executionContext

    route

    val bindingFuture = Http().newServerAt("localhost", 8083).bind(route)

    println(s" GameBoard Server online at http://0.0.0.0:8081/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}