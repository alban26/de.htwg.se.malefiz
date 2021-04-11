package de.htwg.se.malefiz.model.playerComponent

import de.htwg.se.malefiz.playerModule.model.playerServiceComponent.Player
import org.scalatest._
import org.scalatest.matchers.should.Matchers

class PlayerSpec extends WordSpec with Matchers {
  "A Player" when {
    "new" should {
    val player = Player(1, "Robert")
      "have a name"  in {
        player.toString should be("Robert")
      }
      "have a Player number" in {
        player.playerNumber should be (1)
      }
    }
  }
}
