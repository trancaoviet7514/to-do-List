package com.example.trancaoviet.myhelper;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private List<Task> TaskList;
    private Context mContext;

    public TaskAdapter(View itemView) {

    }

    public TaskAdapter(List<Task> taskList, Context context) {
        super();
        this.TaskList = taskList;
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TaskTitle, TaskDate;
        public ImageButton btnComplete, btnDelete, btnNotyfication;
        public Task selectedTask;
        int position = 0;

        public MyViewHolder(View view) {
            super(view);
            TaskTitle = (TextView) view.findViewById(R.id.txtTaskTitle);
            TaskDate = (TextView) view.findViewById(R.id.txtDate);
            btnComplete = (ImageButton) view.findViewById(R.id.btnComplete);
            btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            btnNotyfication = (ImageButton) view.findViewById(R.id.btnNotyficationInItem);


            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Id",selectedTask.getId());
                    contentValues.put("Title",selectedTask.getTitle());
                    contentValues.put("Date",selectedTask.getDate());
                    contentValues.put("Time",selectedTask.getTime());
                    contentValues.put("Notifycation",selectedTask.getNotifycation());

                    if(selectedTask.Complete){
                        btnComplete.setImageResource(R.drawable.uncomplete);
                        contentValues.put("Complete",0);
                        int i = MainActivity.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
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
                        int i = MainActivity.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
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
                    MainActivity.database.delete("tbTask","id = ?",new String[]{String.valueOf(selectedTask.getId())});
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
                    contentValues.put("Title",selectedTask.getTitle());
                    contentValues.put("Date",selectedTask.getDate());
                    contentValues.put("Time",selectedTask.getTime());
                    contentValues.put("Complete",String.valueOf(selectedTask.isComplete()));

                    if(selectedTask.getNotifycation().equals("False")){
                        btnNotyfication.setImageResource(R.drawable.notification);
                        contentValues.put("Notifycation","True");
                        int i = MainActivity.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setNotifycation("True");
                    }
                    else{
                        btnNotyfication.setImageResource(R.drawable.unnotification);
                        contentValues.put("Notifycation","False");
                        int i = MainActivity.database.update("tbTask",contentValues,"id = ?", new String[]{String.valueOf(selectedTask.getId())});
                        if(i!= 0){
                            Toast.makeText(mContext,"Update successful",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext,"Update fault",Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.TaskList.get(position).setNotifycation("False");
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

        holder.TaskTitle.setText(task.getTitle());
        holder.TaskDate.setText(task.getDate());

        if(task.isComplete()){
            holder.btnComplete.setImageResource(R.drawable.complete);
        }
        else{
            holder.btnComplete.setImageResource(R.drawable.uncomplete);
        }
        if(task.getNotifycation().equals("False")){
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
