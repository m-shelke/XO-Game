package com.example.multi_playerxogame

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaParser
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.multi_playerxogame.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {

    //    initiating MediaPlayer class
    lateinit var mediaPlayer: MediaPlayer


    //initiated view binding here
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //                           if the RECEIVE_SMS & SEND_SMS permission is not granted, then request for the permission and get in the arrayOf Manifest
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //                            if the permission is not granted, then explicitly requesting the permission from user
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                111
            )
        } else {
//            else RECEIVE_SMS, and calling receiveMsg() method
            receiveMsg()
        }

        //inflating xml.layout with .java code
        binding = ActivityMainBinding.inflate(layoutInflater)
        //binding root of the xml.layout
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //handling clicked event on playOfflineBtn
        binding.playOfflineBtn.setOnClickListener {
//            calling createOfflineGame() method
            createOfflineGame()
        }

        //handling clicked event on createOnlineBtn
        binding.createOnlineBtn.setOnClickListener {
            //calling createOnlineGame() method to creating online the game
            createOnlineGame()
        }

        //handling clicked event on joinOnlinGameeBtn
        binding.joinOnlineBtn.setOnClickListener {

            //                           if the permission is not granted, then request for the permission and the define in the arrayOf Manifest
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.RECEIVE_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //                            if the permission is not granted, then explicitly requesting the permission
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                    111
                )
            } else {
                receiveMsg()
            }

            //calling joinOnlineGames() method to join the game
            joinOnlineGames()
        }
    }

    //fun for creating online mode game
    fun createOnlineGame() {

        //my id for the creatingOnline game. this the my id for X sign
        GameData.myID = "X"

        //saving GameData inside the UI Of Application
        GameData.saveGameModel(
            GameModel(
                //In the online mode, when gameStatus is CREATED, then we gonna show the Game Id in the gameStatus
                gameStatus = GameStatus.CREATED,

                //by default game id is "-1", in online mode we gonna generated random game id between the 1000->9999 and show to GameStatus
                gameId = Random.nextInt(1000..9999).toString()
            )
        )

        //calling startGame() to start the game
        startGame()
    }


    //    if the permission is granted, then show the result via requestCode == 111
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

//    if the requestCode ==111 and grantResults is Permission Granted
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            then we calling receive() method
            receiveMsg()
    }

    //    creating receive() method to read and receive the message
    private fun receiveMsg() {

//        getting BroadcastReceiver in broadcastReceiver variable
        var boardCastReceiver = object : BroadcastReceiver() {

            //            implementing BroadcastReceiver abstract method called onReceive() method
            override fun onReceive(context: Context?, intent: Intent?) {

//                if the Android SKD Build Version is Equal and Greater than KITKAT, then run the application, otherwise Application is not for this version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

//                    for loop for reading every sms. Getting message from default Message manager app
                    for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

//                        displaying message body of receiver message
//                        Toast.makeText(applicationContext,sms.displayMessageBody,Toast.LENGTH_LONG).show()

//                        setting originationAddress edNum means Phone Number
                        // binding.edNum.setText(sms.originatingAddress)
//                        setting displayMessageBody to edMessage means Phone Number
                        binding.gameIdEd.setText(sms.displayMessageBody)

//                        calling audio() function here
                        audio()

                    }
                }
            }
        }

        //    Registering BroadcastReceiver for SMS_RECEIVED
        registerReceiver(boardCastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }


    //fun for join the online mode game, which already created by somebody
    fun joinOnlineGames() {

        //getting input of EditText from user in the gameId variable
        var gameId = binding.gameIdEd.text.toString()

        //if the game id is empty, that means user didn't enter any game id, then show this Toast message
        if (gameId.isEmpty()) {

            //Toast message for empty gameId entered  by user
            binding.gameIdEd.setError("Please Enter Game ID First..")
            return
        }

        //my id for the joinOnline game. this is my id for "O" sign
        GameData.myID = "O"

        //getting stored model data from the Firebase
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            //if it has some value, then adding addOnSuccessListener
            .addOnSuccessListener {
                //if we get the value, first we convert it to GameModel::class.java inside the model variable
                val model = it.toObject(GameModel::class.java)

                if (model == null) {
                    binding.gameIdEd.setError("Please Enter VALID Game Id..")
                } else {
                    //if model is there means game id is there. Then joined the game
                    model.gameStatus = GameStatus.JOINED

                    //save the game model
                    GameData.saveGameModel(model)

                    //startGame() to start game now
                    startGame()
                }
            }
    }


    //function for creating offline game
    fun createOfflineGame() {

        //saving GameData inside the UI Of Application
        GameData.saveGameModel(
            GameModel(
                //In the Offline mode, User will directly join the game and start game by clicked on Start Game Button
                gameStatus = GameStatus.JOINED
            )
        )

        //calling startGame() to start the game
        startGame()
    }

    //function for start GameActivity
    fun startGame() {

        //calling GameActivity by intent
        startActivity(Intent(this, GameActivity::class.java))
    }

    //    creating function for incoming message audio
    fun audio() {
        //        calling media file
        mediaPlayer = MediaPlayer.create(this, R.raw.message_audio)
//        starting media that is audio
        mediaPlayer.start()
    }

//    override fun onPause() {
//        super.onPause()
//        mediaPlayer.pause()
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release()
//    }
}
