package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.JsonBean;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientMessageChangeActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_address_manager)
    TextView tvAddressManager;
    @BindView(R.id.et_input_name)
    EditText etInputName;
    @BindView(R.id.et_input_phone)
    EditText etInputPhone;
    @BindView(R.id.tv_company)
    EditText tvCompany;
    @BindView(R.id.tv_detail_address)
    EditText tvDetailAddress;
    @BindView(R.id.bt_save_message)
    Button btSaveMessage;
    @BindView(R.id.tv_select_genel_address)
    TextView tvSelectGenelAddress;
    @BindView(R.id.tv_select_arrow)
    ImageView tvSelectArrow;
    @BindView(R.id.et_marke)
    EditText etMarke;

    private RequestQueue mRequestQueue;
    private String user_id;
    private String realname;    //名字
    private String telephone;   //电话
    private String region;  //地区
    private String address;    //详细地址
    private String company_name;   //公司名称
    private String content;    //备注

    private ArrayList<JsonBean> options1Items = new ArrayList<>();    //省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();   //市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();     //县/区

    private List<Province> provincesList = new ArrayList<Province>();
    private List<City> citysList = new ArrayList<City>();
    private List<County> areasList = new ArrayList<County>();

    private String mProvince;
    private String mCity;
    private String mCountry;
    private ProvinceDao mProvinceDao;
    private CityDao mCityDao;
    private CountyDao mCountyDao;
    private boolean isEdit;
    private String customer_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_message_change);
        ButterKnife.bind(this);
        initParams();
        initView();
        initAreaData();
    }

    private void initView() {
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit",false);
        if (isEdit) {
            tvAddressManager.setText("编辑客户");
            restoreData(intent);
        } else {
            tvAddressManager.setText("添加客户");
        }

    }

    private void restoreData(Intent intent) {
        customer_id = intent.getStringExtra("customer_id");
        String customer_realname = intent.getStringExtra("customer_realname");
        String customer_telephone = intent.getStringExtra("customer_telephone");
        String company_name = intent.getStringExtra("company_name");
        String customer_region = intent.getStringExtra("customer_region");
        String customer_address = intent.getStringExtra("customer_address");
        String content = intent.getStringExtra("content");

        etInputName.setText(customer_realname);
        etInputPhone.setText(customer_telephone);
        tvSelectGenelAddress.setText(customer_region);
        tvDetailAddress.setText(customer_address);
        tvCompany.setText(company_name);
        etMarke.setText(content);
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mProvinceDao = mDaoSession.getProvinceDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
    }


    private void saveClientMessage() {
        if (!isRight()) {
            return;
        }

        if (isEdit) {
            saveEditMessage();
        } else {
            saveAddMessage();
        }

    }

    private void saveEditMessage() {
        Request<String> request = NoHttpRequest.editClientMessage(user_id,realname,telephone,
                region,address,company_name,content,customer_id);
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ClientMessageChangeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    ToastUtil.showShort(msg);
                    if ("5001".equals(code)) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                //   dialogLoading.cancel();
            }
        });
    }

    private void saveAddMessage() {
        Request<String> request = NoHttpRequest.addClientRequest(user_id,realname,telephone,region,address,company_name,content);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ClientMessageChangeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    ToastUtil.showShort(msg);
                    if ("5001".equals(code)) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                //   dialogLoading.cancel();
            }
        });
    }

    private boolean isRight() {
        realname = etInputName.getText().toString();
        telephone = etInputPhone.getText().toString();
        region = tvSelectGenelAddress.getText().toString();
        address = tvDetailAddress.getText().toString();
        company_name = tvCompany.getText().toString();
        content = etMarke.getText().toString();
        if ("".equals(realname)) {
            ToastUtil.showShort("姓名不可为空！");
            return false;
        }
        if (!StringUtil.isMobile(telephone)) {
            ToastUtil.showShort("联系号码格式错误！");
            return false;
        }
        if ("".equals(region)) {
            ToastUtil.showShort("请选择地区");
            return false;
        }
        if ("".equals(address)) {
            ToastUtil.showShort("请填写详细地区！");
            return false;
        }
        if ("".equals(company_name)) {
            ToastUtil.showShort("公司名称不可为空！");
            return false;
        }
        return true;
    }

    @OnClick({R.id.iv_back, R.id.tv_select_arrow, R.id.tv_select_genel_address, R.id.bt_save_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_select_arrow:
            case R.id.tv_select_genel_address:
                selectGenelAddress();
                break;
            case R.id.bt_save_message:
                saveClientMessage();
                break;
        }
    }

    private void selectGenelAddress() {
        if (options1Items.size() > 0 && options2Items.size() > 0 && options3Items.size() > 0) {
            showPickerView();
        }
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mProvince = options1Items.get(options1).getPickerViewText();
                mCity = options2Items.get(options1).get(options2);
                mCountry = options3Items.get(options1).get(options2).get(options3);
                tvSelectGenelAddress.setText(mProvince + mCity + mCountry);
            }
        }).setSelectOptions(0, 0, 0)  //设置默认选中项
                .setTitleText("地区选择")
                .setTitleSize(16)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(16)
                .setLineSpacingMultiplier(2.5f)
                .setCancelColor(Color.parseColor("#0da95f"))
                .setSubmitColor(Color.parseColor("#0da95f"))
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initAreaData() {
        ArrayList<JsonBean> jsonBean = new ArrayList<>();
        provincesList = mProvinceDao.queryBuilder().list();
        for (int i = 0; i < provincesList.size(); i++) {
            JsonBean item = new JsonBean();
            item.setName(provincesList.get(i).getRegion_name());
            item.setProvince(provincesList.get(i).getId());
            List<JsonBean.CityBean> cityBeansList = new ArrayList<>();
            citysList = mCityDao.queryBuilder().where(CityDao.Properties.Parent_id.eq(provincesList.get(i).getId())).list();
            for (int il = 0; il < citysList.size(); il++) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(citysList.get(il).getRegion_name());
                item1.setCity(citysList.get(il).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();
                areasList = mCountyDao.queryBuilder().where(CountyDao.Properties.Parent_id.eq(citysList.get(il).getId())).list();
                for (int i2 = 0; i2 < areasList.size(); i2++) {
                    area.add(areasList.get(i2).getRegion_name());
                    areaId.add(areasList.get(i2).getId());
                }
                if (area.size() < 0) {
                    area.add(citysList.get(il).getRegion_name());
                    areaId.add(citysList.get(il).getId());
                }
                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            if (citysList.size() <= 0) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(provincesList.get(i).getRegion_name());
                item1.setCity(provincesList.get(i).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();

                area.add(provincesList.get(i).getRegion_name());
                areaId.add(provincesList.get(i).getId());


                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            item.setCityList(cityBeansList);
            jsonBean.add(item);
        }
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {   //遍历省份
            ArrayList<String> cityList = new ArrayList<>();     //该省的城市列表
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();    //该省的所有地区列表

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {   //遍历该省的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);   //添加城市
                ArrayList<String> city_ArrayList = new ArrayList<>();    //该城市的所有地区列表

                //若是无地区数据，天剑空字符串，放置数据为null 导致三个选型的长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null ||
                        jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_ArrayList.add("");
                } else {
                    city_ArrayList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                province_AreaList.add(city_ArrayList);   //添加该省所有地区数据
            }

            //添加城市数据
            options2Items.add(cityList);

            //添加地区数据
            options3Items.add(province_AreaList);
        }
    }

}
