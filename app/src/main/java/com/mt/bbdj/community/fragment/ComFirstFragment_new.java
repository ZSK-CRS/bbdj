package com.mt.bbdj.community.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.MingleAreaDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.DownloadUtil;
import com.mt.bbdj.baseconfig.utls.FileUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.TopSmoothScroller;
import com.mt.bbdj.baseconfig.view.HorizontalProgressBar;
import com.mt.bbdj.baseconfig.view.MyGridView;
import com.mt.bbdj.community.activity.ChangeManagerdActivity;
import com.mt.bbdj.community.activity.ClientManagerActivity;
import com.mt.bbdj.community.activity.ComplainManagerdActivity;
import com.mt.bbdj.community.activity.EnterManagerActivity;
import com.mt.bbdj.community.activity.GlobalSearchActivity;
import com.mt.bbdj.community.activity.MatterShopActivity;
import com.mt.bbdj.community.activity.MessageAboutActivity;
import com.mt.bbdj.community.activity.MessageManagerdActivity;
import com.mt.bbdj.community.activity.MoneyFormatManagerActivity;
import com.mt.bbdj.community.activity.OutManagerActivity;
import com.mt.bbdj.community.activity.RepertoryActivity;
import com.mt.bbdj.community.activity.SearchPackageActivity;
import com.mt.bbdj.community.activity.SendManagerActivity;
import com.mt.bbdj.community.activity.SendResByHandActivity;
import com.mt.bbdj.community.activity.SystemMessageAboutActivity;
import com.mt.bbdj.community.adapter.MyGridViewAdapter;
import com.mt.bbdj.community.adapter.WaitHandleAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版首页
 */
public class ComFirstFragment_new extends BaseFragment implements View.OnClickListener {

    private String APP_PATH_ROOT = FileUtil.getRootPath(MyApplication.getInstance()).getAbsolutePath() + File.separator + "bbdj";

    private WaitHandleAdapter mAdapter;

    private List<ProductModel> mList = new ArrayList<>();
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String user_id;

    private LinearLayout ll_express;    //快递
    private LinearLayout ll_water;    //桶装水
    private LinearLayout ll_clear;   //干洗

    private XRecyclerView recyclerView;
    private View mView;
    private LinearLayoutManager mLinearLayoutManager;


    public static ComFirstFragment_new getInstance() {
        ComFirstFragment_new comFirstFragment = new ComFirstFragment_new();
        return comFirstFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_com_first_fragment_new, container, false);
        initParams();
        initView();
        initData();
        requestData();
        return mView;
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getTest();
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PayforOrderActivity::" + response.get());

            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WaitHandleAdapter(mList);
        recyclerView = mView.findViewById(R.id.rl_product);
        View head = LayoutInflater.from(getActivity()).inflate(R.layout.head, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        ll_express = head.findViewById(R.id.ll_express);
        ll_water = head.findViewById(R.id.ll_water);
        ll_clear = head.findViewById(R.id.ll_clear);

        ll_express.setOnClickListener(this);
        ll_water.setOnClickListener(this);
        ll_clear.setOnClickListener(this);
        recyclerView.addHeaderView(head);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        for (int i = 0; i < 2; i++) {

            ProductModel productModel = new ProductModel();
            productModel.setShowType(i == 0);
            productModel.setType(0);
            productModel.setProductName("陈小可");
            productModel.setPhone("18435151841");
            productModel.setShowBottom(i == 1);
            productModel.setAddress("收件地址：山西省太原市小店区南中环街长治路口37号大生科技大厦305室");
            mList.add(productModel);

            productModel = null;
        }

        for (int i = 0; i < 3; i++) {

            ProductModel productModel = new ProductModel();
            productModel.setShowType(i == 0);
            productModel.setType(0);
            productModel.setProductName("陈小可");
            productModel.setShowBottom(i == 2);
            productModel.setPhone("18435151841");
            productModel.setAddress("收件地址：山西省太原市小店区南中环街长治路口37号大生科技大厦305室");
            mList.add(productModel);
            productModel = null;
        }

        for (int i = 0; i < 2; i++) {
            ProductModel productModel = new ProductModel();
            productModel.setShowType(i == 0);
            productModel.setType(0);
            productModel.setShowBottom(i == 1);
            productModel.setProductName("陈小可");
            productModel.setPhone("18435151841");
            productModel.setAddress("收件地址：山西省太原市小店区南中环街长治路口37号大生科技大厦305室");
            mList.add(productModel);
            productModel = null;
        }

        ProductModel productModel = new ProductModel();
        productModel.setType(3);
        mList.add(productModel);
        productModel = null;
        mAdapter.notifyDataSetChanged();

    }


    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_express:
                LinearSmoothScroller s1 = new TopSmoothScroller(getActivity());
                s1.setTargetPosition(4);
                mLinearLayoutManager.startSmoothScroll(s1);
                break;
            case R.id.ll_water:
                LinearSmoothScroller s2 = new TopSmoothScroller(getActivity());
                s2.setTargetPosition(2);
                mLinearLayoutManager.startSmoothScroll(s2);
                break;
            case R.id.ll_clear:
                LinearSmoothScroller s3 = new TopSmoothScroller(getActivity());
                s3.setTargetPosition(7);
                mLinearLayoutManager.startSmoothScroll(s3);
                break;
        }
    }
}
