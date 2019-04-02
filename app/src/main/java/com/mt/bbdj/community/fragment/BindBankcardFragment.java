package com.mt.bbdj.community.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.kongzue.dialog.v2.SelectDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.BindAccountModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.FileUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.RecognizeService;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.CommonDialog;
import com.mt.bbdj.community.activity.PayforOrderActivity;
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

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :
 */
public class BindBankcardFragment extends Fragment {

    @BindView(R.id.et_account_name)
    EditText etAccountName;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.iv_identify_bank)
    ImageView ivIdentifyBank;
    @BindView(R.id.et_account_bank)
    EditText etAccountBank;
    @BindView(R.id.bt_commit)
    Button btCommit;
    @BindView(R.id.rl_take_camera)
    RelativeLayout takeCamera;

    Unbinder unbinder;
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int ACTION_BIND_ACCOUNT = 200;
    private WaitDialog waitDialog;
    private View view;
    private String picturePath = "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;
    private File compressPicture;
    private final int TAKE_BANK = 100;
    private WaitDialog waitDialog1;

    public static BindBankcardFragment getInstance() {
        BindBankcardFragment bf = new BindBankcardFragment();
        return bf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bind_bank_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        return view;
    }

    private void initView() {
        btCommit.setClickable(false);
        btCommit.setBackgroundResource(R.drawable.shap_bt_cirle);
        btCommit.setTextColor(Color.parseColor("#ffffff"));
        takeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeBankPicture();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        //两者都绑定或者只绑定银行卡则赋值
        if (targetEvent.getTarget() == TargetEvent.BIND_BANK_ACCOUNT || targetEvent.getTarget() == TargetEvent.BIND_ACCOUNT_BUTTON) {
            BindAccountModel bindAccountModel = (BindAccountModel) targetEvent.getObject();
            etAccountName.setText(bindAccountModel.getBank_realName());
            etAccountBank.setText(bindAccountModel.getBank());
            etAccount.setText(bindAccountModel.getBack_number());
            etAccount.setEnabled(false);
            etAccountName.setEnabled(false);
            etAccountBank.setEnabled(false);
            takeCamera.setClickable(false);
        } else {
            btCommit.setClickable(true);
            btCommit.setBackgroundResource(R.drawable.bt_bg_8);
            btCommit.setTextColor(Color.parseColor("#ffffff"));
            etAccount.setEnabled(true);
            etAccountName.setEnabled(true);
            etAccountBank.setEnabled(true);
            takeCamera.setClickable(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.bt_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_commit:
                commitAccount();
                break;
        }
    }

    private void takeBankPicture() {
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }
            String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            compressPicture = new File(f, uuid);
            Uri photoURI = FileProvider.getUriForFile(getActivity(), MyApplication.getInstance().getPackageName() + ".provider", photoFile);
            Intent intent = new Intent();

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

            startActivityForResult(intent, TAKE_BANK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_BANK && resultCode == RESULT_OK) {
            waitDialog1 = WaitDialog.show(getActivity(), "识别中...").setCanCancel(true);
            identifyBank();    //识别银行卡
        }
    }

    private void identifyBank() {
        RecognizeService.recBankCard(getActivity(), photoFile.getAbsolutePath(), new RecognizeService.ServiceListener() {
            @Override
            public void onResult(String result) {
                String[] resultArray = result.split("\n");
                String accountNumber = resultArray[0];
                String realAccount = accountNumber.replace("卡号：", "");
                etAccount.setText(realAccount);

                String bankName = resultArray[2];
                String realBank = bankName.replace("发卡行：", "");
                etAccountBank.setText(realBank);
                waitDialog1.doDismiss();
            }
        });
    }

    private void showCommitDialog(final String accountName, final String account, final String accountBank) {

        SelectDialog.show(getActivity(), "提示", "请确认绑定账号，绑定之后不可更改！", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        commitAccountMessage(accountName, account, accountBank);
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCanCancel(true);
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

    private void commitAccount() {
        String accountName = etAccountName.getText().toString();
        String account = etAccount.getText().toString();
        String accountBank = etAccountBank.getText().toString();
        if (isCommit(accountName, account, accountBank)) {
            showCommitDialog(accountName, account, accountBank);
        }
    }

    private void commitAccountMessage(String accountName, String account, String accountBank) {
        Request<String> request = NoHttpRequest.getBindAccountRequest(user_id, "1", accountName, "", accountBank, account);
        mRequestQueue.add(ACTION_BIND_ACCOUNT, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                waitDialog = WaitDialog.show(getActivity(), "提交中...");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "BindBankFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        btCommit.setClickable(false);
                        btCommit.setBackgroundResource(R.drawable.shap_bt_cirle);
                        btCommit.setTextColor(Color.parseColor("#ffffff"));

                        etAccount.setEnabled(false);
                        etAccountName.setEnabled(false);
                        etAccountBank.setEnabled(false);
                        takeCamera.setClickable(false);
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

    private boolean isCommit(String accountName, String account, String accountBank) {
        if ("".equals(accountName)) {
            ToastUtil.showShort("姓名不可为空！");
            return false;
        }
        if ("".equals(account)) {
            ToastUtil.showShort("账号不可为空！");
            return false;
        }
        if ("".equals(accountBank)) {
            ToastUtil.showShort("开户行不可为空！");
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
