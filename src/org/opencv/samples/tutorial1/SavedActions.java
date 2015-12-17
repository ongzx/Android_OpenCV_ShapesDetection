package org.opencv.samples.tutorial1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chihi.ong on 28/11/15.
 */
public class SavedActions extends Activity {

    private TextView mTextView;
    private Button mPlayBtn;
    private Button mBackBtn;
    private Button mClearBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_action_layout);


        mTextView = (TextView) findViewById(R.id.textView);
        mPlayBtn = (Button) findViewById(R.id.play);
        mBackBtn = (Button) findViewById(R.id.back);
        mClearBtn = (Button) findViewById(R.id.clear);

        ArrayList<String> list = ActionList.getInstance().getData();

        if (!list.isEmpty()) {

        } else {
            //mPlayBtn.setVisibility(SurfaceView.INVISIBLE);
            mClearBtn.setVisibility(SurfaceView.INVISIBLE);
        }

        mPlayBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent action = new Intent(v.getContext(), ActionAnimation.class);
                startActivity(action);
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionList.clearData();
                finish();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ActionArrayAdapter adapter = new ActionArrayAdapter(this,
                R.layout.list_item, list);

        DynamicListView listView = (DynamicListView) findViewById(R.id.listView);


        listView.setCheeseList(list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//
//            }
//
//        });
    }

    public class ActionArrayAdapter extends ArrayAdapter<String> {

        final int INVALID_ID = -1;

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public ActionArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= mIdMap.size()) {
                return INVALID_ID;
            }
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}