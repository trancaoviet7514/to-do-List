package com.example.trancaoviet.myhelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    TextView btnDate, btnTime, btnCancel, btnSave;
    EditText edtTaskContent;
    Switch btnNotifycation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        btnDate = (TextView) findViewById(R.id.btnDate);
        btnTime = (TextView) findViewById(R.id.btnTime);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        btnSave = (TextView) findViewById(R.id.btnSave);

        edtTaskContent = (EditText) findViewById(R.id.edtTaskContent);
        final Switch btnNotifycation = (Switch) findViewById(R.id.btnNotifycation);

        //load currentTime
        btnDate.setText(Utils.dateFormat.format(Calendar.getInstance().getTime()));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(101,getIntent());
                finish();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {

            Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task

            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {

                        dateSelected.set(0,0,0, selectedHour, selectedMinute);
                        btnTime.setText( Utils.timeFormat.format( dateSelected.getTime() ) );

                    }
                }, hour, minute, true);

                mTimePicker.setTitle("Pick Time");
                mTimePicker.show();
            }

        });

        btnDate.setOnClickListener(new View.OnClickListener() {

            Calendar dateSelected = Calendar.getInstance(); // use for storge data seleted in dialog add task

            @Override
            public void onClick(View view) {

                Calendar newCalendar = Calendar.getInstance();

                final DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                        btnDate.setText(Utils.dateFormat.format(dateSelected.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();

            }

        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String TaskContent = edtTaskContent.getText().toString();
                if( TaskContent.equals("") )  TaskContent = "[Không có nội dung]";

                Date date = null;
                Time time = null;
                try {
                    date = Utils.dateFormat.parse(btnDate.getText().toString());

                    Date date_time = Utils.timeFormat.parse(btnTime.getText().toString());
                    time= new Time(date_time.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                boolean hasNotifycation = btnNotifycation.isChecked();

                Task newTask = new Task(TaskContent, date, time, false, hasNotifycation);

                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TASK",newTask);
                intent.putExtras(bundle);
                setResult(101,intent);
                finish();

                if( newTask.isHasNotifycation() ) {
                    NotifycationService.TaskList.add(newTask);
                    Intent notifycationIntent = new Intent(AddTaskActivity.this,NotifycationService.class);
                    startService(notifycationIntent);
                }

            }
        });
    }
}
