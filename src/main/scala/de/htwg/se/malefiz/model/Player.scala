package de.htwg.se.malefiz.model

import java.awt.Color

case class Player(playerNumber: Int) {
   val numberOfPlayer: Int = playerNumber
   var playerColor: Color = Color.black





  /* color match {
      case "Red" =>  playerColor = Color.red
      case "Green" => playerColor = Color.green
      case "Yellow" => playerColor = Color.yellow
      case "Blue" => playerColor = Color.blue
   }
*/

   override def toString:String = "Playernumber: " + playerNumber// + " Player: " + name + " Color: " + playerColor + figuresOfPlayer
}

