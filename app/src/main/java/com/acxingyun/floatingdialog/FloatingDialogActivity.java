package com.acxingyun.floatingdialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingDialogActivity extends AppCompatActivity {
    private LinearLayout a;
    private WindowManager mWm;
    private float mStartY;
    private long mDownTime, mUpTime;
    private WindowManager.LayoutParams localLayoutParams;
    private static final String CLASS_TAG = "FloatingDialogActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_dialog);
    }

    public void openFloatingDialog(View v){
        Log.i(CLASS_TAG, "openFloatingDialog called...");
        if (Build.VERSION.SDK_INT < 23){
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void showDialogAndIntent(View v){
        Log.i(CLASS_TAG, "showDialogAndIntent called...");
        showDialog();
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.HWSettings");
//        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
//                .setData(Uri.fromParts("package",
//                        getApplicationContext().getPackageName(), null));
        startActivity(intent);
    }

    public void showDialog(){
        localLayoutParams = new WindowManager.LayoutParams();
        mWm= (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        mWm.getDefaultDisplay().getMetrics(localDisplayMetrics);
        int i1 = localDisplayMetrics.widthPixels;
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        if(Build.VERSION.SDK_INT > 24) {
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        localLayoutParams.format = 1;
        localLayoutParams.flags = 40;
        localLayoutParams.gravity = 81;
        localLayoutParams.x = 0;
        localLayoutParams.y = 60;
        localLayoutParams.width = (i1 - 90);
        localLayoutParams.height = -2;
        a = ((LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_dialog, null));
        mWm.addView(a, localLayoutParams);
        ((TextView)a.findViewById(R.id.permission_tip)).setText("弹出窗口");
        ImageView mDialogCancel = a.findViewById(R.id.dialog_cancel);
        mDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDialog();
            }
        });
        a.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartY = event.getRawY();
                        Log.i(CLASS_TAG, "ACTION_DOWN,mStartY:" + mStartY);
                        Log.i(CLASS_TAG, "localLayoutParams.y:" + localLayoutParams.y);
                        mDownTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i(CLASS_TAG, "ACTION_MOVE,event.getRawY():" + event.getRawY());
                        localLayoutParams.y += mStartY-event.getRawY();
                        Log.i(CLASS_TAG, "localLayoutParams.y:" + localLayoutParams.y);
                        mWm.updateViewLayout(a, localLayoutParams);
                        mStartY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mUpTime = System.currentTimeMillis();
                        return mUpTime-mDownTime>200;
                }
                return false;
            }
        });
    }

    private void removeDialog() {
        if(mWm !=null && a!=null) {
            mWm.removeView(a);
            a = null;
            mWm = null;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        removeDialog();
    }
}
