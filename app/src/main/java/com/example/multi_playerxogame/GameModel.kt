package com.example.multi_playerxogame

import kotlin.random.Random

//Game class as data
data class GameModel (

    //Id of the game which is between 10,000 to 9,999
   var gameId : String = "-1",

    //we get filled position from this mutableList that means which button is clicked or not etc
    var filledPos : MutableList<String> = mutableListOf("","","","","","","","",""),  // like "O" or "X" for the 9th position

    //variable for checking who is win
    var winner : String ="",

    //gameStatus variable for updating the whos turn and who is win the game and for the draw of the match
    var gameStatus : GameStatus = GameStatus.CREATED,

    //variable for first move of the player and we set it random for both
    var currentPlayer : String = (arrayOf("X","O"))[Random.nextInt(2)]

)

enum class GameStatus(){
    CREATED,
    JOINED,
    INPROGESS,
    FINISHED
}
