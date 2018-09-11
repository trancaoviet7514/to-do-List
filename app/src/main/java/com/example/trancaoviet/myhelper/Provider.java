package com.example.trancaoviet.myhelper;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Provider {

    public static DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Task> listAllTask = new ArrayList<>();
    private int maxID;
    private boolean isSignIn = false;
    public static String UserName, Password;

    public Provider() {

        if(!isSignIn){
            UserName = "UN_SIGN_IN";
        }

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ( dataSnapshot == null ) return;
                maxID = ((Long) dataSnapshot.getValue()).intValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDataBase.child("User").child(UserName).child("MaxID").addValueEventListener(valueEventListener);
        //mDataBase.child("User").child(UserName).child("TaskList").addValueEventListener(valueEventListener_TaskList);
    }

    public void getAllTask(final TaskChangeCallBack taskChangeCallBack){

        final ArrayList<Task> result = new ArrayList<>();

            ValueEventListener myValueEventListener = new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ( dataSnapshot == null ) return;
                    //result.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        DBTask dbTask = child.getValue(DBTask.class);
                        Task task = new Task(dbTask);
                        result.add(task);
                    }
                    taskChangeCallBack.onFinish(result);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mDataBase.child("User").child(UserName).child("TaskList").addListenerForSingleValueEvent(myValueEventListener);

    }

    public ArrayList<Task> getTaskList(Date dateStart, Date dateEnd) {

        ArrayList<Task> result =  new ArrayList<>();

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

    public void getTaskList (Date dateStart, Date dateEnd, boolean isComplete) {

        ArrayList<Task> result = new ArrayList<>();


        Task task;

        for(int i = 0; i < result.size(); i++) {

            task = result.get(i);

            if ( task.getDate().compareTo(dateStart) < 0 || task.getDate().compareTo(dateEnd) > 0 ) {
                result.remove(i);
                i--;
            }
        }


    }

    public void insertTask(Task task) {

        task.setId(maxID);

        DBTask dbTask = new DBTask ( task );
        mDataBase.child("User").child(UserName).child("TaskList").child ( String.valueOf ( maxID ) ).setValue ( dbTask );

        mDataBase.child("User").child(UserName).child("MaxID").setValue(new Integer ( ++maxID ) );

    }

    public boolean updateTask(int id, Task task) {

        DBTask dbTask = new DBTask(task);
        mDataBase.child("User").child(UserName).child("TaskList").child( String.valueOf(id) ).setValue(dbTask);

        return true;

    }

    public boolean deleteTask(int id) {

        mDataBase.child("User").child(UserName).child("TaskList").child( String.valueOf(id) ).removeValue();

        return true;

    }

    interface TaskChangeCallBack {
        void onFinish(ArrayList<Task> listTask);
    }
}

