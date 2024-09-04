package com.example.multi_playerxogame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

//making GameData class as obj
object GameData {

    //we get MutableLiveData in gameModel variable to show the opponent user
     var gameModel : MutableLiveData<GameModel> = MutableLiveData()

    //getting LiveData form the gameModel variable
    var liveGameModel : LiveData<GameModel> = gameModel

    //this id for myXId for creating the online game
    var myID = ""

    //this fun will store and save the actual game data
    fun saveGameModel(model: GameModel){
        //this stmt gonna post value to ->> private var gameModel : MutableLiveData<GameModel> = MutableLiveData() for immediate reflate to UI
        gameModel.postValue(model)

        //if game id is not "-1" then store to the firebase otherwise it was offline game
        if (model.gameId != "-1"){

            //here, we set the entire GameData model on firebase Database
            Firebase.firestore.collection("games")
                .document(model.gameId)
                .set(model)
        }


    }

    //fetching game model from the firebase database
    fun fetchGameModel(){
        gameModel.value?.apply {

            //checking is the user online or not by if gameId is not "-1" then user is online but if it's "-1" he is in offline mode
            if (gameId != "-1"){

                Firebase.firestore.collection("games")
                    .document(gameId)
                    //when ever change occurred it gonna add listener
                    .addSnapshotListener { value, error ->

                        //getting gamedata value to the model variable and reflate it to UI
                       val model = value?.toObject(GameModel::class.java)

                        //this stmt gonna post value to ->> private var gameModel : MutableLiveData<GameModel> = MutableLiveData() for immediate reflate to UI. Same as above
                        gameModel.postValue(model)
                    }
            }
        }
    }
}