package com.example.trancaoviet.myhelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogAddTask();
            }
        });

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

    private void showDialogAddTask() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_input);
        dialog.setCancelable(false);
        dialog.show();

        // define all controls of dialog and set event for them
        mapAndSetEventControlForDialog(dialog);
    }

    private void setEventForControlInDialog(Dialog dialog) {

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

                        btnTime.setText(selectedHour+":"+selectedMinute);

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
                DatePickerDialog  datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);

                        btnDate.setText(dateFormatter.format(dateSelected.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                btnDate.setText(dateFormatter.format(dateSelected.getTime()));
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TaskTitle = edtTaskTitle.getText().toString();
                if(TaskTitle.equals("")) TaskTitle = "[Không có tiêu đề]";
                String TaskContent = edtTaskContent.getText().toString();
                String Date = btnDate.getText().toString();
                String Time = btnTime.getText().toString();
                boolean Notifycation = btnNotifycation.isChecked();

                ContentValues contentValues = new ContentValues();
                contentValues.put("Title",TaskTitle);
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
                    edtTaskTitle.setText("");
                }

                if(Notifycation){
                    NotifycationService.TaskList.add(new Task(TaskTitle,TaskContent,Date,Time));
                    Intent intent = new Intent(MainActivity.this,NotifycationService.class);
                    startService(intent);
                }
            }
        });
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

        } else if (id == R.id.nav_UncompleteTask) {

        }
//

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTaskUpComing() {

    }

    String DATABASE_NAME="HelperDB.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    static SQLiteDatabase database=null;

    public static RecyclerView rcvTask;
    public static ArrayList<Task> TaskList;
    public static TaskAdapter taskAdapter;

    private void showAllTaskOnDatabase() {

        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor=database.query("tbTask",null,null,null,null,null,"Date,Time");
        TaskList.clear();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String Title=cursor.getString(1);
            String Content = cursor.getString(2);
            String Date = cursor.getString(3);
            String Time = cursor.getString(4);
            String Notifycation = cursor.getString(5);
            boolean Complete =  (cursor.getInt(6) == 1);
            TaskList.add(new Task(id, Title,Content,Date,Time,Notifycation,Complete));//cần chỉnh lại chỗ này------------------------------------------------------------------------------
        }
        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    private void addEvents() {

    }

    private void addControls() {
        rcvTask = (RecyclerView) findViewById(R.id.rcvTask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());
        rcvTask.addItemDecoration(dividerItemDecoration);
        rcvTask.setLayoutManager(linearLayoutManager);

        TaskList = new ArrayList<Task>();
        taskAdapter = new TaskAdapter(TaskList,MainActivity.this);
        rcvTask.setAdapter(taskAdapter);

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
