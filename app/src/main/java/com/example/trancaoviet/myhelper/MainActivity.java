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
            dateStart = Calendar.getInstance().getTime();
            dateEnd = Utils.dateFormat.parse("31/12/9999");
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
            dateStart = Utils.dateFormat.parse(txtDateStart.getText().toString());
            dateEnd = Utils.dateFormat.parse(txtDateEnd.getText().toString());
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
            dateStart = Utils.dateFormat.parse(txtDateStart.getText().toString());
            dateEnd = Utils.dateFormat.parse(txtDateEnd.getText().toString());
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
            dateStart = Utils.dateFormat.parse(txtDateStart.getText().toString());
            dateEnd = Utils.dateFormat.parse(txtDateEnd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TaskList.clear();
        TaskList.addAll(taskProvider.getTaskList(dateStart,dateEnd,true));
        taskAdapter.notifyDataSetChanged();
    }

    private void addControls() {

        fab = (FloatingActionButton) findViewById(R.id.fab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());

        TaskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(TaskList,MainActivity.this);

        rcvTask = (RecyclerView) findViewById(R.id.rcvTask);
        rcvTask.addItemDecoration(dividerItemDecoration);
        rcvTask.setLayoutManager(linearLayoutManager);
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

        txtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar currentDate = Calendar.getInstance();

                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);

                        txtDateEnd.setText(Utils.dateFormat.format( dateSelected.getTime() ) );
                        if( rdOneDayMode.isChecked() )    txtDateStart.setText( txtDateEnd.getText() );

                        showTask();
                    }

                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
        txtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar currentDate = Calendar.getInstance();

                final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);

                        txtDateStart.setText(Utils.dateFormat.format(dateSelected.getTime()));

                        showTask();
                    }

                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }
    private void setEvent_radioButtonSelectedChange() {

        rgViewMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if( rdOneDayMode.isChecked() ) {

                    txtDateDevider.setVisibility(View.INVISIBLE);
                    txtDateStart.setVisibility(View.INVISIBLE);
                }
                else {
                    txtDateStart.setVisibility(View.VISIBLE);
                    txtDateDevider.setVisibility(View.VISIBLE);
                }
            }
        });

        rdOneDayMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if( rdOneDayMode.isChecked() ) {

                    txtDateStart.setText( txtDateEnd.getText() );
                    showTask();
                }
            }
        });
    }

    private void loadCurrentDateforTxtDateEnd() {

        Calendar currentTime = Calendar.getInstance();

        txtDateEnd.setText( Utils.dateFormat.format( currentTime.getTime() ) );
        txtDateStart.setText( Utils.dateFormat.format( currentTime.getTime() ) );
    }

    private void slideToRemoveTask() {

        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                Task selectedTask = TaskList.get( viewHolder.getAdapterPosition() );

                Provider.deleteTask( selectedTask.getId() );

                MainActivity.TaskList.remove( viewHolder.getAdapterPosition() );

                MainActivity.taskAdapter.notifyItemRemoved( viewHolder.getAdapterPosition() );

                MainActivity.taskAdapter.notifyItemRangeChanged( viewHolder.getAdapterPosition(),TaskList.size() + 1 );
            }

        });

        swipeToDismissTouchHelper.attachToRecyclerView(rcvTask);
    }

    private void setEvent_fabButtonClick(){

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task newTask = (Task) data.getSerializableExtra("TASK");
        if(newTask!=null){
            taskProvider.insertTask(newTask);
            showTask();
        }
    }
}
