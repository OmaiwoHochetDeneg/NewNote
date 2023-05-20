package com.example.newnote.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }

    fun insertToDb(title: String, content: String, time: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
        Log.d("Insert", "Insert to ${MyDbNameClass.TABLE_NAME}: Title: $title, Content: $content")
    }

    fun updateItem(id:Int, title: String, content: String, time: String){
        var selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
        Log.d("Insert", "Insert to ${MyDbNameClass.TABLE_NAME}: Title: $title, Content: $content")
    }

    fun removeItemFromDb(id: Int){
        var selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
        Log.d("Delete", "Delete item with id = $id")
    }


    @SuppressLint("Range")
    suspend fun readDbData(searchText: String): ArrayList<ListItem> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListItem>()
        var selection = "${MyDbNameClass.COLUMN_NAME_TITLE} LIKE ?"

        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null, selection, arrayOf("%$searchText%"), null, null, null)
        with(cursor){
            while(this?.moveToNext()!!){
                val dataTitle = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
                val dataContent = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
                val dataTime = cursor?.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))
                val dataId = cursor?.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val item = ListItem()
                item.title = dataTitle.toString()
                item.content = dataContent.toString()
                item.time = dataTime.toString()
                if (dataId != null) {
                    item.id = dataId
                }
                dataList.add(item)
            }
        }
        cursor?.close()
        return@withContext dataList
    }

    fun closeDb(){
        myDbHelper.close()
    }
}