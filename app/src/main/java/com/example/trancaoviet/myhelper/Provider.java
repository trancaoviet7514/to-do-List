package com.example.trancaoviet.myhelper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Provider {
    SQLiteDatabase database;

    Provider(){
    }

    public Provider(SQLiteDatabase database) {
        this.database = database;

    }

    ArrayList<Task> query(String tableName,String[] SelectColumn,String whereClause,String[] selectionArg,String having,String groupBy,String orderBy){
        ArrayList<Task> result = new ArrayList<Task>();
        Cursor cursor = database.query(tableName, SelectColumn, whereClause, selectionArg, having, groupBy, orderBy);




        return result;
    }
}

