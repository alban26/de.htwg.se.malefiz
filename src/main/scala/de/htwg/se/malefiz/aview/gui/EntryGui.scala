package de.htwg.se.malefiz.aview.gui

import java.awt.{Color, Font}

import de.htwg.se.malefiz.Malefiz.{entryGui, entryPlayerGui}
import de.htwg.se.malefiz.controller.controllerComponent.ControllerInterface

import scala.swing.event.ButtonClicked
import scala.swing.{Button, Dimension, Frame, GridBagPanel, Label, _}

class EntryGui(controller: ControllerInterface) extends Frame {

  visible = true
  title = "Malefiz"
  maximize()

  val welcomeLabel = new Label("Welcome to malefiz")
  welcomeLabel.foreground = Color.WHITE
  welcomeLabel.font = new Font("Sans Serif", Font.BOLD, 22)

  val newGameButton = new Button("New Game")
  val loadButton = new Button("Load Game")
  val loadButtonDB = new Button("Load Game from DB")
  val quitButton = new Button("Quit")

  def showErrorMessage(error: String): Unit = {
    Dialog.showConfirmation(contents.head, error, optionType = Dialog.Options.Default, title = "ERROR")
    this.visible = true
  }

  contents = new GridBagPanel {

    background = Color.DARK_GRAY

    def constraints(x: Int,
                    y: Int,
                    gridWidth: Int = 1,
                    gridHeight: Int = 1,
                    weightX: Double = 0.0,
                    weightY: Double = 0.0,
                    fill: GridBagPanel.Fill.Value = GridBagPanel.Fill.None,
                    ipadX: Int = 0,
                    ipadY: Int = 0,
                    anchor: GridBagPanel.Anchor.Value = GridBagPanel.Anchor.Center)
    : Constraints = {
      val contraint = new Constraints
      contraint.gridx = x
      contraint.gridy = y
      contraint.gridwidth = gridWidth
      contraint.gridheight = gridHeight
      contraint.weightx = weightX
      contraint.weighty = weightY
      contraint.fill = fill
      contraint.ipadx = ipadX
      contraint.ipady = ipadY
      contraint.anchor = anchor
      contraint
    }

    add(welcomeLabel,
      constraints(0, 0, gridHeight = 2, ipadX = 50, ipadY = 50, anchor = GridBagPanel.Anchor.North, fill = GridBagPanel.Fill.Vertical))
    add(newGameButton,
      constraints(0, 2, gridHeight = 2, ipadX = 20, ipadY = 20))
    add(loadButton,
      constraints(0, 4, gridHeight = 2, ipadX = 20, ipadY = 20))
    add(loadButtonDB,
      constraints(0, 6, gridHeight = 2, ipadX = 20, ipadY = 20))
    add(quitButton,
      constraints(0, 8, gridHeight = 2, ipadX = 20, ipadY = 20))
  }

  listenTo(newGameButton, quitButton, loadButton, loadButtonDB)

  reactions += {
    case ButtonClicked(`quitButton`) => System.exit(0)
    case ButtonClicked(`loadButton`) =>
      entryGui.visible = false
      controller.load()
    case ButtonClicked(`loadButtonDB`) =>
      entryGui.visible = false
      controller.loadFromDB()
    case ButtonClicked(`newGameButton`) =>
      visible = false
      entryPlayerGui.visible = true
  }

  size = new Dimension(500, 500)
}
