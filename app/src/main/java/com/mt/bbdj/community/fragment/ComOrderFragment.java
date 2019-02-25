package com.mt.bbdj.community.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;

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

        return view;
    }

    public static ComOrderFragment getInstance(){
        ComOrderFragment comOrderFragment = new ComOrderFragment();
        return comOrderFragment;
    }
}
