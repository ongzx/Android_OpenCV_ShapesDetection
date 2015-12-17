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
public class challenge1 extends Activity {

    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge1);

        mButton = (Button) findViewById(R.id.scan);
        mButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent camera = new Intent(getApplicationContext(), Tutorial1Activity.class);
                startActivity(camera);
                return false;
            }
        });
    }
}