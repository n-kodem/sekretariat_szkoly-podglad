package com.nkodem.sekretariat_szkoly_podglad

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = ThreadPolicy.Builder().permitAll().build()
        var view = this
        StrictMode.setThreadPolicy(policy)
        view.findViewById<Button>(R.id.loadDataButton).setOnClickListener {
            Thread {
                val urls = ArrayList<String>() //to read each line
                //TextView t; //to show the result, please declare and find it inside onCreate()
//                try {
                    // Create a URL for the desired page
                    val url = URL("https://raw.githubusercontent.com/n-kodem/sekretariat_szkoly-podglad/master/exported_data.txt?token=AOTFRIY325EH54OA5ZTPQPDB2XPHW") //My text file location
                    //First open the connection
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 60000 // timing out in a minute
                    val `in` = BufferedReader(InputStreamReader(conn.inputStream))

                    while (`in`.readLine()?.also { it.toString().let { it1->urls.add(it1) } } != null) {}
                    `in`.close()


                //since we are in background thread, to post results we have to go back to ui thread. do the following for that
                this.runOnUiThread(Runnable {
                    Log.d("WEBSITE TEXT",urls.toString())
                    var table = view.findViewById<TableLayout>(R.id.itemtable)

                    for(element in urls){

                        var row = TableRow(this@MainActivity)

                        row.weightSum=30F
                        for (column in element.split(",")) {
                            var tv = TextView(this@MainActivity)
                            tv.setPadding(10,10,10,10)
                            tv.textAlignment= View.TEXT_ALIGNMENT_CENTER
                            tv.text = column
                            row.addView(tv)
                        }
                        table.addView(row)
                    }

                })
            }.start()
        }
    }
}
