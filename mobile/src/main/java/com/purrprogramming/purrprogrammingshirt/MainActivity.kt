package com.purrprogramming.purrprogrammingshirt

import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity(){
    val SERVICE_NOTIFICATION = "com.polyglotprogramminginc.purrprogramming.SERVICE_NOTIFICATION"
    lateinit private var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val initialize = findViewById(R.id.initialize) as Button
        initialize.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "initialize")
            sendBroadcast(i)
        }

        val southAfrica = findViewById(R.id.south_africa) as Button
        southAfrica.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "south_africa")
            sendBroadcast(i)
        }

        val ruby = findViewById(R.id.ruby) as Button
        ruby.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "ruby")
            sendBroadcast(i)
        }

        val eyes = findViewById(R.id.eyes) as Button
        eyes.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "eyes")
            sendBroadcast(i)
        }

        val purr = findViewById(R.id.purrProgramming) as Button
        purr.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "purr")
            sendBroadcast(i)
        }

        val swirls = findViewById(R.id.flashSwirls) as Button
        swirls.setOnClickListener {
            val i: Intent = Intent(SERVICE_NOTIFICATION)
            i.putExtra("command", "flashSwirls")
            sendBroadcast(i)
        }

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
            R.id.action_settings -> {
                Toast.makeText(applicationContext, "settings", Toast.LENGTH_SHORT).show()
                val i: Intent = Intent(SERVICE_NOTIFICATION)
                i.putExtra("command", "list")
                sendBroadcast(i)
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
    }

}
