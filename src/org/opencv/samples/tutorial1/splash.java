package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by chihi.ong on 3/12/15.
 */
public class splash extends Activity{

    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mButton = (Button) findViewById(R.id.start);
        mButton.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Intent camera = new Intent(getApplicationContext(), challenge1.class);
                startActivity(camera);
                return false;
            }
        });

    }


}