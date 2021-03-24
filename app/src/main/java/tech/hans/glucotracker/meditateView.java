package tech.hans.glucotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class meditateView extends AppCompatActivity {

    private MediaPlayer mp = null;
    private TextView nowPlaying,instucts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditate_view);
        nowPlaying = (TextView) findViewById(R.id.nowPlaying);
        instucts = (TextView) findViewById(R.id.instructs);
    }

    public void playSound(View v){
        if(v.getId() == R.id.ayp){
            if(mp != null){
                mp.stop();
            }
            mp = MediaPlayer.create(this,getResources().getIdentifier("amlayogapadhi", "raw", getPackageName()));
            mp.start();
            mp.setLooping(true);
            nowPlaying.setText("Now Playing - AMLA YOGA PADHI\nIt will Play till the time you want to mediatate, Stop when done with meditation");
            instucts.setText("Instructions:\nSpine Erect and Concentrate on Breathing In and Breathing Out with your Eyes Closed");
        }else if(v.getId() == R.id.wholeness){
            if(mp != null){
                mp.stop();
            }
            mp = MediaPlayer.create(this,getResources().getIdentifier("wholeness", "raw", getPackageName()));
            mp.start();
            mp.setLooping(true);
            nowPlaying.setText("Now Playing - WHOLENESS\nIt will Play till the time you want to mediatate, Stop when done with meditation");
            instucts.setText("INSTRUCTIONS:\nSpine Erect and Concentrate on Breathing In and Breathing Out with your Eyes Closed");
        }else if(v.getId() == R.id.stopper){
            if(mp != null){
                mp.stop();
                mp = null;
                nowPlaying.setText("");
                instucts.setText("");
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "No Music is playing Currently", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mp != null){
            mp.stop();
            mp = null;
            nowPlaying.setText("");
            instucts.setText("");
        }
        finish();
    }
}