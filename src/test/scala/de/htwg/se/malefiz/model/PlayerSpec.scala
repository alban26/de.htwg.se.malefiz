package de.htwg.se.malefiz.model

import org.scalatest._
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends WordSpec with Matchers {
  "A Player" when {
    "new" should {
    val player = Player(1, "Robert", "red")
      "have a name"  in {
        player.name should be("Robert")
      }
      "have a color respresentation" in {
      player.colour should be("red")
      }
      "have a Player number" in {
        player.playerNumber should be (1)
      }
    }
  }
}
