package com.abdel_fatah.mostafa.DoNotForgetGod;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.abdel_fatah.mostafa.remembergod.R;

import java.lang.reflect.Field;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int[] times={30000,60000,300000,600000,900000,1200000,1800000,3600000,7200000};
    String [] timesDurtion={"30 Seconds","1 Minutes","5 Minutes","10 Minutes","15 Minutes","20 Minutes","30 Minutes","60 Minutes"};
    String [] words={"كل الاذكار" , "لا اله الا الله","سبحان الله","الحمدلله","الله اكبر","استغفر الله"
            ,"سبحان الله وبحمده سبحان الله العظيم","صلي علي محمد"  ,"لا اله الا الله سبحانك اني كنت من الظالمين "
            ,"لا حول ولا قوة الا بالله","حسبي الله ونعم الوكيل"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //**************** Initizlied component ***************************************************

        final Switch off_on =(Switch) findViewById(R.id.switchButton);
        final NumberPicker pickers =(NumberPicker)findViewById(R.id.numberPicker1);
        pickers.setMinValue(0);
        pickers.setWrapSelectorWheel(true);
        pickers.setMaxValue(timesDurtion.length - 1);
        pickers.setDisplayedValues(timesDurtion);
        setNumberPickerTextColor(pickers);
        final NumberPicker pickers2 = (NumberPicker) findViewById(R.id.numberPicker2);
        pickers2.setMinValue(0);
        pickers2.setWrapSelectorWheel(true);
        pickers2.setMaxValue(words.length - 1);
        pickers2.setDisplayedValues(words);
        setNumberPickerTextColor(pickers2);

        //************************* start Serivce and set setting **********************************

        setData();
        final Intent intent =new Intent(this,MyService.class);
        startService(intent);

        //************************ timeDurtion picker action ****************************************

        pickers.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
             @Override
             public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                 pickers.setValue(newVal);
                 MyService.timeDurtion=times[newVal];
                 saveData();
             }
         } );
        //************************* azhar pinker action ********************************************

        pickers2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                pickers2.setValue(newVal);
                MyService.word=newVal;
                saveData();
            }
        } );


        //********************* Switch on/off action ************************************************
        off_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MyService.Run = true;
                    saveData();
                }else{
                    MyService.Run = false;
                    saveData();
                }
            }

        });

    }


    public void message(String error){
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "تطبيق لا تنسي ذكر الله");
            intent.putExtra(Intent.EXTRA_TEXT, "تطبيق لا تنسي ذكر الله يساعدك في تذكر بعض الاذكار صوتا"+
                    "\nhttps://play.google.com/store/apps/details?id=com.abdel_fatah.mostafa.DoNotForgetGod");
            startActivity(Intent.createChooser(intent, "Please Choose One ......."));
            return true;
        }else if (id == R.id.action_Valuable) {
            final String appPackageName = getPackageName();  // getPackageName() طلبنا اسم الباكيج الخاص للتطبيق من هذا التطبيق, لو أردت تقييم تطبيق اخر ضع اسم الباكيج الخاصة به
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            return true;
        }else if (id == R.id.action_more) {
            String developerName = "AndRody";  // your name in google play
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q="+developerName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q="+developerName)));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    //**********************************************************************************************

    public  boolean setNumberPickerTextColor(NumberPicker numberPicker)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(Color.WHITE);
                    ((EditText)child).setTextColor(Color.WHITE);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                }
                catch(IllegalAccessException e){
                }
                catch(IllegalArgumentException e){
                }
            }
        }
        return false;
    }

    public void saveData(){
        Switch switchbtn=(Switch)findViewById(R.id.switchButton);
        NumberPicker picker=(NumberPicker)findViewById(R.id.numberPicker1);
        NumberPicker picker2=(NumberPicker)findViewById(R.id.numberPicker2);
        SharedPreferences sharedPreferences=getSharedPreferences("Setting", MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("timeDurtion", picker.getValue());
        editor.putInt("word", picker2.getValue());
        editor.putBoolean("checked", switchbtn.isChecked());
        editor.commit();
        setData();
    }

    public void setData(){
        Switch switchbtn=(Switch)findViewById(R.id.switchButton);
        NumberPicker picker=(NumberPicker)findViewById(R.id.numberPicker1);
        NumberPicker picker2=(NumberPicker)findViewById(R.id.numberPicker2);
        SharedPreferences sharedPreferences=this.getSharedPreferences("Setting", MainActivity.this.MODE_PRIVATE);
        picker.setValue(sharedPreferences.getInt("timeDurtion", 0));
        picker2.setValue(sharedPreferences.getInt("word", 0));
        MyService.word=picker2.getValue();
        switchbtn.setChecked(sharedPreferences.getBoolean("checked", true));
        if (switchbtn.isChecked()) {
            MyService.Run = true;
        }else {
            MyService.Run = false;
        }
        MyService.timeDurtion=times[picker.getValue()];
    }
    public  void  play(){
        TelephonyManager telManager;
        telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final PhoneStateListener phoneListener = new PhoneStateListener() {
            MediaPlayer mp;
            boolean playing=true;
            int[] sound={R.raw.sound0,R.raw.sound1,R.raw.sound2,R.raw.sound3,R.raw.sound4,R.raw.sound5,R.raw.sound6
                    ,R.raw.sound7,R.raw.sound8,R.raw.sound9 };

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (MyService.word == 0) {
                    int rnd = new Random().nextInt(sound.length);
                    mp = MediaPlayer.create(MyService.context, sound[rnd]);
                } else {
                    mp = MediaPlayer.create(MyService.context, sound[MyService.word - 1]);
                }
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            playing=false;
                            break;
                        case TelephonyManager.CALL_STATE_IDLE: {
                            if ( playing)
                                mp.start();
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    mp.reset();
                                    mp.release();
                                };
                            });
                            break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        };
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        MyService.flag=true;
        // ***********************end****************************************************

    }

}
