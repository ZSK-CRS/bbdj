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
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.community.adapter.PackageMessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowPackageMessageActivity extends AppCompatActivity {

    private RelativeLayout ivBack;
    private RecyclerView rlMessage;
    private ImageView expressLogo;
    private String express_id;
    private String express;
    private TextView expressMessage;
    private String result;
    private String yundan;

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
        result = intent.getStringExtra("result");
        yundan = intent.getStringExtra("yundan");
    }

    private void initListView() {

        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        ExpressLogoDao expressLogoDao = daoSession.getExpressLogoDao();
        List<ExpressLogo> expressList = expressLogoDao.queryBuilder().where(ExpressLogoDao.Properties.Express_id.eq(express_id)).list();

        if (expressList.size() != 0) {
            ExpressLogo express = expressList.get(0);
            Glide.with(this).load(express.getLogoLocalPath())
                    .into(expressLogo);
        }

        rlMessage.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<PackageMessage> logisticsBeans = setData();
        PackageMessageAdapter adapter = new PackageMessageAdapter(this, logisticsBeans);
        rlMessage.setAdapter(adapter);
    }

    private ArrayList<PackageMessage> setData() {
        ArrayList<PackageMessage> logisticsBeans = new ArrayList<>();
        try {
            int sate = 0;
            JSONObject resultStr = new JSONObject(result);
            JSONObject dataObj = resultStr.getJSONObject("data");
            JSONArray data = dataObj.getJSONArray("data");
            for (int i = 0; i < data.length();i++) {
                JSONObject entity = data.getJSONObject(i);
                String content = entity.getString("content");
                String timeStramp = entity.getString("date");
                String title = entity.getString("states");
                String time = DateUtil.changeStampToStandrdTime("MM-dd HH:mm",timeStramp);
                String[] timeArray = time.split(" ");
                String month = timeArray[0];
                String hour = timeArray[1];
                PackageMessage packageMessage = new PackageMessage(month,hour,sate,title,content);
                logisticsBeans.add(packageMessage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return logisticsBeans;
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        rlMessage = findViewById(R.id.rl_package_message);
        expressLogo = findViewById(R.id.iv_item_expressage_logo);
        expressMessage = findViewById(R.id.tv_item_expressage_name);
        expressMessage.setText(express+" "+yundan);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
