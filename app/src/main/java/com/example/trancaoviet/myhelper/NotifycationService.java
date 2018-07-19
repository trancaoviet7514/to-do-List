package com.example.trancaoviet.myhelper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NotifycationService extends Service {

    public static ArrayList<Task> TaskList = new ArrayList<Task>();

    public NotifycationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(TaskList.size()==0)  NotifycationService.this.stopSelf();

                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Calendar currentTime = Calendar.getInstance();
                Date date = currentTime.getTime();
                Time time = new Time(date.getTime());

                for(int i = 0; i < TaskList.size();i++) {

                    if(TaskList.get(i).getDate().compareTo(date) < 0) {

                        if(TaskList.get(i).getTime().compareTo(time) < 0) {

                            Intent resultIntent = new Intent(NotifycationService.this, MainActivity.class);
                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            NotifycationService.this,
                                            0,
                                            resultIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                            Notification.Builder builder = new Notification.Builder(NotifycationService.this.getApplicationContext());
                            Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notyfication_sound_plucky);
                            builder.setSmallIcon(R.drawable.complete)
                                    .setContentTitle(TaskList.get(i).getContent())
                                    .setContentIntent(resultPendingIntent)
                                    .setSound(defaultSoundUri);

                            mNotifyMgr.notify(001,builder.build());
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= 26) {
                                //v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                v.vibrate(500);
                            }
                            TaskList.remove(i);

                            //rung va chuong
                        }
                    }
                }
            }
        },5000);

        return START_CONTINUATION_MASK;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

}
