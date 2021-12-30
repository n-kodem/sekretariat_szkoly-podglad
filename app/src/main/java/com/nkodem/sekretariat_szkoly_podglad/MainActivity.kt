package com.nkodem.sekretariat_szkoly_podglad

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = ThreadPolicy.Builder().permitAll().build()
        val view = this
        val spinner:Spinner = view.findViewById(R.id.sortBy)
        val ordType:Spinner = view.findViewById(R.id.orderType)
        spinner.isVisible=false
        ordType.isVisible=false
        StrictMode.setThreadPolicy(policy)
        val table = view.findViewById<TableLayout>(R.id.itemtable)

        var dataTable: MutableList<MutableList<String>> = mutableListOf()

        fun sortTable(){
            table.removeAllViews()
            val firstRow = TableRow(this@MainActivity)
            for (column in dataTable[0]) {
                val tv = TextView(this@MainActivity)
                tv.setPadding(10,10,10,10)
                tv.textAlignment= View.TEXT_ALIGNMENT_CENTER
                tv.text = column
                firstRow.addView(tv)

            }
            table.addView(firstRow)

            var dataSorted = dataTable.subList(1,dataTable.size-1).sortedBy { it[spinner.selectedItemPosition] }
            //dataSorted.filter {  }
            if(ordType.selectedItemPosition==1) {
                dataSorted = dataSorted.reversed()
            }
            for(element in dataSorted){
                val row = TableRow(this@MainActivity)
                row.weightSum=30F
                for (column in element) {
                    val tv = TextView(this@MainActivity)
                    tv.setPadding(10,10,10,10)
                    tv.textAlignment= View.TEXT_ALIGNMENT_CENTER
                    tv.text = column
                    row.addView(tv)
                }
                table.addView(row)
            }
        }

        view.findViewById<Button>(R.id.loadDataButton).setOnClickListener {
            if(view.findViewById<EditText>(R.id.urlContainer).text.toString()!=""){
                table.removeAllViews()
                table.refreshDrawableState()
                dataTable.clear()
                Log.d("WTF",view.findViewById<EditText>(R.id.urlContainer).text.toString())
                Thread {
                    try{
                        val urls = ArrayList<String>()
                        // Create a URL for the desired page
                        val url = URL(view.findViewById<EditText>(R.id.urlContainer).text.toString()) //My text file location
                        //First open the connection
                        val conn = url.openConnection() as HttpURLConnection
                        conn.connectTimeout = 60000 // timing out in a minute
                        val `in` = BufferedReader(InputStreamReader(conn.inputStream))

                        while (`in`.readLine()?.also { it.toString().let { it1->urls.add(it1) } } != null) {}
                        `in`.close()


                        //since we are in background thread, to post results we have to go back to ui thread. do the following for that
                        this.runOnUiThread(Runnable {
                            for(element in urls){
                                dataTable.add(element.split(",").toMutableList())
                            }

                            spinner.isVisible=true
                            ordType.isVisible=true
                            val arrayList: ArrayList<String> = ArrayList()
                            for(element in dataTable[0]){
                                arrayList.add(element)
                            }
                            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
                            spinner.adapter = arrayAdapter
                            spinner.setSelection(1)
                            sortTable()

                        })
                    }catch (err:Exception){
                        val snackbar = Snackbar.make(
                            view.findViewById(R.id.loadDataButton),
                            "Invalid Url",
                            Snackbar.LENGTH_LONG
                        )
                        val snackbarView = snackbar.view
                        val textView =
                            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                        textView.textSize = 16f
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }.start()
            } else{
                val snackbar = Snackbar.make(
                    view.findViewById(R.id.loadDataButton),
                    "No url provided",
                    Snackbar.LENGTH_LONG
                )
                val snackbarView = snackbar.view
                val textView =
                    snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.textSize = 16f
                snackbar.show()
            }

        }

        var spinnersBeLike = object : OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sortTable()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        spinner.onItemSelectedListener = spinnersBeLike
        ordType.onItemSelectedListener = spinnersBeLike
    }
}
