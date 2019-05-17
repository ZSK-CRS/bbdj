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
import com.mt.bbdj.community.activity.ClearOrderActivity;
import com.mt.bbdj.community.activity.HomemarkActivity;
import com.mt.bbdj.community.activity.SendManagerActivity;
import com.mt.bbdj.community.activity.WaterOrderActivity;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版订单
 */
public class ComOrderFragment extends BaseFragment implements View.OnClickListener {

    public static ComOrderFragment getInstance() {
        ComOrderFragment comOrderFragment = new ComOrderFragment();
        return comOrderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_com_order_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        LinearLayout ll_my_order = view.findViewById(R.id.ll_my_order);
        LinearLayout ll_clear_service = view.findViewById(R.id.ll_clear_service);
        LinearLayout ll_out_manager = view.findViewById(R.id.ll_out_manager);
        LinearLayout ll_water_service = view.findViewById(R.id.ll_water_service);
        LinearLayout ll_homemark_service = view.findViewById(R.id.ll_homemark_service);
        ll_my_order.setOnClickListener(this);
        ll_clear_service.setOnClickListener(this);
        ll_out_manager.setOnClickListener(this);
        ll_water_service.setOnClickListener(this);
        ll_homemark_service.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_my_order:    //我的寄件/订单
                handleOrder();
                break;
            case R.id.ll_out_manager:    //我的管理
                ToastUtil.showShort("功能暂未开放");
                break;
            case R.id.ll_water_service:     //桶装水服务
                searchWaterService();
                break;
            case R.id.ll_clear_service:   //干洗服务
                searchClearService();
                break;
            case R.id.ll_homemark_service:
                searchHomemarkService();     //上门维修服务
                break;
        }
    }

    private void searchHomemarkService() {
        Intent intent = new Intent(getActivity(), HomemarkActivity.class);
        startActivity(intent);
    }

    private void searchClearService() {
        Intent intent = new Intent(getActivity(), ClearOrderActivity.class);
        startActivity(intent);
    }

    private void searchWaterService() {
        Intent intent = new Intent(getActivity(), WaterOrderActivity.class);
        startActivity(intent);
    }

    private void handleOrder() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        startActivity(intent);
    }
}
