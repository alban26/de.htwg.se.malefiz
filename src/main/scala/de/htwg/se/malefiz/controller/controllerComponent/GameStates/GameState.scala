package de.htwg.se.malefiz.controller.controllerComponent.GameStates

import de.htwg.se.malefiz.controller.controllerComponent.{ControllerInterface, State}
import de.htwg.se.malefiz.controller.controllerComponent.controllerBaseImpl.Controller

case class GameState(controller: ControllerInterface) {
  var state: State[GameState] = Setup(controller)

  def run(string: String): Unit = {
    state.handle(string, this)
  }

  def nextState(state: State[GameState]): Unit = {
    this.state = state
  }

}
