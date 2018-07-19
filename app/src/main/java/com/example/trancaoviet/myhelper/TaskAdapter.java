package com.example.trancaoviet.myhelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private ArrayList<Task> TaskList;
    private Context mContext;

    public TaskAdapter(View itemView) {

    }

    public TaskAdapter(ArrayList<Task> taskList, Context context) {
        super();
        this.TaskList = taskList;
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TaskContent, TaskDate, TaskTime;
        public ImageButton btnComplete, btnDelete, btnNotyfication;
        public Task selectedTask;
        int position = 0;

        public MyViewHolder(View view) {
            super(view);
            TaskContent = (TextView) view.findViewById(R.id.txtTaskContent);
            TaskDate = (TextView) view.findViewById(R.id.txtDate);
            TaskTime = (TextView) view.findViewById(R.id.txtTime);
            btnComplete = (ImageButton) view.findViewById(R.id.btnComplete);
            btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            btnNotyfication = (ImageButton) view.findViewById(R.id.btnNotyficationInItem);



            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Id",selectedTask.getId());
                    contentValues.put("Date",selectedTask.getDate().toString());
                    contentValues.put("Time",selectedTask.getTime().toString());
                    contentValues.put("Notifycation",selectedTask.isHasNotifycation());

                    if(selectedTask.isComplete()){
                        btnComplete.setImageResource(R.drawable.uncomplete);
                        contentValues.put("Complete",0);
                        int i = Provider.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setComplete(false);
                    }
                    else{
                        btnComplete.setImageResource(R.drawable.complete);
                        contentValues.put("Complete",1);
                        int i = Provider.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setComplete(true);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Provider.database.delete("tbTask","id = ?",new String[]{String.valueOf(selectedTask.getId())});
                    TaskAdapter.this.notifyItemRemoved(position);
                    MainActivity.TaskList.remove(position);
                    TaskAdapter.this.notifyItemRangeChanged(position,TaskList.size()+1);
                }
            });
            btnNotyfication.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Id",selectedTask.getId());
                    contentValues.put("Date",selectedTask.getDate().toString());
                    contentValues.put("Time",selectedTask.getTime().toString());
                    contentValues.put("Complete",String.valueOf(selectedTask.isComplete()));

                    if(selectedTask.isHasNotifycation()){
                        btnNotyfication.setImageResource(R.drawable.notification);
                        contentValues.put("Notifycation","true");
                        int i = Provider.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            //Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setHasNotifycation(true);
                        NotifycationService.TaskList.add(selectedTask);
                        Intent intent = new Intent(mContext,NotifycationService.class);
                        mContext.startService(intent);
                    }
                    else{
                        btnNotyfication.setImageResource(R.drawable.unnotification);
                        contentValues.put("Notifycation","false");
                        int i = Provider.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            //Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setHasNotifycation(false);
                        NotifycationService.TaskList.remove(selectedTask);
                    }
                }
            });
        }


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Task task = TaskList.get(position);
        holder.selectedTask = task;
        holder.position = position;
        holder.TaskDate.setText(task.getDate().toString());
        holder.TaskTime.setText(task.getTime().toString());
        holder.TaskContent.setText(task.getContent());

        if(task.isComplete()){
            holder.btnComplete.setImageResource(R.drawable.complete);
        }
        else{
            holder.btnComplete.setImageResource(R.drawable.uncomplete);
        }
        if(task.isHasNotifycation()){
            holder.btnNotyfication.setImageResource(R.drawable.unnotification);
        }
        else{
            holder.btnNotyfication.setImageResource(R.drawable.notification);
        }
    }
    @Override
    public int getItemCount() {
        return TaskList.size();
    }
}
