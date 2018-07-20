package com.example.trancaoviet.myhelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Provider taskProvider;

    //Some control in MainActivity

    public static RecyclerView rcvTask;
    public static ArrayList<Task> TaskList;
    public static TaskAdapter taskAdapter;

    FloatingActionButton fab;

    TextView txtDateStart,txtDateEnd,txtDateDevider;
    RadioButton rdOneDayMode, rdSomeDayMode;
    RadioGroup rgViewMode;

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

        taskProvider = new Provider(MainActivity.this);

        addControls();
        addEvents();

        showTask();
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
            showUpCommingTask();
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

    private void showUpCommingTask() {

        Date dateStart = null;
        Date dateEnd = null;

        try {
            dateStart = new SimpleDateFormat("dd/MM/yyyy").parse(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
            dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/9999");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TaskList.clear();
        TaskList.addAll(taskProvider.getTaskList(dateStart,dateEnd));

        taskAdapter.notifyDataSetChanged();
    }

    private void showTask() {

        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateStart.getText().toString());
            dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateEnd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TaskList.clear();
        TaskList.addAll(taskProvider.getTaskList(dateStart,dateEnd));

        taskAdapter.notifyDataSetChanged();
    }

    private void showTaskUnCompelte() {

        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateStart.getText().toString());
            dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateEnd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TaskList.clear();
        TaskList.addAll(taskProvider.getTaskList(dateStart,dateEnd,false));

        taskAdapter.notifyDataSetChanged();
    }

    private void showTaskCompelte() {

        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateStart.getText().toString());
            dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(txtDateEnd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TaskList.clear();
        TaskList.addAll(taskProvider.getTaskList(dateStart,dateEnd,true));
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

        final TextView btnDate, btnTime, btnCancel, btnSave;
        final EditText edtTaskContent = (EditText) dialog.findViewById(R.id.edtTaskContent);
        final Switch btnNotifycation = (Switch) dialog.findViewById(R.id.btnNotifycation);

        btnDate = (TextView) dialog.findViewById(R.id.btnDate);
        btnTime = (TextView) dialog.findViewById(R.id.btnTime);
        btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        btnSave = (TextView) dialog.findViewById(R.id.btnSave);

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
            Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
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
            Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

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
                if(TaskContent.equals("")){
                    TaskContent = "[Không có nội dung]";
                }
                Date date = null;
                Time time = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(btnDate.getText().toString());

                    Date date_time = new SimpleDateFormat("HH:mm").parse(btnTime.getText().toString());
                    time= new Time(date_time.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean hasNotifycation = btnNotifycation.isChecked();

                Task newTask = new Task(TaskContent, date, time, false,hasNotifycation);

                long i = taskProvider.insertTask(newTask);

                if( i != -1 ) {
                    //Toast.makeText(MainActivity.this,"Insert sucessful",Toast.LENGTH_SHORT).show();
                    showTask();
                    edtTaskContent.setText("");

                }

                if( newTask.isHasNotifycation() ) {
                    NotifycationService.TaskList.add(newTask);
                    Intent intent = new Intent(MainActivity.this,NotifycationService.class);
                    startService(intent);
                }
                dialog.dismiss();
            }
        });
    }

    private void addControls() {

        fab = (FloatingActionButton) findViewById(R.id.fab);

        rcvTask = (RecyclerView) findViewById(R.id.rcvTask);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());
        rcvTask.addItemDecoration(dividerItemDecoration);
        rcvTask.setLayoutManager(linearLayoutManager);

        TaskList = new ArrayList<Task>();
        taskAdapter = new TaskAdapter(TaskList,MainActivity.this);
        rcvTask.setAdapter(taskAdapter);

        txtDateStart = (TextView) findViewById(R.id.txtDateStart);
        txtDateDevider = (TextView) findViewById(R.id.txtDeviderCharacter_DateStart_DateEnd);
        txtDateEnd = (TextView) findViewById(R.id.txtDateEnd);

        rgViewMode = (RadioGroup) findViewById(R.id.rgViewMode);
        rdOneDayMode = (RadioButton) findViewById(R.id.rdOneDay);
        rdSomeDayMode = (RadioButton) findViewById(R.id.rdSomeDay);
    }

    private void addEvents() {

        setEvent_fabButtonClick();
        slideToRemoveTask();
        loadCurrentDateforTxtDateEnd();
        setEvent_radioButtonSelectedChange();
        setEvent_txtDateClick();
    }

    private void setEvent_txtDateClick() {
        final Calendar dateSelected = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        txtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                        txtDateEnd.setText(dateFormatter.format(dateSelected.getTime()));
                        if(rdOneDayMode.isChecked())    txtDateStart.setText(txtDateEnd.getText());
                        showTask();
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        txtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                        txtDateStart.setText(dateFormatter.format(dateSelected.getTime()));
                        showTask();
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }
    private void setEvent_radioButtonSelectedChange() {
        rgViewMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rdOneDayMode.isChecked()){
                    txtDateDevider.setVisibility(View.INVISIBLE);
                    txtDateStart.setVisibility(View.INVISIBLE);
                }
                else{
                    txtDateStart.setVisibility(View.VISIBLE);
                    txtDateDevider.setVisibility(View.VISIBLE);
                }
            }
        });
        rdOneDayMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rdOneDayMode.isChecked()){
                    txtDateStart.setText(txtDateEnd.getText());
                    showTask();
                }
            }
        });
    }
    private void loadCurrentDateforTxtDateEnd(){
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        txtDateEnd.setText(dateFormat.format(currentTime.getTime()));
        txtDateStart.setText(dateFormat.format(currentTime.getTime()));
    }
    private void slideToRemoveTask(){
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                taskProvider.deleteTask(new String[]{String.valueOf(TaskList.get(viewHolder.getAdapterPosition()).getId())});
                MainActivity.TaskList.remove(viewHolder.getAdapterPosition());
                MainActivity.taskAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                MainActivity.taskAdapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(),TaskList.size()+1);
            }

        });
        swipeToDismissTouchHelper.attachToRecyclerView(rcvTask);
    }

    private void setEvent_fabButtonClick(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogAddTask();
            }
        });
    }

}
