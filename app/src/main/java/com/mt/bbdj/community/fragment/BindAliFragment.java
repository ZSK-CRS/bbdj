package com.mt.bbdj.community.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kongzue.dialog.v2.SelectDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.BindAccountModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.CommonDialog;
import com.mt.bbdj.community.activity.SearchPackageActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :
 */
public class BindAliFragment extends Fragment {

    private Button mBtbindAccount;
    private EditText mAcountName, mAccount;
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int ACTION_BIND_ACCOUNT = 100;
    private WaitDialog waitDialog;
    private View view;

    public static BindAliFragment getInstance() {
        BindAliFragment bf = new BindAliFragment();
        return bf;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bind_ali, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initParams();
        mBtbindAccount = view.findViewById(R.id.bt_commit);
        mAcountName = view.findViewById(R.id.et_account_name);
        mAccount = view.findViewById(R.id.et_account);
        mBtbindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitAccount();
            }
        });

        mBtbindAccount.setClickable(false);
        mBtbindAccount.setBackgroundResource(R.drawable.shap_bt_cirle);
        mBtbindAccount.setTextColor(Color.parseColor("#ffffff"));
    }

    private void showCommitDialog(final String accountName, final String account) {
        SelectDialog.show(getActivity(), "提示", "请确认绑定账号，绑定之后不可更改！", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commitAccountMessage(accountName,account);
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCanCancel(true);
      /* new CommonDialog.Builder(getActivity(),1,false)
               .setTitle("提示")
               .setMessage("请确认绑定账号，绑定之后不可更改！")
               .setNegativeButton("取消", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                   }
               }).setPositiveButton("确定", new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               commitAccountMessage(accountName,account);
           }
       },R.color.blue).show();*/
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent){
        //两者都绑定或者只绑定支付宝则赋值
        if (targetEvent.getTarget() == TargetEvent.BIND_ALI_ACCOUNT ||
                targetEvent.getTarget() == TargetEvent.BIND_ACCOUNT_BUTTON) {
            BindAccountModel bindAccountModel = (BindAccountModel) targetEvent.getObject();
            mAcountName.setText(bindAccountModel.getAli_realName());
            mAccount.setText(bindAccountModel.getAli_account());
        } else {
            mBtbindAccount.setClickable(true);
            mBtbindAccount.setBackgroundResource(R.drawable.bt_bg_8);
            mBtbindAccount.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    private void commitAccount() {
        String accountName = mAcountName.getText().toString();
        String account = mAccount.getText().toString();
        if (isCommit(accountName, account)){
            showCommitDialog(accountName, account);
        }
    }

    private void commitAccountMessage(String accountName, String account) {
        Request<String> request = NoHttpRequest.getBindAccountRequest(user_id,"2",accountName,account,"","");
        mRequestQueue.add(ACTION_BIND_ACCOUNT, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                waitDialog = WaitDialog.show(getActivity(),"提交中...");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "BindAccountFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg  = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        mBtbindAccount.setClickable(false);
                        mBtbindAccount.setBackgroundResource(R.drawable.shap_bt_cirle);
                        mBtbindAccount.setTextColor(Color.parseColor("#ffffff"));
                    }
                    ToastUtil.showShort(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                waitDialog.doDismiss();
            }
        });
    }

    private boolean isCommit(String accountName, String account) {
        if ("".equals(accountName)) {
            ToastUtil.showShort("姓名不可为空！");
            return false;
        }
        if ("".equals(account)) {
            ToastUtil.showShort("账号不可为空！");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
