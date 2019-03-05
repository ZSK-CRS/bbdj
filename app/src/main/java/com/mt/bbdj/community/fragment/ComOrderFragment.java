package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.community.activity.SendManagerActivity;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版订单
 */
public class ComOrderFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_com_order_fragment,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        LinearLayout ll_my_order = view.findViewById(R.id.ll_my_order);
        ll_my_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SendManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    public static ComOrderFragment getInstance(){
        ComOrderFragment comOrderFragment = new ComOrderFragment();
        return comOrderFragment;
    }
}
