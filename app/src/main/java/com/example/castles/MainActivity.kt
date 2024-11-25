package com.example.castles

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.castles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var joined_room : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //online fragment
    fun createRoomClick(view: View){
        ButtonClicks.OnCreateRoomClick(view as Button);
        joined_room = false
    }

    //online fragment
    fun joinRoomClick(view: View){
        ButtonClicks.OnJoinRoomClick(view as Button);
        joined_room = true
    }


    //fragment 1
    fun nextPageClick(view: View){
        ButtonClicks.OnNextPageClick(view as Button);
    }

    //fragment 1
    fun colorButtonClick(view: View){
        ButtonClicks.OnColorClick(view as Button);
    }

    //fragment 2
    fun onClickStart(view : View) {
        view.setVisibility(View.GONE);
        ButtonClicks.StartGame(view);

        ButtonClicks.UpdateIcons(
            view.getContext() as Activity, intArrayOf(
                R.id.castle_button1, R.id.castle_button2, R.id.castle_button3,
                R.id.castle_button4, R.id.castle_button5, R.id.castle_button6, R.id.castle_button7
            )
        )

        ButtonClicks.DisplayPassword((view.context as Activity).findViewById(R.id.roomPasswordText), !joined_room);
        ((view.context as Activity).findViewById(R.id.roomPasswordLayout) as View).visibility = View.VISIBLE;
    }

    //fragment 2
    fun onCastleClick(view : View) {
        ButtonClicks.OnCastleImageClick(view as ImageButton);
    }

    //fragment 2
    fun onPlusClick(view: View){
        ButtonClicks.OnPlusClick(view as Button);
    }

    //fragment 2
    fun onNextRoomClick(view: View){
        ButtonClicks.OnNextRoomClick(view as Button);
    }

    //fragment 2
    fun onDoneCountClick(view: View){
        ButtonClicks.OnDoneClick(view as Button);
    }
}