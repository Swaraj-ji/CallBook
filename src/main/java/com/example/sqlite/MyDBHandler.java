package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class MyDBHandler extends SQLiteOpenHelper {
    public MyDBHandler(@Nullable Context context) {
        super(context, "Users", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table Contacts(name text, number int primary key)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean update_data(String number, String name){
        Log.i(null,"Inside update_data==================");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("name",name);
        c.put("number",number);
        Cursor cur = db.rawQuery("select * from Contacts where number=?",new String[]{number});
        if(cur.getCount()>0) {
            long r = db.update("Contacts", c, "number=?", new String[]{number});
            Log.i(null,"update_data giving result======================");
            if (r == -1) return false;
            else return true;
        }
        else return false;
    }

    public boolean delete_data(String number){
        Log.i(null,"in the Delete_data Function==============================");
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Contacts where number=?",new String[]{number});
        if(cursor.getCount()>0){
            long r = db.delete("Contacts","number=?",new String[]{number});
            if(r==-1) return false;
            else return true;
        }
        else return false;
    }

    public boolean insert_data(String name , String number){
        ContentValues c = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        c.put("name",name);
        c.put("number",number);
        long r = db.insert("Contacts",null,c);
        if(r == -1) return  false;
        else return true;
    }

    public Cursor getInfo(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Contacts",null);
        return cursor;
    }
}
