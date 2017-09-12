package com.abdel_fatah.mostafa.DoNotForgetGod;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.abdel_fatah.mostafa.remembergod.R;

import java.util.Random;

public class MyService extends Service {

    TelephonyManager telManager;
    MediaPlayer mp;
    static Context context;
    static boolean flag;
    static boolean Run;
    static int word = 0;
    static boolean allow=true;
    static int timeDurtion = 30000;
    Handler handler;
    Runnable runnable;
    int[] times={30000,60000,300000,600000,900000,1200000,1800000,3600000,7200000};

    public static void Message(String mesaage) {
        Toast.makeText(context.getApplicationContext(), mesaage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        flag = true;
        context = this.getApplicationContext();
        Intent ishintent = new Intent(this, MyService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, ishintent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pintent);
        handler = new Handler();
        super.onCreate();
    }

    public void setData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting", context.MODE_PRIVATE);
        int value = sharedPreferences.getInt("timeDurtion", 0);
        MyService.word = sharedPreferences.getInt("word", 0);
        boolean checked = sharedPreferences.getBoolean("checked", true);
        if (checked) {
            MyService.Run = true;
        } else {
            MyService.Run = false;
        }
        timeDurtion=times[value];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setData();
        if (Run && flag) {
            flag = false;
            runnable = new Runnable() {
                TelephonyManager telManager;
                MediaPlayer mp;
                int[] sound = {R.raw.sound0, R.raw.sound1, R.raw.sound2, R.raw.sound3, R.raw.sound4, R.raw.sound5, R.raw.sound6
                        , R.raw.sound7, R.raw.sound8, R.raw.sound9};
                boolean play = true;
                public void run() {
                    //***********************Start***************************************************
                    if (MyService.word == 0) {
                        int rnd = new Random().nextInt(sound.length);
                        mp = MediaPlayer.create(MyService.context, sound[rnd]);
                    } else {
                        mp = MediaPlayer.create(MyService.context, sound[MyService.word - 1]);
                    }
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    final PhoneStateListener phoneListener = new PhoneStateListener() {
                        @Override
                        public void onCallStateChanged(int state, String incomingNumber) {
                            try {
                                switch (state) {
                                    case TelephonyManager.CALL_STATE_OFFHOOK:
                                    case TelephonyManager.CALL_STATE_RINGING:
                                        play = false;
                                        break;
                                    case TelephonyManager.CALL_STATE_IDLE: {
                                        if (play)
                                            mp.start();
                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            public void onCompletion(MediaPlayer mp) {
                                                mp.reset();
                                                mp.release();
                                            }

                                            ;
                                        });
                                        break;
                                    }
                                }
                            } catch (Exception ex) {
                                Message("Message : " + ex.getMessage());
                            }
                        }
                    };
                    telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    MyService.flag = true;
                    // ***********************end****************************************************
                }
            };
            handler.postDelayed(runnable,timeDurtion);

        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
