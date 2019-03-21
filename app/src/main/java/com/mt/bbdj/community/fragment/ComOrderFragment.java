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
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.SendManagerActivity;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版订单
 */
public class ComOrderFragment extends BaseFragment implements View.OnClickListener {

    public static ComOrderFragment getInstance(){
        ComOrderFragment comOrderFragment = new ComOrderFragment();
        return comOrderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_com_order_fragment,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        LinearLayout ll_my_order = view.findViewById(R.id.ll_my_order);
        LinearLayout ll_my_service = view.findViewById(R.id.ll_my_service);
        LinearLayout ll_my_manager = view.findViewById(R.id.ll_my_manager);
        ll_my_order.setOnClickListener(this);
        ll_my_service.setOnClickListener(this);
        ll_my_manager.setOnClickListener(this);
    }


    @Override
    public void onClick(View view ){
        switch (view.getId()) {
            case R.id.ll_my_order:    //我的寄件/订单
                handleOrder();
                break;
            case R.id.ll_my_service:   //我的服务
                ToastUtil.showShort("功能暂未开放");
                break;
            case R.id.ll_my_manager:    //我的管理
                ToastUtil.showShort("功能暂未开放");
                break;
        }
    }

    private void handleOrder() {
        Intent intent = new Intent(getActivity(),SendManagerActivity.class);
        startActivity(intent);
    }
}
