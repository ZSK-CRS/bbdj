package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiveExpressActivity extends BaseActivity {
    @BindView(R.id.tv_receive_address)
    AppCompatTextView tvReceiveAddress;
    @BindView(R.id.tv_receive_person)
    TextView tvReceivePerson;
    @BindView(R.id.tv_send_address)
    AppCompatTextView tvSendAddress;
    @BindView(R.id.tv_send_message)
    TextView tvSendMessage;
    @BindView(R.id.tv_dingdan)
    TextView tvDingdan;
    @BindView(R.id.tv_goods_type)
    TextView tvGoodsType;
    @BindView(R.id.tv_goods_weight)
    TextView tvGoodsWeight;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.tv_kuaididanhao)
    TextView tvKuaididanhao;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.bt_confirm)
    Button btConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_express);
        ButterKnife.bind(this);
        initParams();
    }

    private void initParams() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreference();
        String collect_region = sharedPreferences.getString("collect_region","");
        String collect_address = sharedPreferences.getString("collect_address","");
        String collect_name = sharedPreferences.getString("collect_name","");
        String collect_phone = sharedPreferences.getString("collect_phone","");

        String send_region = sharedPreferences.getString("send_region","");
        String send_address = sharedPreferences.getString("send_address","");
        String send_name = sharedPreferences.getString("send_name","");
        String send_phone = sharedPreferences.getString("send_phone","");

        String mail_id = sharedPreferences.getString("mail_id","");    //订单id
        String dingdanhao = sharedPreferences.getString("dingdanhao","");    //订单号
        String goods_name = sharedPreferences.getString("goods_name","");
        String weight = sharedPreferences.getString("weight","");
        String yundanhao = sharedPreferences.getString("yundanhao","");    //运单号
        String money = sharedPreferences.getString("money","");
        String express_name = sharedPreferences.getString("express_name","");

        tvReceiveAddress.setText(collect_region+collect_address);
        tvReceivePerson.setText(collect_name+"  "+collect_phone);
        tvSendAddress.setText(send_region+"  "+send_address);
        tvSendMessage.setText(send_name+"  "+send_phone);
        tvDingdan.setText(dingdanhao);
        tvGoodsType.setText(goods_name);
        tvGoodsWeight.setText(weight);
        tvExpressName.setText(express_name);
        tvKuaididanhao.setText(yundanhao);
        tv_money.setText(money+"元");
    }

    @OnClick(R.id.bt_confirm)
    public void onViewClicked(View view) {
        Intent intent = new Intent(ReceiveExpressActivity.this,BluetoothSearchActivity.class);
        startActivity(intent);
    }
}
