package de.htwg.se.malefiz.gameBoardModule.aview
import de.htwg.se.malefiz.gameBoardModule.controller.controllerComponent.Statements
import de.htwg.se.malefiz.gameBoardModule.controller.controllerComponent.Statements.addPlayer
import de.htwg.se.malefiz.gameBoardModule.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.malefiz.gameBoardModule.model.gameBoardComponent.gameBoardBaseImpl.{Cell, Creator, GameBoard, Player}
import org.scalatest._
import org.scalatest.matchers.should.Matchers
import scala.collection.mutable.Map

class TuiSpec extends WordSpec with Matchers {

  "A Malefiz Tui" when {
    "when a new game start" should {
      val cellConfigFile = "/configuration/mainCellConfiguration"
      val cellLinksFile = "/configuration/mainCellLinks"

      val players: List[Option[Player]] = List().empty
      val cellList: List[Cell] = Creator().getCellList(cellConfigFile)
      val cellGraph: Map[Int, Set[Int]] = Creator().getCellGraph(cellLinksFile)
      val possibleCells: Set[Int] = Set().empty

      val gameBoard = GameBoard(
        cellList,
        players,
        cellGraph,
        possibleCells,
        Option(1),
        None,
        None,
        None,
        Option(addPlayer)
      )

      val controller = new Controller(gameBoard)
      val tui = new Tui(controller)

      "At the beginning you will entry to the Setup-State" +
        "In our case its defined as Number -> 4" +
        "Also the Statement is set which is shown in the GUI and in the TUI" in {
        controller.state.currentState.toString should be("4")
        controller.gameBoard.statementStatus.get should be(Statements.addPlayer)
      }
      "read names from the console" in {
        tui.processInput("n Robert")
        tui.processInput("n Alban")

        val playerList = controller.gameBoard.players

        playerList.head.get.playerNumber should be(1)
        playerList.head.get.name should be("Robert")
        playerList(1).get.playerNumber should be(2)
        playerList(1).get.name should be("Alban")
      }
      "When the game gets started by a full Players List or if typed in start, " +
        "in this case it's Roberts turn. The state's gonna be 'Roll' in our case it's -> 1 " in {
        tui.processInput("n start")
        controller.gameBoard.statementStatus.get should be(Statements.roll)
        controller.gameBoard.playersTurn.get.playerNumber should be(1)
        controller.gameBoard.playersTurn.get.name should be("Robert")
        controller.state.currentState.toString should be("1")
      }
      "So when Robert is pressing any key now, he's going to roll the dice" in {
        tui.processInput("k")
        controller.gameBoard.statementStatus.get should be(
          Statements.selectFigure
        )
        controller.gameBoard.dicedNumber.get should ((be >= 1).and(be < 7))
        controller.setDicedNumber(Some(1))
      }
      "Now Robert needs to select his Figure. In this case he gets to the next State " +
        "The 'Select Figure State' is the Number 2" in {
        controller.state.currentState.toString should be("2")
      }
      "Well so let's say he chooses his second Figure. In this case " +
        "he types in '1 2', because he is the first player (1) and wants his second figure(2)" +
        "So when he choose, the variable selectedFigures got filled with these numbers" +
        "Also he gets to the next state SetFigure!" in {
        tui.processInput("1 2")
        controller.gameBoard.selectedFigure.get should be(1, 2)
        controller.gameBoard.statementStatus.get should be(
          Statements.selectField
        )
      }
      "Now he can move his figur - for the test case we have set " +
        "the dicednumber manually to 1 so we can predict that he only can move to field 22" in {
        controller.gameBoard.possibleCells.contains(22) should be(true)
      }
      "What if Robert wants to take another Figure - He has to klick again on his figure " in {
        tui.processInput("1 2")
        tui.processInput("1 4")
        controller.state.currentState.toString should be("3")
      }
      "Robert sets his Figure on 22" in {
        controller.state.currentState.toString should be("3")
        tui.processInput("22")
        controller.state.currentState.toString should be("1")
      }
      "After he has set his Figure, it is Albans turn now." in {
        controller.gameBoard.playersTurn.get.name should be("Alban")
      }
      "Like Robert Alban will press any key to dice. For this test case we set his diced number to" +
        "5. So he can reach a wall! 1. Diced Number -> 5 " +
        "                           2. Selected figure -> 2 1 : Now he is in state: SetFigure" +
        "                           3. Set figure -> 46 : 46 is a cell which contains a wall" +
        "                           4. Now he is in state 'SetWall' in our case its '5'" in {
        tui.processInput("r")
        controller.setDicedNumber(Some(5))
        tui.processInput("2 1")
        controller.state.currentState.toString should be("3")
        tui.processInput("46")
        controller.gameBoard.statementStatus.get should be(Statements.wall)
        controller.state.currentState.toString should be("5")

      }

      "Alban decides to set his wall on 24 - what he may forgot - this is a forbidden area for walls" +
        "he gets the message that he should put his wall on another field" in {
        tui.processInput("23")
        controller.gameBoard.statementStatus.get should be(Statements.wrongWall)
      }
      "Now Alban decides to put his wall on Roberts figure ... he should get the last statement again!" in {
        tui.processInput("22")
        controller.gameBoard.statementStatus.get should be(Statements.wrongWall)
      }
      "It seems to be not Albans day today - now he sets his wall on another wall! he will read the statement" +
        "again - 'Alban du darfst die Mauer dort nicht setzen. Bitte wähle ein anderes Feld aus.'" in {
        tui.processInput("54")
        controller.gameBoard.statementStatus.get should be(Statements.wrongWall)
      }
      "Now he will put his wall on a valid field " in {
        tui.processInput("48")
        controller.gameBoard.statementStatus.get should be(
          Statements.nextPlayer
        )
      }
      "Now he will undo his step an then redo his last step" in {
        print(controller.gameBoard.statementStatus.get)
        tui.processInput("undo")
        print(controller.gameBoard.statementStatus.get)
        tui.processInput("redo")
        print(controller.gameBoard.statementStatus.get)
        controller.gameBoard.cellList(48).hasWall should be(true)
        controller.gameBoard.statementStatus.get should be(Statements.wrongWall)
      }
      "Now it's Roberts turn again. But what happens when he chooses Albans figure after throwing the Cube" in {
        tui.processInput("r")
        controller.setDicedNumber(Some(1))
        tui.processInput("2 1")
        controller.gameBoard.statementStatus.get should be(
          Statements.selectField
        )
      }
      "Now he chooses his right figure but he decides to kick his own other figure." in {
        tui.processInput("1 1")
        tui.processInput("22")
        controller.gameBoard.statementStatus.get should be(
          Statements.wrongField
        )
      }
      "We can do this action with the redo again" in {
        tui.processInput("redo")
        controller.gameBoard.statementStatus.get should be(
          Statements.wrongField
        )
      }
      "We can undo this action " in {
        tui.processInput("undo")
        controller.state.currentState.toString should be("3")
        controller.gameBoard.playersTurn.get.name should be("Robert")
      }
      "If we want to Test if a player can win the game, we set the possible Cell of this turn to the Cell 131 - The Winner Cell" +
        "Then we set the the figure 1 of player 1 to the winner Cell" in {
        controller.setPossibleCells(Set(131))
        tui.processInput("131")
        controller.gameBoard.statementStatus.get should be(Statements.won)
      }

    }
  }
}
