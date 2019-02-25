package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.model.PackageMessage;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.community.adapter.PackageMessageAdapter;

import java.util.ArrayList;

public class ShowPackageMessageActivity extends AppCompatActivity {

    private RelativeLayout ivBack;
    private RecyclerView rlMessage;
    private ImageView expressLogo;
    private String express_id;
    private String express;
    private TextView expressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_package_message);
        initParams();
        initView();
        initListView();
    }

    private void initParams() {
        Intent intent = getIntent();
        express_id = intent.getStringExtra("express_id");
        express = intent.getStringExtra("express");
    }

    private void initListView() {

        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        ExpressLogoDao expressLogoDao = daoSession.getExpressLogoDao();
        ExpressLogo express = expressLogoDao.queryBuilder().where(ExpressLogoDao.Properties.Express_id.eq(express_id)).list().get(0);

        Glide.with(this).load(express.getLogoLocalPath())
                .into(expressLogo);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;   //设置false 就是禁止RecyclerView滑动
            }
        };
        rlMessage.setLayoutManager(linearLayoutManager);

        //模拟数据集
        ArrayList<PackageMessage> logisticsBeans = new ArrayList<>();
        logisticsBeans.add(new PackageMessage("今天", "20:10", 1, "已签收", "[自提柜]已签收，签收人凭取货码签收。感谢使用CQ高朋花园蜂巢【自提柜】，期待再次为您服务。"));
        logisticsBeans.add(new PackageMessage("昨天", "10:05", 0, "待取件", "[自提柜]你的快件已被CQ高朋花园蜂巢【自提柜】代收，请及时取件。"));
        logisticsBeans.add(new PackageMessage("昨天", "09:09", 0, "派送中", "[重庆市]重庆机场公司派送"));
        logisticsBeans.add(new PackageMessage("01-09", "08:09", 0, "运输中", "【重庆机场公司】已收入"));
        logisticsBeans.add(new PackageMessage("01-08", "16:31", 0, "", "快件已到达【重庆机场公司】 扫描员是【石海波】"));
        logisticsBeans.add(new PackageMessage("01-07", "23:23", 0, "", "由【广东航空部】发往【重庆航空部】"));
        logisticsBeans.add(new PackageMessage("01-06", "16:23", 0, "已揽件", "【华强营业点】已收件"));

        PackageMessageAdapter adapter = new PackageMessageAdapter(this, logisticsBeans);
        rlMessage.setAdapter(adapter);
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        rlMessage = findViewById(R.id.rl_package_message);
        expressLogo = findViewById(R.id.iv_item_expressage_logo);
        expressMessage = findViewById(R.id.tv_item_expressage_name);
        expressMessage.setText(express+" 8455223233481121");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
