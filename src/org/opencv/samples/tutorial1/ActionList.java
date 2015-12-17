package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chihi.ong on 28/11/15.
 */
public class ActionList
{
    private String[] data = new String[] { "Android", "iPhone", "WindowsMobile"};

    private static final ArrayList<String> list = new ArrayList<String>();

    public ArrayList<String> getData() {return list;}

    public ArrayList<String> reverseData() {
        for(int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    public static void setData(String message) {

        String action = "";

        if (message == "Pentagon") {
            action = "Move Right";
        } else if (message == "Square") {
            action = "Move Down";
        } else if (message == "Triangle") {
            action = "Move Up";
        } else if (message == "Circle") {
            action = "Move Left";
        }
        list.add(action);

    }

    public static void clearData() {
        list.clear();
    }

    private static final ActionList holder = new ActionList();

    public static ActionList getInstance() {return holder;}


}