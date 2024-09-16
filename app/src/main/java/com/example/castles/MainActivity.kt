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


    //fragment 1
    fun colorButtonClick(view: View){
        ButtonClicks.onColorClick(view as Button);
    }

    //fragment 2
    fun onClickStart(view : View) {
        ButtonClicks.updateIcons(
            view.getContext() as Activity, intArrayOf(
                R.id.castle_button1, R.id.castle_button2, R.id.castle_button3,
                R.id.castle_button4, R.id.castle_button5, R.id.castle_button6, R.id.castle_button7
            )
        )
        view.setVisibility(View.GONE);
        ButtonClicks.startGame();
    }

    //fragment 2
    fun onCastleClick(view : View) {
        ButtonClicks.onCastleImageClick(view as ImageButton);
    }

    //fragment 2
    fun onPlusClick(view: View){
        ButtonClicks.onPlusClick(view as Button);
    }

    //fragment 2
    fun onNextRoomClick(view: View){
        ButtonClicks.onNextRoomClick(view as Button);
    }

    //fragment 2
    fun onDoneCountClick(view: View){
        ButtonClicks.onDoneClick(view as Button);
    }
}