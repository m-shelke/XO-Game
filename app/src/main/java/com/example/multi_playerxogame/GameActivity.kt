package com.example.multi_playerxogame

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.multi_playerxogame.databinding.ActivityGameBinding

//implementing onClickListener to handle button click events
class GameActivity : AppCompatActivity(),View.OnClickListener {

    //gameModel is instance of the GameModel class and assigned it as a null for to get the that class gameModel mutableLiveData
    private var gameModel: GameModel? = null

    //initiate binding here
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //binding xml.layout to java code
        binding = ActivityGameBinding.inflate(layoutInflater)
        //binding root of xml.layout
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //finding view that is button and setting onClickListener to it by passing this as reference
//        binding.btn0.setOnClickListener { this }
//        binding.btn1.setOnClickListener { this }
//        binding.btn2.setOnClickListener { this }
//        binding.btn3.setOnClickListener { this }
//        binding.btn4.setOnClickListener { this }
//        binding.btn5.setOnClickListener { this }
//        binding.btn6.setOnClickListener { this }
//        binding.btn7.setOnClickListener { this }
//        binding.btn8.setOnClickListener { this }

        //calling fetchGameModel() form the GameData and what ever happen in data it gonna reflate to the UI
        GameData.fetchGameModel()

        //finding view that is button and setting onClickListener to it by passing this as reference above line of code is not fired setOnClickListener
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)


        //clicked event on startGameBtn
        binding.startGameBtn.setOnClickListener {
            //called startGame() fun to start the game
            startGame()
        }

        //observing live game data
        GameData.gameModel.observe(this) {

            //Store and save GameModel to gameModel variable
            gameModel = it

            //calling SetItUI for immediate update bwt entered Data and UI
            setItUI()
        }

    }

    //fun for setting UI and Data
    fun setItUI() {

        gameModel?.apply {

            //Assigning arraylist position to UI
            binding.btn0.text = filledPos[0]
            binding.btn1.text = filledPos[1]
            binding.btn2.text = filledPos[2]
            binding.btn3.text = filledPos[3]
            binding.btn4.text = filledPos[4]
            binding.btn5.text = filledPos[5]
            binding.btn6.text = filledPos[6]
            binding.btn7.text = filledPos[7]
            binding.btn8.text = filledPos[8]

            //When GameActivity start, then startGameBtn is VISIBLE
            binding.startGameBtn.visibility = View.VISIBLE

            //for updating the game status like who's turn, draw and who is win the game
            binding.gameStatusTV.text =

                    //when is like a switch case
                when (gameStatus) {

                    //when gameStatus is CREATED, we return
                    GameStatus.CREATED -> {

                        //When GameStatus is CREATED then Visibility of startGameBtn is INVISIBLE
                        binding.startGameBtn.visibility = View.INVISIBLE

                        //if game is CREATED then show Id of the game
                        "Game Id :"+ gameId
                    }

                    //when gameStatus is JOINED
                    GameStatus.JOINED -> {
                        //if game is Joined then show this Stritng in the gameStatusTv
                        "Clicked On 'Start Game Button'"
                    }
                    //if gameStatus is INPROGESS
                    GameStatus.INPROGESS -> {

                        //When GameStatus is INPROGESS then setting Visibility of startGameBtn is INVISIBLE
                        binding.startGameBtn.visibility = View.INVISIBLE

                        when(GameData.myID){
                            //it's myID then display it's "Your Turn"
                            currentPlayer -> "Your Turn"

                            //Then return currentPlayer that "X" or may be "O" and return String as well
                            else -> currentPlayer +" Turn"
                        }

                    }
                    //if GameStatus is Finished then, we gonna check the Winner of the game
                    GameStatus.FINISHED -> {

                        //if winner is not empty then show the winner won the match otherwise match get tie
                        if (winner.isNotEmpty()){
                            //GameData and its my id then i won the match
                            when(GameData.myID){
                                winner -> "You Won"
                                //else 8opponent won the match
                                else -> winner + " Won"
                            }
                        }
                        //else Match tie
                        else " Match Tie..."
                    }
                }

        }
    }

    //creating fun for to start offline game
    fun startGame() {
        gameModel?.apply {

            //calling updateGameData with required parameter
            updateGameData(
                GameModel(
                    //gameId is same as generated
                    gameId = gameId,
                    //GameStatus change into-> "O" "X" turn because now gameStatus is INPROGESS
                    gameStatus = GameStatus.INPROGESS
                )
            )
        }
    }

    //implementing and override onClick method to handle button clicked that's 9 over their
    override fun onClick(v: View?) {

        //Adding data to Button, if the button get clicked by the User
        gameModel?.apply {

            //if the gameStatus is not in GameStatus.INPROGESS means either its of turn of "X" "O" then toast this message
            if (gameStatus != GameStatus.INPROGESS) {
                Toast.makeText(applicationContext, "Game Not Started Yet..", Toast.LENGTH_SHORT).show()
                return
            }
            //else if gameStatus is INPROGESS then..
            if (gameId != "-1" && currentPlayer != GameData.myID){
                Toast.makeText(applicationContext, "Not Your Turn..", Toast.LENGTH_SHORT).show()
                return
            }

            //we have the View in the clicked method and we already defines the tags in the xml.layout, we get it as String and store to the variable
            var clicekdPos = (v?.tag as String).toInt()

            //if the var filledPos : MutableList<String> = mutableListOf("","","","","","","","","") [clickedPos] is empty
            if (filledPos[clicekdPos].isEmpty()) {

                //filledPos is empty then, we assigned it to the currentPlayer
                filledPos[clicekdPos] = currentPlayer

                //if playerA clicked and it's "X" then switch the PlayerB to "O"
                currentPlayer = if (currentPlayer == "X") "O" else "X"

                //calling checkWinner(), everytime to check for the winner
                checkForWinner()

                //every time button clicked, let's update the GameData for that calling updateGameData
                updateGameData(this)
            }

        }

    }

    //fun for updating the GameData
    fun updateGameData(model: GameModel) {
        //form here, getting the updateGameData from saveGameModel method of GameData class
        GameData.saveGameModel(model)
    }

    //fun for checking who's is winner of the match
    fun checkForWinner(){

        //getting all winning position in winningPos variable in the format of arraylist
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6)
        )

        gameModel?.apply {

            for (i in winningPos){

                //for the position 0,1,2 suppose
                if (
                    //if filledPos is 0,1,2 and its not empty
                    filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty())
                    {
                        //gameStatus is finished and declaring the winner inside winner variable
                        gameStatus = GameStatus.FINISHED
                        //for the 0,1,2 position
                        winner = filledPos[i[0]]
                    }
            }

           //if none of the filledPos is empty
          if (filledPos.none(){it.isEmpty()}){
              //gameStatus will be FINISHED and winner will not be decide because there is no any winner matching with position
              gameStatus = GameStatus.FINISHED
          }

            //and here we update the winner of the match in the GameStatus view
            updateGameData(this)
        }
    }
}