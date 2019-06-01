package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.mt.bbdj.baseconfig.view.RxActivityTool;


/**
 * @author vondear
 */
public class ActivityBase extends AppCompatActivity {

    public ActivityBase mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        DragAndDropPermissionsCompat
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
