package com.example.newnote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.newnote.databinding.ActivityEditBinding
import com.example.newnote.db.MyIntentConstants
import com.example.newnote.db.MyDbManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {
    private val myDbManager = MyDbManager(this)
    private lateinit var binding: ActivityEditBinding
    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        getMyIntents()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickSave(view: View){
        val myTitle : String = binding.edTitle.text.toString()
        val myContent : String = binding.edContent.text.toString()

        if(myTitle != "" && myContent != ""){

            if(id != -1){
                myDbManager.updateItem(id,myTitle, myContent, getCurrentTime())
            }else{
                myDbManager.insertToDb(myTitle, myContent, getCurrentTime())
            }
            finish()
        }
    }

    private fun getMyIntents(){
        val i = intent
        if (i != null){
            if(i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null){
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, -1)
                binding.edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY).toString())
                binding.edContent.setText(i.getStringExtra(MyIntentConstants.I_CONTENT_KEY).toString())
            }
        }
    }

    private fun getCurrentTime(): String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy hh:mm", Locale.getDefault())
        return formatter.format(time)
    }
}