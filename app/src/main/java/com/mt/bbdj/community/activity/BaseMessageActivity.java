package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;

import org.raphets.roundimageview.RoundImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseMessageActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.iv_head)
    RoundImageView ivHead;
    @BindView(R.id.tv_mingcheng)
    TextView tvMingcheng;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_email)
    TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_message);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() == 0) {
            return ;
        }
        UserBaseMessage userBaseMessage = userBaseMessages.get(0);
        String mingcheng = userBaseMessage.getMingcheng();
        String name = userBaseMessage.getContacts();
        String phone = userBaseMessage.getContact_number();
        String email = userBaseMessage.getContact_email();
        tvMingcheng.setText(mingcheng);
        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
    }


    @OnClick({R.id.iv_back, R.id.iv_head})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_head:
                break;
        }
    }
}
