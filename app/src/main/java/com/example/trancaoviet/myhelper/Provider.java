package com.example.trancaoviet.myhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Provider {
    String DATABASE_NAME="HelperDB.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    static SQLiteDatabase database=null;
    Context mContext;


    public Provider(Context context) {
        this.mContext = context;
        initDataBase();
        openDadabase();
    }

    private void initDataBase() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try
            {
                CopyDataBaseFromAsset();
                //Toast.makeText(mContext, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                //Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openDadabase(){
        database = mContext.openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput = mContext.getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(mContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();

            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDatabasePath() {
        return mContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

    private ArrayList<Task> query(String tableName,String[] SelectColumn,String whereClause,String[] selectionArg,String having,String groupBy,String orderBy){
        ArrayList<Task> result = new ArrayList<Task>();
        Cursor cursor=database.query(tableName,SelectColumn,whereClause,selectionArg,null,null,orderBy);

        while (cursor.moveToNext())
        {
            int ColumnIndex_ID = cursor.getColumnIndex("ID");
            int ColumnIndex_Content = cursor.getColumnIndex("Content");
            int ColumnIndex_Date = cursor.getColumnIndex("Date");
            int ColumnIndex_Time = cursor.getColumnIndex("Time");
            int ColumnIndex_Notifycation = cursor.getColumnIndex("Notifycation");
            int ColumnIndex_Complete = cursor.getColumnIndex("Complete");

            int id = cursor.getInt(ColumnIndex_ID);
            String Content = cursor.getString(ColumnIndex_Content);
            Date date = null;
            Time time = null;
            try {
                String dddd = cursor.getString(ColumnIndex_Date);
                date = new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(ColumnIndex_Date));

                Date date_time = new SimpleDateFormat("HH:mm").parse(cursor.getString(ColumnIndex_Time));
                time = new Time(date_time.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            boolean hasNotifycation = cursor.getString(ColumnIndex_Notifycation).equals("1");
            boolean isComplete =  cursor.getString(ColumnIndex_Complete).equals("1");


            result.add(new Task(id,Content,date,time, hasNotifycation,isComplete));
        }
        cursor.close();
        return result;
    }

    public ArrayList<Task> getTaskList(Date dateStart, Date dateEnd){
        ArrayList<Task> result = new ArrayList<>();

        result.addAll(query("tbTask",null,null,null,null,null,"Date,Time"));
        Task task;

        for ( int i = 0; i < result.size(); i++) {

            task = result.get(i);
            if ( task.getDate().compareTo(dateStart) < 0 ||  task.getDate().compareTo(dateEnd) > 0 ) {
                result.remove(i);
                i--;
            }
        }

        return result;
    }

    public ArrayList<Task> getTaskList(Date dateStart, Date dateEnd,boolean isComplete){
        ArrayList<Task> result = new ArrayList<>();

        String Complete = isComplete==true?"1":"0";
        result.addAll(query("tbTask",null,"Complete = ?",new String[]{Complete},null,null,"Date,Time"));
        Task task;
        for(int i = 0; i < result.size();i++){
            task = result.get(i);
            try {
                Date taskDate = new SimpleDateFormat("dd/MM/yyyy").parse(task.getDate().toString());
                if(taskDate.compareTo(dateStart)<0 ||(taskDate.compareTo(dateEnd)>0)){
                    result.remove(i);
                    i--;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    public int deleteTask(String[] id){
        return database.delete("tbTask","id = ?",id);
    }

    public long insertTask(Task task){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Content", task.getContent());
        contentValues.put("Date", new SimpleDateFormat("dd/MM/yyyy").format(task.getDate()));
        contentValues.put("Time", task.getTime().toString());
        contentValues.put("Notifycation", task.isHasNotifycation());
        contentValues.put("Complete",0);
        return database.insert("tbTask", null, contentValues);
    }
}

