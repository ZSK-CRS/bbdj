package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ClientManagerAdapter;
import com.mt.bbdj.community.adapter.WaitPrintAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientManagerActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private RelativeLayout icBack;
    private TextView clientAddressManager;
    private XRecyclerView  recyclerView;
    private ClientManagerAdapter mAdapter;

    private List<HashMap<String,String>> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_manager);
        initView();
        requestData();
    }

    private void requestData() {

    }

    private void initView() {
        icBack = findViewById(R.id.iv_back);
        clientAddressManager = findViewById(R.id.bt_commit);
        recyclerView = findViewById(R.id.rl_client_manager);
        initRecycler();
    }

    private void initRecycler() {
        mAdapter = new ClientManagerAdapter(this, mList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL,
                 Color.parseColor("#f4f4f4"), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        recyclerView.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        recyclerView.loadMoreComplete();
    }
}
