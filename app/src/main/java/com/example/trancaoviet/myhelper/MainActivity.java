package com.example.trancaoviet.myhelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String DATABASE_NAME="HelperDB.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    static SQLiteDatabase database=null;

    public static RecyclerView rcvTask;
    public static ArrayList<Task> TaskList;
    public static TaskAdapter taskAdapter;
    Spinner spType;

    FloatingActionButton fab;
    TextView valueOfSpiner;
    Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        xulySaoChepCSDLtuAsset();

        addControls();
        addEvents();

        showAllTaskOnDatabase();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_UpcomingTask) {
            showTaskUpComing();
        } else if (id == R.id.nav_CompleteTask) {
            showTaskCompelte();

        } else if (id == R.id.nav_UncompleteTask) {
            showTaskUnCompelte();
        }
//

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTaskUnCompelte() {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(currentTime.getTime());
        String time = timeFormat.format(currentTime.getTime());

        TaskList.clear();
        TaskList.addAll(getTaskList("tbTask",null,"(Date > ? or (Date = ? and Time >= ?))and Complete = 0",new String[]{date,date,time},null,null,"Date,Time"));

        taskAdapter.notifyDataSetChanged();
    }

    private void showTaskCompelte() {

        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(currentTime.getTime());
        String time = timeFormat.format(currentTime.getTime());

        TaskList.clear();
        TaskList.addAll(getTaskList("tbTask",null,"(Date > ? or (Date = ? and Time >= ?))and Complete = 1",new String[]{date,date,time},null,null,"Date,Time"));
        taskAdapter.notifyDataSetChanged();
    }

    private void showDialogAddTask() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_input);
        dialog.setCancelable(false);
        dialog.show();

        // define all controls of dialog and set event for them
        mapAndSetEventControlForDialog(dialog);
    }


    private void mapAndSetEventControlForDialog(final Dialog dialog) {

        final Button btnDate, btnTime, btnCancel, btnSave;
        final EditText edtTaskTitle = (EditText) dialog.findViewById(R.id.edtTaskTitle);
        final EditText edtTaskContent = (EditText) dialog.findViewById(R.id.edtTaskContent);
        final ToggleButton btnNotifycation = (ToggleButton) dialog.findViewById(R.id.btnNotifycation);

        btnDate = (Button) dialog.findViewById(R.id.btnDate);
        btnTime = (Button) dialog.findViewById(R.id.btnTime);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnSave = (Button) dialog.findViewById(R.id.btnSave);

        //load currentTime
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(currentTime.getTime());
        btnDate.setText(date);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        dateSelected.set(0,0,0,selectedHour,selectedMinute);
                        btnTime.setText(timeFormatter.format(dateSelected.getTime()));

                    }
                }, hour, minute, true);

                mTimePicker.setTitle("Pick Time");
                mTimePicker.show();
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);

                        btnDate.setText(dateFormatter.format(dateSelected.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TaskContent = edtTaskContent.getText().toString();
                String Date = btnDate.getText().toString();
                String Time = btnTime.getText().toString();
                boolean Notifycation = btnNotifycation.isChecked();

                ContentValues contentValues = new ContentValues();
                contentValues.put("Content", TaskContent);
                contentValues.put("Date", Date);
                contentValues.put("Time", Time);
                contentValues.put("Notifycation", String.valueOf(Notifycation));
                contentValues.put("Complete",0);

                long i = database.insert("tbTask", null, contentValues);

                if(i!=-1){
                    Toast.makeText(MainActivity.this,"Insert sucessful",Toast.LENGTH_SHORT).show();
                    showAllTaskOnDatabase();
                    edtTaskContent.setText("");

                }

                if(Notifycation){
                    NotifycationService.TaskList.add(new Task(TaskContent,TaskContent,Date,Time));
                    Intent intent = new Intent(MainActivity.this,NotifycationService.class);
                    startService(intent);
                }
                dialog.dismiss();
            }
        });
    }



    ArrayList<Task> getTaskList(String tableName,String[] SelectColumn,String whereClause,String[] selectionArg,String having,String groupBy,String orderBy){
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
            String Date = cursor.getString(ColumnIndex_Date);
            String Time = cursor.getString(ColumnIndex_Time);
            String Notifycation = cursor.getString(ColumnIndex_Notifycation);
            boolean Complete =  (cursor.getInt(ColumnIndex_Complete) == 1);

            result.add(new Task(id,Content,Date,Time,Notifycation,Complete));
        }
        cursor.close();
        return result;
    }

    private void showTaskUpComing() {

        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(currentTime.getTime());
        String time = timeFormat.format(currentTime.getTime());

        TaskList.clear();
        TaskList.addAll(getTaskList("tbTask",null,"Date > ? or (Date = ? and Time >= ?)",new String[]{date,date,time},null,null,"Date,Time"));

        taskAdapter.notifyDataSetChanged();
    }

    private void showAllTaskOnDatabase() {

        TaskList.clear();
        TaskList.addAll(getTaskList("tbTask",null,null,null,null,null,"Date,Time"));

        taskAdapter.notifyDataSetChanged();
    }

    private void addEvents() {
        slideToRemoveTask();
        openDadabase();
        setEvent_fabButtonClick();

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2,long arg3) {

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //load currentTime for value of spiner
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(currentTime.getTime());
        valueOfSpiner.setText(date);


        valueOfSpiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);

                        valueOfSpiner.setText(dateFormatter.format(dateSelected.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    void slideToRemoveTask(){
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                MainActivity.database.delete("tbTask","id = ?",new String[]{String.valueOf(TaskList.get(viewHolder.getAdapterPosition()).getId())});
                MainActivity.TaskList.remove(viewHolder.getAdapterPosition());
                MainActivity.taskAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                MainActivity.taskAdapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(),TaskList.size()+1);
            }

        });
        swipeToDismissTouchHelper.attachToRecyclerView(rcvTask);
    }
    void openDadabase(){
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
    }
    void setEvent_fabButtonClick(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogAddTask();
            }
        });
    }


    //---------------------------------

    private void addControls() {

        valueOfSpiner = (TextView) findViewById(R.id.textValueSp);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        spType = (Spinner) findViewById(R.id.spType);
        ArrayList<String> spList = new ArrayList<String>();
        spList.add("Ngày");
        spList.add("Tuần");
        spList.add("Tháng");
        spList.add("Năm");

        ArrayAdapter<String> spTypeAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,spList);
        spTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spType.setAdapter(spTypeAdapter);
        spTypeAdapter.notifyDataSetChanged();


        rcvTask = (RecyclerView) findViewById(R.id.rcvTask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());
        rcvTask.addItemDecoration(dividerItemDecoration);
        rcvTask.setLayoutManager(linearLayoutManager);

        TaskList = new ArrayList<Task>();
        taskAdapter = new TaskAdapter(TaskList,MainActivity.this);
        rcvTask.setAdapter(taskAdapter);

    }

    private void xulySaoChepCSDLtuAsset() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try
            {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
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
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
}
