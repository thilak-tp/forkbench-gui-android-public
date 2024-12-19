package com.forktail.barebonesforkbench

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var scoreText: TextView
    lateinit var benchButton: Button
    //private lateinit var glSurfaceView: GLSurfaceView






    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        /*val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this) { isNetworkAvailable ->
            isNetworkAvailable?.let {
                updateUI(it)
            }

         */
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        textView = findViewById(R.id.textView)
        scoreText = findViewById(R.id.scoreText)
        benchButton = findViewById(R.id.Benchbutton)
        /*
        val supportsEs2 = packageManager.hasSystemFeature("android.hardware.opengles.aep")
        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(MyGLRenderer())
        }
        else {
            // This is where you could create an OpenGL ES 1.x compatible renderer if needed.
         return
        }
       */
        //setContentView(glSurfaceView)


        Log.d("GL", "GL_RENDERER = "   + GLES32.glGetString( GLES32.GL_RENDERER   ));
        Log.d("GL", "GL_VENDOR = "     + GLES32.glGetString( GLES32.GL_VENDOR     ));
        Log.d("GL", "GL_VERSION = "    + GLES32.glGetString( GLES32.GL_VERSION    ));
        Log.i("GL", "GL_EXTENSIONS = " + GLES32.glGetString( GLES32.GL_EXTENSIONS ));

        // To get the total RAM memory
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem/(1024 * 1024)

        // To get the number of cores of the CPU
        val numberOfCores = Runtime.getRuntime().availableProcessors()

        // To Get current battery percentage - NOT WOKRING!!
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            iFilter ->
            registerReceiver(null, iFilter)
        }

        val batteryPercentage: Float? = batteryStatus?.let { Intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        // To get the total and free disk space of the phone
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize : Long
        val totalBlocks: Long
        val availableBlocks: Long

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            blockSize = stat.blockSizeLong
            totalBlocks = stat.blockCountLong
            availableBlocks = stat.availableBlocksLong
        }
        else
        {
            blockSize = stat.blockSize.toLong()
            totalBlocks = stat.blockCount.toLong()
            availableBlocks = stat.availableBlocks.toLong()

        }
        val totalSpace = totalBlocks * blockSize
        val freeSpace = availableBlocks + blockSize
        //To get display information
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight= displayMetrics.heightPixels
        /*
        To get device storage information
         */
        val stat1 = StatFs(Environment.getExternalStorageDirectory().path)
        val bytesAvailable = stat1.blockSize.toLong() * stat.blockCount.toLong()
        val megAvailable = bytesAvailable / 1048576
        val path: File = Environment.getDataDirectory()
        val stat2 = StatFs(path.path)
        val blockSize1 = stat2.blockSize.toLong()
        val availableBlocks1 = stat2.availableBlocks.toLong()
        val format: String = Formatter.formatFileSize(this, availableBlocks1 * blockSize1)
        val total = stat2.getTotalBytes()/(1024*1024*1024)
        val totalUsedPecentage : Float
        totalUsedPecentage = (availableBlocks1*blockSize1/total).toFloat()*1000


        // get default display from the windows manager
        val display = windowManager.defaultDisplay

        // declare and initialize a point
        val size = Point()

        // store the points related details from the display variable in the size variable
        display.getSize(size)

        // store the point information in integer variables width and height
        // where .x extracts width pixels and .y extracts height pixels
        val width = size.x
        val height = size.y
        val arrayList = getMemoryInfo()
        val availRAMMB = arrayList.get(0) /(1024*1024)
        val totalRAMMB = arrayList.get(1)/(1024*1024)
        /*
        var bluetoothStatus : Boolean = false
        val btAdapter = (if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1))
            (applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        else
            (BluetoothAdapter.getDefaultAdapter()))

        if (btAdapter == null) {
            bluetoothStatus = false
        }
        if (btAdapter.state == BluetoothAdapter.STATE_ON) {
            bluetoothStatus = true
        }
        */

        //TO get mac addres

        benchButton.setOnClickListener {
            val benchmarkResult = benchmarkCpu()
            scoreText.text = benchmarkResult
        }
        val systemInfo =
            """------------------Mobile--------------------
            |Manufacturer:          ${Build.MANUFACTURER}
            |Model:                 ${Build.MODEL}
            |Brand:                 ${Build.BRAND}
            |Device:                ${Build.DEVICE}
            |Product:               ${Build.PRODUCT}
            |Board:                 ${Build.BOARD}
            |Display Width:         ${screenWidth}
            |Display Height:        ${screenHeight}
            |Display :              ${width} X ${height}
            |Display Type:          ${Build.DISPLAY}
            
            |-----------------CPU-------------------------
            |Total RAM (MB):        ${totalMemory}
            |Number of Cores:       ${numberOfCores}
            |Battery (%):           ${getBatteryPercentage(applicationContext)}
               
            |-----------------Android--------------------
            |Android Version:       ${Build.VERSION.RELEASE}
            |SDK Version:           ${Build.VERSION.SDK_INT}
            |Android Codename:      ${Build.VERSION.CODENAME}
            |Network Capable?:      ${isOnline(applicationContext)}
            |Storage Available:     ${format}
            |Storage Total:         ${total} GB
            """
            .trimMargin()

        textView.text = systemInfo
    }
    private fun getScreenResolution(context: Context): String {
        val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        return "{$width,$height}"
    }
    private fun benchmarkCpu(): String {
        val iterations = 1000000000
        val startTime = SystemClock.elapsedRealtime()
        var dummy = 0.0

            for (i in 0 until iterations) {
                dummy += sqrt(i.toDouble())
        }
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        return "Benchmark completed \nIn: $duration ms \nResultant Value: $dummy" }
    private fun getMemoryInfo(): ArrayList<Float> {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        val runtime = Runtime.getRuntime()
        /*val builder = StringBuilder()
        builder.append("Available Memory: ").append(memoryInfo.availMem).append("\
                n").append("Total Memory: ").append(memoryInfo.totalMem).append("
        ").append("Runtime
                Maximum Memory: ").append(runtime.maxMemory()).append("
        ").append("Runtime Total Memory:
        ").append(runtime.totalMemory()).append("
        ").append("Runtime Free Memory:
        ").append(runtime.freeMemory()).append("
        ")
         */
        var result = memoryInfo.availMem.toString() + " \n" + memoryInfo.totalMem + "\n" + runtime.maxMemory() + "\n" + runtime.totalMemory() + "\n" + runtime.freeMemory()
        var arrayList = ArrayList<Float>()
        arrayList.add(0,memoryInfo.availMem.toFloat())
        arrayList.add(1, memoryInfo.totalMem.toFloat())
        arrayList.add(2, runtime.maxMemory().toFloat())
        arrayList.add(3, runtime.totalMemory().toFloat())
        arrayList.add(4, runtime.freeMemory().toFloat())
        return arrayList
    }
    // CHeck for internet status
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    fun getBatteryPercentage(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= 21) {
            val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        } else {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, iFilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level!! / scale!!.toDouble()
            return  (batteryPct * 100).toInt()
        }
    }

}


