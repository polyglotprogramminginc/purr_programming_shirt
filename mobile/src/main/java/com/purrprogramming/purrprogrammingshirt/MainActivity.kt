package com.purrprogramming.purrprogrammingshirt

import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    val SERVICE_NOTIFICATION = "com.polyglotprogramminginc.purrprogramming.SERVICE_NOTIFICATION"
    lateinit private var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        when (item.itemId) {
            R.id.action_initialize -> {
                val i: Intent = Intent(SERVICE_NOTIFICATION)
                i.putExtra("command", "initialize")
                sendBroadcast(i)
            }
            R.id.action_eyes -> {
                val i: Intent = Intent(SERVICE_NOTIFICATION)
                i.putExtra("command", "eyes")
                sendBroadcast(i)
            }
            R.id.action_swirls -> {
                val i: Intent = Intent(SERVICE_NOTIFICATION)
                i.putExtra("command", "flashSwirls")
                sendBroadcast(i)
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
    }

}
