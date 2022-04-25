package com.arifsutriyono.akselerogyrop9

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.contextaware.ContextAware


class MainActivity : AppCompatActivity(),SensorEventListener {

    private var sensorManager: SensorManager? = null
    //    instansiasi nullable sensor manager sehingga dapat menampung data null

    private var running = false

    //    membuat variable yang akan menghitung jumlah langkah dengan nilai awal 0 float
    private var jumlahLangkah = 0f

    private var jumlahLangkahSebelumnya = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData() //memanggil fungsi load data
        resetSteps() //memanggil fungsi reset data

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager //instansiasi sensor manager

    }

    override fun onResume() {
        super.onResume()
        running = true

        //berfungsi untuk mengembalikan nilai langkah
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            //menampilkan pesan ketika step sensor bernilai null yang memiliki kemungkinan akselero meter tidak ada
            Toast.makeText(this, "tidak ada sensor yang terdeteksi ", Toast.LENGTH_SHORT)

        } else {

            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        var tv_jumlahLangkah = findViewById<TextView>(R.id.tv_jumlahLangkah)

        if (running) {
            //melakukan pembacaan terhadap event yang terjadi
            jumlahLangkah = event!!.values[0]

            var langkahSekarang = jumlahLangkah.toInt() - jumlahLangkahSebelumnya.toInt()

            tv_jumlahLangkah.text = ("$langkahSekarang") //mengambil data langkah
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    private fun resetSteps() {
        var tv_jumlahLangkah = findViewById<TextView>(R.id.tv_jumlahLangkah)

        tv_jumlahLangkah.setOnClickListener {
            //ini akan memberikan pesan ketika pengguna menekan angka dengan sentuhan pendek
            Toast.makeText(this, "tekan lama untuk melakukan reset", Toast.LENGTH_SHORT).show()
        }
        tv_jumlahLangkah.setOnLongClickListener {
            jumlahLangkahSebelumnya = jumlahLangkah

            tv_jumlahLangkah.text =
                0.toString() //ini akan mereplace atau menggantikan nilai data langkah dengan nilai 0

            saveData()//fungsi untuk menyimpan data langkah

            true
        }
    }

    private fun saveData() {
        //variabel ini akan mengijinkan kita untuk menyimpan data yang ada
        val sharedPreferences = getSharedPreferences("myprefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", jumlahLangkahSebelumnya)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        //ini akan membaca data key1 dari myprefs
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        Log.d("MainActivity", "$savedNumber")

        jumlahLangkahSebelumnya = savedNumber
    }
}