package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.EncodingUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrintPreviewActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回
    @BindView(R.id.iv_code)
    ImageView ivCode;        //条形码
    @BindView(R.id.tv_code_number)
    TextView tvCodeNumber;    //条形码数值
    @BindView(R.id.bt_again_old)
    Button btAgainOld;    //原单重打
    @BindView(R.id.bt_again_new)
    Button btAgainNew;    //再打一单
    @BindView(R.id.tv_region)
    TextView tvRegion;     //地区
    @BindView(R.id.tv_collect_name)
    TextView tvCollectName;
    @BindView(R.id.tv_collect_phone)
    TextView tvCollectPhone;
    @BindView(R.id.tv_collect_address)
    TextView tvCollectAddress;
    @BindView(R.id.tv_receive_name)
    TextView tvReceiveName;
    @BindView(R.id.tv_receive_phone)
    TextView tvReceivePhone;
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @BindView(R.id.tv_goos_name)
    TextView tvGoosName;
    @BindView(R.id.tv_good_weigth)
    TextView tvGoodWeigth;
    @BindView(R.id.tv_gost_money)
    TextView tvGostMoney;
    @BindView(R.id.tv_mark)
    TextView tvMark;
    @BindView(R.id.tv_enter_time)
    TextView tvEnterTime;    //录单时间
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_tag_number)
    TextView tvTagNumber;     //三段码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        //条形码
        Bitmap codeBitmap = EncodingUtil.createBarcode(tvCodeNumber.getText().toString(), 700, 150, false);
        ivCode.setImageBitmap(codeBitmap);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        //销毁
        if (targetEvent.getTarget() == TargetEvent.DESTORY) {
            finish();
        }
    }

    private void initView() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreference();
        String yundanhao = sharedPreferences.getString("yundanhao", "");    //运单号
        String transit = sharedPreferences.getString("transit", "");    //时间
        String place = sharedPreferences.getString("place", "");    //地区
        String collect_name = sharedPreferences.getString("collect_name", "");    //收件人联系人
        String collect_phone = sharedPreferences.getString("collect_phone", "");    //收件人联系电话
        String collect_address = sharedPreferences.getString("collect_region", "")
                + sharedPreferences.getString("collect_address", "");    // 收件人地址

        String send_name = sharedPreferences.getString("send_name", "");    //寄件联系人
        String send_phone = sharedPreferences.getString("send_phone", "");    //寄件联系人
        String send_address = sharedPreferences.getString("send_region", "")
                + sharedPreferences.getString("send_address", "");    // 寄件人地址

        String goods_name = sharedPreferences.getString("goods_name", "");   //物品名称
        String weight = sharedPreferences.getString("weight", "");    //计费重量
        String content = sharedPreferences.getString("content", "");    //备注
        String money = sharedPreferences.getString("money", "");    //运费
        String code = sharedPreferences.getString("code", "");    //三段码


        String codeStr = StringUtil.changeStringFormat(yundanhao, 4, "   ");
        String[] dateStr = transit.split("  ");

        tvEnterTime.setText(dateStr.length>1?dateStr[1]:dateStr[0]);
        tvCodeNumber.setText(codeStr);
        tvTagNumber.setText(code);
        tvRegion.setText(place);
        tvCollectName.setText(collect_name);
        tvCollectPhone.setText(collect_phone);
        tvCollectAddress.setText(collect_address);
        tvReceiveName.setText(send_name);
        tvReceivePhone.setText(send_phone);
        tvReceiveAddress.setText(send_address);
        tvGoosName.setText(goods_name);
        tvGoodWeigth.setText(weight);
        tvGostMoney.setText(money+"元");
        tvMark.setText(content);
        tvDate.setText(transit);

    }


    @OnClick({R.id.iv_back, R.id.bt_again_old, R.id.bt_again_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_again_old:
                Intent intent = new Intent(this,BluetoothSearchAgainActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_again_new:
                printAgainNew();
                break;
        }
    }

    private void printAgainNew() {
        Intent intent = new Intent(this, RecordSheetDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
