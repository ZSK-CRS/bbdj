package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConsumeDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.rl_ic)
    RelativeLayout rlIc;
    @BindView(R.id.tv_title_send)
    TextView tvTitleSend;
    @BindView(R.id.tv_send_name)
    TextView tvSendName;
    @BindView(R.id.tv_send_phone)
    TextView tvSendPhone;
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;
    @BindView(R.id.rl_receive)
    RelativeLayout rlReceive;
    @BindView(R.id.tv_title_receive)
    TextView tvTitleReceive;
    @BindView(R.id.tv_receive_name)
    TextView tvReceiveName;
    @BindView(R.id.tv_receive_phone)
    TextView tvReceivePhone;
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @BindView(R.id.rl_goods)
    RelativeLayout rlGoods;
    @BindView(R.id.tv_title_goods)
    TextView tvTitleGoods;
    @BindView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @BindView(R.id.tv_shouzhi)
    TextView tv_shouzhi;
    @BindView(R.id.tv_goods_weiht)
    TextView tvGoodsWeiht;
    @BindView(R.id.tv_mark_title)
    TextView tvMarkTitle;
    @BindView(R.id.tv_goods_mark)
    TextView tvGoodsMark;
    @BindView(R.id.tv_dingdan)
    TextView tv_dingdan;
    @BindView(R.id.tv_yundan)
    TextView tv_yundan;
    @BindView(R.id.ll_message)
    LinearLayout ll_message;

    private RequestQueue mRequestQueue;
    private int REQUEST_DETAIL_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume_detail);
        ButterKnife.bind(this);
        initParams();
    }

    private void initParams() {

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        Intent intent = getIntent();
        String con_id = intent.getStringExtra("con_id");
        String user_id = intent.getStringExtra("user_id");
        String title = intent.getStringExtra("title");
        tv_title.setText(title);
        requestDetail(user_id, con_id);
    }

    private void requestDetail(String user_id, String con_id) {
        Request<String> request = NoHttpRequest.getConsumeDetailRequest(user_id, con_id);
        mRequestQueue.add(REQUEST_DETAIL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ConsumeRecordActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");

                        String types = dataObj.getString("types");

                        String con_amount = dataObj.getString("con_amount");
                        String budget = dataObj.getString("budget");
                        if ("1".equals(budget)) {
                            con_amount = "-" + con_amount+"元";
                        } else {
                            con_amount = "+" + con_amount+"元";
                        }
                        tv_shouzhi.setText(con_amount);

                        if ("1".equals(types) || "2".equals(types)) {
                            ll_message.setVisibility(View.VISIBLE);
                            JSONObject peopleObj = dataObj.getJSONObject("people");
                            setMessage(peopleObj);
                        } else {
                            ll_message.setVisibility(View.GONE);
                        }

                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(e.getMessage());
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setMessage(JSONObject peopleObj) throws JSONException {
        String order_number = peopleObj.getString("order_number");
        String waybill_number = peopleObj.getString("waybill_number");
        String send_name = peopleObj.getString("send_name");
        String send_phone = peopleObj.getString("send_phone");
        String send_address = peopleObj.getString("send_region") + peopleObj.getString("send_address");

        String collect_name = peopleObj.getString("collect_name");
        String collect_phone = peopleObj.getString("collect_phone");
        String collect_address = peopleObj.getString("collect_region") + peopleObj.getString("collect_address");

        String goods_weight = peopleObj.getString("goods_weight");
        String dot_weight = peopleObj.getString("dot_weight");
        String goods_name = peopleObj.getString("goods_name");
        String content = peopleObj.getString("content");

        tv_dingdan.setText(StringUtil.handleNullResultForString(order_number));
        tv_yundan.setText(StringUtil.handleNullResultForString(waybill_number));
        tvSendName.setText(StringUtil.handleNullResultForString(send_name));
        tvSendPhone.setText(StringUtil.handleNullResultForString(send_phone));
        String address = StringUtil.handleNullResultForString(send_address);
        tvSendAddress.setText(StringUtil.handleNullResultForString(send_address));

        tvReceiveName.setText(StringUtil.handleNullResultForString(collect_name));
        tvReceiveAddress.setText(StringUtil.handleNullResultForString(collect_address));
        tvReceivePhone.setText(StringUtil.handleNullResultForString(collect_phone));

        if ("null".equals(dot_weight) || null == dot_weight) {
            dot_weight = "(快递公司未返回重量)";
        } else {
            dot_weight = dot_weight + "kg"+"(快递公司返回重量)";
        }
        tvGoodsName.setText(StringUtil.handleNullResultForString(goods_name));
        tvGoodsWeiht.setText(dot_weight);
        tvMarkTitle.setText(StringUtil.handleNullResultForString(content));

    }

}
