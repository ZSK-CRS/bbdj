package com.mt.bbdj.baseconfig.utls;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mt.bbdj.R;


/**
 * @author ZSK
 * @date 2018/5/23
 * @function
 */

public class MyFragmentDialog extends DialogFragment {

    private TextView messageText;

    public static MyFragmentDialog getInstance(String message) {
        MyFragmentDialog fragmentDialog = new MyFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message",message);
        fragmentDialog.setArguments(bundle);
        return fragmentDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message","请稍后...");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getActivity().getLayoutInflater();
        View view  = inflate.inflate(R.layout.fragment_dialog,null);
        messageText = (TextView) view.findViewById(R.id.fragment_dialog_message);
        messageText.setText(message);
        builder.setView(view);
        builder.setCancelable(false);
        this.setCancelable(false);
        return builder.create();
    }
}
