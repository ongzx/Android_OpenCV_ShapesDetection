package org.opencv.samples.tutorial1;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chihi.ong on 29/11/15.
 */
public class ActionAnimation extends Activity {

    private ImageView mImageView;
    private boolean isCorrect = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_animate_layout);

        mImageView = (ImageView) findViewById(R.id.imageView);

        ArrayList<String> list = ActionList.getInstance().getData();
//
//        ActionList.setData("Square");
//        ActionList.setData("Pentagon");
//        ActionList.setData("Square");

        AnimationSet set = new AnimationSet(true);

        String message = "";

        if (list.size() == 3) {
            if (list.get(0).equals("Move Down") &&
                    list.get(1).equals("Move Right") &&
                    list.get(2).equals("Move Down"))
            {
                isCorrect = true;
            }
            else
            {
                isCorrect = false;
            }
        } else {
            isCorrect = false;
        }

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        int offset;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                offset = 700;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                offset = 490;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                offset = 190;
                break;
            default:
                offset = 190;
        }

        for (int i = 0; i < list.size(); i++) {


            if (list.get(i).equals("Move Up")) {
                message = "Moving up...";
                set.addAnimation(new TranslateAnimation(0, 0, mImageView.getY(), mImageView.getY() - offset));
            } else if (list.get(i).equals("Move Down")) {
                message = "Moving down...";
                set.addAnimation(new TranslateAnimation(0, 0, mImageView.getY(), mImageView.getY() + offset));
            } else if (list.get(i).equals("Move Left")) {
                message = "Moving left...";
                set.addAnimation(new TranslateAnimation(mImageView.getX(), mImageView.getX() - (offset+50), 0, 0));
            } else if (list.get(i).equals("Move Right")) {
                message = "Moving right...";
                set.addAnimation(new TranslateAnimation(mImageView.getX(), mImageView.getX() + (offset+50), 0, 0));
            }


//            final String finalMessage = message;
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                            finalMessage,
//                            Toast.LENGTH_SHORT).show();
//                }
//            }, 1000);

            set.getAnimations().get(i).setDuration(1000);
            set.getAnimations().get(i).setStartOffset(1000 * i);


        }


        set.setFillAfter(true);
        set.start();


        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //Functionality here

                if (isCorrect) {
                    ActionList.clearData();
                    Intent success = new Intent(getApplicationContext(), success.class);
                    startActivity(success);
                } else {
                    ActionList.clearData();
                    Intent success = new Intent(getApplicationContext(), failure.class);
                    startActivity(success);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mImageView.startAnimation(set);



    }
}