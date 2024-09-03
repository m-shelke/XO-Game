package com.example.multi_playerxogame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//making GameData class as obj
object GameData {

    //we get MutableLiveData in gameModel variable to show the opponent user
     var gameModel : MutableLiveData<GameModel> = MutableLiveData()

    //getting LiveData form the gameModel variable
    var liveGameModel : LiveData<GameModel> = gameModel

    //this fun will store and save the actual game data
    fun saveGameModel(model: GameModel){
        //this stmt gonna post value to ->> private var gameModel : MutableLiveData<GameModel> = MutableLiveData() for immediate reflate to UI
        gameModel.postValue(model)
    }
}