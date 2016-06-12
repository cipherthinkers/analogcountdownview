package com.cipherthinkers.analogcountdownviewsample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cipherthinkers.analogcountdownview.AnalogCountdownView;

public class SampleActivity extends AppCompatActivity {

    private AnalogCountdownView mAnalogCountdownView;
    private AnalogCountdownView.AnalogTimerUpdateListener mAnalogTimerUpdateListener;
    private FloatingActionButton mFab;
    private Context mContext;
    private boolean isPaused = true;
    private boolean isExpired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPaused){
                    isPaused = true;
                    mFab.setImageDrawable(ActivityCompat.getDrawable(mContext, R.mipmap.ic_play));
                    mAnalogCountdownView.stop();
                }else{
                    isPaused = false;
                    mFab.setImageDrawable(ActivityCompat.getDrawable(mContext, R.mipmap.ic_pause));
                    if(isExpired) {
                        isExpired = false;
                        mAnalogCountdownView.resetAndStart(mAnalogTimerUpdateListener);
                    }else{
                        mAnalogCountdownView.start(mAnalogTimerUpdateListener);
                    }
                }
            }
        });

        mAnalogCountdownView = (AnalogCountdownView)findViewById(R.id.analogcountdownview);
        mAnalogTimerUpdateListener = new AnalogCountdownView.AnalogTimerUpdateListener() {
            @Override
            public void onTimerUpdated(int currentValue, int totalValue) {

            }

            @Override
            public void onTimerStarted() {
                isPaused = false;
            }

            @Override
            public void onTimerExpired() {
                isPaused = true;
                isExpired = true;
                mFab.setImageDrawable(ActivityCompat.getDrawable(mContext, R.mipmap.ic_restart));
            }
        };
//        mAnalogCountdownView.start(mAnalogTimerUpdateListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
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
}
