package com.mt.bbdj.baseconfig.utls;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.mt.bbdj.R;


public class HkDialogLoading extends Dialog {
    Context context;

    private MyFragmentDialog dialog;

    private AlertDialog.Builder alertBuilder;

    private AlertDialog alertDialog;

    public HkDialogLoading(Context context) {
        super(context, R.style.HKDialogLoading);
        this.context = context;
        View view = getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.fragment_dialog_message);
        textView.setText("正在加载中...");
        alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(view);
        alertBuilder.setOnKeyListener(keylistener);
        alertBuilder.setCancelable(false);
        alertDialog = alertBuilder.create();
    }

    OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                return true;
            } else {
                return false;
            }
        }
    };

    public HkDialogLoading(Context context, String message) {
        super(context, R.style.HKDialogLoading);
        this.context = context;
        View view = getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.fragment_dialog_message);
        textView.setText(message);
        alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(view);
        alertBuilder.setOnKeyListener(keylistener);
        alertBuilder.setCancelable(false);
        alertDialog = alertBuilder.create();
    }


    @Override
    public void cancel() {
        super.cancel();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        if (alertDialog != null) {
            alertDialog.show();
        }
    }
} 