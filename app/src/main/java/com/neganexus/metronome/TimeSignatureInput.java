package com.neganexus.metronome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.media.SoundPool;

import static java.lang.Integer.parseInt;


public class TimeSignatureInput extends Activity {

    private SoundPool play;
    private int click;
    private Handler timer;
    private Runnable clicker;
    private Switch switch1;
    private EditText editBPM;
    private EditText editBeats;
    private int bpm;
    private int beats;
    private int x;
    private static final String TAG = "TimeSignatureInput";



    boolean loaded = false;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_signature_input);

        switch1 = (Switch) findViewById(R.id.switch1);
        editBPM = (EditText) findViewById(R.id.editBPM);
        editBeats = (EditText) findViewById(R.id.editBeats);
        x = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        }

        else {
            createOldSoundPool();
        }



        timer = new Handler();

        click = play.load(context, R.raw.square, 1);

        play.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool play, int click, int status) {
                loaded = true;
            }
        });


        clicker = new Runnable() {
            @Override
            public void run() {
                if (loaded) {
                    if (x > 1 && x < beats) {
                        play.play(click, 1, 1, 1, 0, 1);
                        timer.postDelayed(this, 60000 / bpm);
                        x = x + 1;
                    }
                    else if (x == beats) {
                        play.play(click, 1, 1, 1, 0, 1);
                        timer.postDelayed(this, 60000 / bpm);
                        x = 1;
                    }
                    else {
                        play.play(click, 1, 1, 1, 0, 2);
                        timer.postDelayed(this, 60000 / bpm);
                        x = x + 1;
                    }

                }
            }
        };

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        bpm = parseInt(editBPM.getText().toString());
                        beats = parseInt(editBeats.getText().toString());
                        timer.postDelayed(clicker, 60000 / bpm);
                    }
                    catch (Exception e) {
                        errorBox(e.getMessage());
                    }

                }
                else {
                    timer.removeCallbacks(clicker);
                    x = 1;
                }
            }

        });

    }

    private void errorBox(String message) {
        switch1.setChecked(false);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle("Error");
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }



    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_signature_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        play = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }
    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        play = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
    }

}
