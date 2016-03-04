package com.purrprogramming.purrprogrammingshirt

import android.bluetooth.BluetoothManager
import android.content.*
import android.os.CountDownTimer
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mbientlab.metawear.AsyncOperation
import com.mbientlab.metawear.MetaWearBleService
import com.mbientlab.metawear.MetaWearBoard
import com.mbientlab.metawear.module.Gpio
import com.mbientlab.metawear.module.NeoPixel
import java.util.*

class PurrProgrammingNotificationListenerService : NotificationListenerService(), ServiceConnection {

    enum class STATE {
        RUBY_SCREEN,
        PURR_PROGRAMMING,
        SOUTH_AFRICA,
        IDLE
    }
    
    public val SERVICE_NOTIFICATION = "com.polyglotprogramminginc.purrprogramming.SERVICE_NOTIFICATION"
    private var TAG: String = this.javaClass.getSimpleName()
    private lateinit var nlservicereciver: NLServiceReceiver
    lateinit private var serviceBinder: MetaWearBleService.LocalBinder
    private val TEST_MAC_ADDRESS = "F3:B4:58:C7:1F:7C"
    private val MW_MAC_ADDRESS = "E9:C0:FF:D9:86:DB"
    private val STRAND: Byte = 0
    private val GPIO_PIN: Byte = 0
    private val RED: String = "Red"
    private val GREEN: String = "Green"
    private val BLUE: String = "Blue"
    private var connected: Boolean = false
    private var mwBoard: MetaWearBoard? = null
    private var southAfricaIndex: Int = 0
    private var rubyIndex: Int = 0
    lateinit private var npModule: NeoPixel
    lateinit private var gpIO: Gpio
    private var freshRequest : Boolean = false
    private var requestRunning : Boolean = false
    private var currentWorkFlow : STATE = STATE.IDLE
    private var nextWorkFlow : STATE = STATE.IDLE
    private var iteration : Int = 0
    private val southAfricaRed = mapOf<String, Byte>(RED to 255.toByte(), GREEN to 0, BLUE to 0)
    private val southAfricaGreen = mapOf<String, Byte>(RED to 0, GREEN to 255.toByte(), BLUE to 0)
    private val southAfricaBlue = mapOf<String, Byte>(RED to 0, GREEN to 0, BLUE to 255.toByte())
    private val southAfricaYellow = mapOf<String, Byte>(RED to 255.toByte(), GREEN to 255.toByte(),
            BLUE to 0)
    private val southAfricaWhite = mapOf<String, Byte>(RED to 255.toByte(), GREEN to 255.toByte(),
            BLUE to 255.toByte())
    private val computerScreenRuby = mapOf<Int, Map<String, Byte>>(
            0 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 0, BLUE to 0),
            1 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 52, BLUE to 23),
            2 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 46, BLUE to 46)
    )

    private val eyes = mapOf<Int, Map<String, Byte>>(
            0 to mapOf<String, Byte>(RED to 0, GREEN to 0, BLUE to 0),
            1 to mapOf<String, Byte>(RED to 37, GREEN to 45, BLUE to 0),
            2 to mapOf<String, Byte>(RED to 109, GREEN to 145.toByte(), BLUE to 0),
            3 to mapOf<String, Byte>(RED to 194.toByte(), GREEN to 232.toByte(), BLUE to 0),
            4 to mapOf<String, Byte>(RED to 195.toByte(), GREEN to 255.toByte(), BLUE to 0)
    )

    private val computerScreenPurrProgramming = mapOf<Int, Map<String, Byte>>(
            0 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 64, BLUE to 0),
            1 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 128.toByte(), BLUE to 0),
            2 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 110, BLUE to 23)
    )

    private val paw = mapOf<Int, Map<String, Byte>>(
            0 to mapOf<String, Byte>(RED to 255.toByte(), GREEN to 0, BLUE to 0),
            1 to mapOf<String, Byte>(RED to 0, GREEN to 0, BLUE to 255.toByte())
    )

    override fun onCreate() {
        super.onCreate();
        nlservicereciver = NLServiceReceiver();
        val filter: IntentFilter = IntentFilter();
        filter.addAction(SERVICE_NOTIFICATION);
        registerReceiver(nlservicereciver, filter);
        bindService(Intent(this, MetaWearBleService::class.java),
                this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
        unbindService(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "**********  onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        if (sbn.packageName.equals("com.twitter.android")) {
            // turn into rubyfuza
            Log.i(TAG, "aaaand the text is " + sbn.notification.extras.get("android.text"))
            if (sbn.notification.extras.get("android.text").equals("@rubyfuza")){
                Log.i(TAG, "new polyglot test 2 message")
                if(currentWorkFlow == STATE.IDLE){
                    Log.i(TAG, "sending ruby intent")
                    val i: Intent = Intent(SERVICE_NOTIFICATION)
                    i.putExtra("command", "ruby")
                    sendBroadcast(i)
                }else{
                    nextWorkFlow = STATE.RUBY_SCREEN
                }
            }else if(sbn.notification.extras.get("android.text").equals("@PurrProgramming")){
                if(currentWorkFlow == STATE.IDLE){
                    Log.i(TAG, "sending purr programming intent")
                     val i: Intent = Intent(SERVICE_NOTIFICATION)
                    i.putExtra("command", "purr")
                    sendBroadcast(i)
                }else{
                    nextWorkFlow = STATE.PURR_PROGRAMMING
                }
                // this should be purr programming
            }else if(sbn.notification.extras.get("android.text").equals("@lgleasain")){
                 if(currentWorkFlow == STATE.IDLE){
                    Log.i(TAG, "sending south africa intent")
                     val i: Intent = Intent(SERVICE_NOTIFICATION)
                    i.putExtra("command", "south_africa")
                    sendBroadcast(i)
                }else{
                    nextWorkFlow = STATE.SOUTH_AFRICA
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "********** onNOtificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }

    override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
        serviceBinder = service as MetaWearBleService.LocalBinder
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val remoteDevice = btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS)
        mwBoard = serviceBinder.getMetaWearBoard(remoteDevice)
        mwBoard!!.setConnectionStateHandler(connectionStateHandler)
        mwBoard!!.connect()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
    }

    private val connectionStateHandler = object : MetaWearBoard.ConnectionStateHandler() {
        override fun failure(status: Int, error: Throwable?) {
            super.failure(status, error)
        }

        override fun disconnected() {
            mwBoard!!.connect()
            Log.i("Purr Programming Service", "disconnected")
        }

        override fun connected() {
            Log.i("Purr Programming Service", "connected")
            if (!connected) {
                Log.i("Main Actiuvity", "Initializing neopixels")
                connected = true
            }
            val result = mwBoard!!.readRssi()
            result.onComplete(object : AsyncOperation.CompletionHandler<Int>() {
                override fun success(rssi: Int) {
                    Log.i("RSSI is ", Integer.toString(rssi))
                }
            })
        }

    }

    private fun setSouthAfrica(offset: Int) {
        try {
            val pixels = Array(5,
                    { i ->
                        (i + 1 + offset).mod(5)
                    })

            val colors: HashMap<Int, Map<String, Byte>> = HashMap<Int, Map<String, Byte>>()

            colors.put(0, southAfricaWhite)
            colors.put(1, southAfricaBlue)
            colors.put(2, southAfricaRed)
            colors.put(3, southAfricaYellow)
            colors.put(4, southAfricaGreen)

            for (index in 0..4) {
                npModule.setPixel(STRAND, (pixels[index] + 1).toByte(),
                        colors[index]!!.get(RED)!!,
                        colors[index]!!.get(GREEN)!!,
                        colors[index]!!.get(BLUE)!!)
            }

        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun startSouthAfrica() {
        object : CountDownTimer(30000, 2000) {

            override public fun onTick(millisUntilFinished: Long) {
                setSouthAfrica(southAfricaIndex++)
                setPaw(southAfricaIndex.mod(2))
            }

            override public fun onFinish() {
                flashSwirls()
            }

        }.start();
    }

    private fun setRubyScren(colorIndex: Int) {
        try {
            val color: Map<String, Byte> = computerScreenRuby[colorIndex]!!
            for (pixel in 2..5) {
                npModule.setPixel(STRAND, pixel.toByte(),
                        color[RED]!!,
                        color[GREEN]!!,
                        color[BLUE]!!)
            }
        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun setCatEye(eye: Int, colorIndex: Int) {
        try {
            val color: Map<String, Byte> = eyes[colorIndex]!!
            npModule.setPixel(STRAND, eye.toByte(),
                    color[RED]!!,
                    color[GREEN]!!,
                    color[BLUE]!!)
        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun setPaw(colorIndex: Int) {
        try {
            val color: Map<String, Byte> = paw[colorIndex]!!
            npModule.setPixel(STRAND, 0.toByte(),
                    color[RED]!!,
                    color[GREEN]!!,
                    color[BLUE]!!)
        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun setPurrScreen(colorIndex: Int) {
        try {
            val color: Map<String, Byte> = computerScreenPurrProgramming[colorIndex]!!
            for (pixel in 2..5) {
                npModule.setPixel(STRAND, pixel.toByte(),
                        color[RED]!!,
                        color[GREEN]!!,
                        color[BLUE]!!)
            }
        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun setIdle() {
        try {
            for (pixel in 0..5){
                 npModule.setPixel(STRAND, pixel.toByte(),
                        0.toByte(),
                        0,
                        255.toByte())
            }
        } catch(e: Exception) {
            Log.i("problem with ", e.toString())
        }
    }

    private fun startRubyScreen() {
        currentWorkFlow = STATE.RUBY_SCREEN
        object : CountDownTimer(30000, 2000) {

            override public fun onTick(millisUntilFinished: Long) {
                setRubyScren((rubyIndex++).mod(3))
                setCatEye(1, rubyIndex.mod(5))
                setPaw(rubyIndex.mod(2))
            }

            override public fun onFinish() {
                flashSwirls()
            }

        }.start();
    }

    private fun startPurrPorgrammingScreen() {
        object : CountDownTimer(30000, 2000) {

            override public fun onTick(millisUntilFinished: Long) {
                setPurrScreen((rubyIndex++).mod(3))
                setCatEye(1, rubyIndex.mod(5))
                setPaw(rubyIndex.mod(2))
            }

            override public fun onFinish() {
                flashSwirls()
            }

        }.start();
    }

    private fun startBackEyes() {
        setCatEye(6, 4)
        setCatEye(7, 4)
        object : CountDownTimer(60000, 10000) {

            override public fun onTick(millisUntilFinished: Long) {
                object : CountDownTimer(2000, 250) {
                    var index = 3
                    override public fun onTick(millisUntilFinished: Long) {
                        setCatEye(7, index)
                        if (index > 0) {
                            index++
                        } else {
                            index--
                        }
                    }

                    override public fun onFinish() {
                        currentWorkFlow = STATE.IDLE
                    }
                }.start()
            }

            override public fun onFinish() {
            }

        }.start()
    }

    private fun turnOnSwirl(pin: Int) {
        gpIO.setDigitalOut(pin.toByte())
    }

    private fun turnOffSwirl(pin: Int) {
        gpIO.clearDigitalOut(pin.toByte())
    }

    private fun flashSwirls() {
        object : CountDownTimer(30000, 500) {

            override public fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished.mod(2).toInt() == 0) {
                    turnOnSwirl(1)
                    turnOffSwirl(2)
                } else {
                    turnOnSwirl(2)
                    turnOffSwirl(1)
                }
            }

            override public fun onFinish() {
                turnOnSwirl(1)
                turnOnSwirl(2)
                if(nextWorkFlow == STATE.IDLE) {
                    if(iteration < 2) {
                        iteration++
                        if (currentWorkFlow == STATE.RUBY_SCREEN) {
                            // add eye blink at the end of this later.
                            startRubyScreen()
                        }else if(currentWorkFlow == STATE.PURR_PROGRAMMING){
                            startPurrPorgrammingScreen()
                        }else if(currentWorkFlow == STATE.SOUTH_AFRICA){
                            startSouthAfrica()
                        }
                    }else{
                        Log.i(TAG, "starting back eyes")
                        setIdle()
                        startBackEyes()
                    }
                }else{
                    if(nextWorkFlow == STATE.PURR_PROGRAMMING){
                        nextWorkFlow = STATE.IDLE
                        currentWorkFlow = STATE.PURR_PROGRAMMING
                        iteration = 0
                        startPurrPorgrammingScreen()
                    }else if(nextWorkFlow == STATE.RUBY_SCREEN){
                        nextWorkFlow = STATE.IDLE
                        currentWorkFlow = STATE.RUBY_SCREEN
                        iteration = 0
                        startRubyScreen()
                    }else if(nextWorkFlow == STATE.SOUTH_AFRICA){
                        nextWorkFlow = STATE.IDLE
                        currentWorkFlow = STATE.SOUTH_AFRICA
                        iteration = 0
                        startSouthAfrica()
                    }
                }
            }

        }.start();
    }

    internal inner class NLServiceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("command").equals("clearall")) {
                this@PurrProgrammingNotificationListenerService.cancelAllNotifications()
            } else if (intent.getStringExtra("command").equals(("initialize"))) {
                npModule = mwBoard!!.getModule(NeoPixel::class.java)
                npModule.initializeStrand(STRAND, NeoPixel.ColorOrdering.MW_WS2811_GRB,
                        NeoPixel.StrandSpeed.SLOW, GPIO_PIN, 8)
                gpIO = mwBoard!!.getModule(Gpio::class.java)
            } else if (intent.getStringExtra("command").equals("south_africa")) {
                startSouthAfrica()
            } else if (intent.getStringExtra("command").equals("ruby")) {
                startRubyScreen()
            } else if (intent.getStringExtra("command").equals("eyes")) {
                startBackEyes()
            } else if (intent.getStringExtra("command").equals("purr")) {
                startPurrPorgrammingScreen()
            } else if (intent.getStringExtra("command").equals("flashSwirls")) {
                flashSwirls()
            }
        }
    }
}
