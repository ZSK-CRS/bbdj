package com.mt.bbdj.community.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.BluetoothMessage;
import com.mt.bbdj.baseconfig.db.gen.BluetoothMessageDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.BitmapUtil;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.BluetoothSearchAdapter;
import com.yanzhenjie.durban.Controller;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import HPRTAndroidSDKA300.HPRTPrinterHelper;
import HPRTAndroidSDKA300.PublicFunction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothSearchActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;            //返回
    @BindView(R.id.rl_have_mate)
    RecyclerView rlHaveMate;         //已配对列表
    @BindView(R.id.rl_add_machine)
    RecyclerView rlAddMachine;      //搜索到的设备
    @BindView(R.id.bt_search)
    Button btSearch;              //搜索
    @BindView(R.id.tv_mate_machine)
    TextView tvMateMache;     //当前连接设备
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private BluetoothAdapter mBtAdapter;
    private List<String> mPaireDevicesData = new ArrayList<>();  //已配对的数据源
    private List<String> mNewDeviceData = new ArrayList<>();    //新扫描的数据源
    private BluetoothSearchAdapter mPairehAdapter;      //已配对的适配器
    private BluetoothSearchAdapter mNewAdapter;      //新扫描的适配器
    private List<String> strAddressList = new ArrayList<>();
    private DaoSession mDaoSession;
    private BluetoothMessageDao mBluetoothDao;    //蓝牙信息
    private String toothAddress = "";
    private Thread printThread;     //新添加的线程
    private Context thisCon = null;
    private Message message;
    private PublicFunction PFun = null;
    public static String paper = "0";


    private WaitDialog dialogLoading;
    private String bluetoothName;
    private SharedPreferences mShared;
    private String fastLogoBig;
    private String fastLogoMini;
    private String printType;     //表示进入此界面的途径，用于打印后的界面刷新  “1” ： 待收件界面   “2” ：待打印界面  "3" : 已完界面

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            dialogLoading.doDismiss();
            if (msg.what != 0) {
                ToastUtil.showShort("连接失败，请重试！");
            } else {
                String bluetoothName = (String) msg.obj;
                tvMateMache.setText(bluetoothName);
                printPanel();    //打印面单
                //打印完成之后刷新任务列表
                refreshDataList();
            }
        }
    };

    private Timer mPrintTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initData();
        initSetting();
        initView();
        initPannelData();   //面板信息
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY) {
            finish();
        }
    }

    private void initSetting() {
        String paper = PFun.ReadSharedPreferencesData("papertype");
        if (!"".equals(paper)) {
            BluetoothSearchActivity.paper = paper;
        }
    }

    private void initView() {
        initPairedList();     //初始化已配对列表
        initNewList();      //初始化新设备的列表
        initClickListenr();   //给列表的中的蓝牙设备创建监听事件
    }


    private void actionToPrintDetail() {
        Intent intent = new Intent(this, PrintPreviewActivity.class);
        startActivity(intent);
    }

    private void refreshDataList() {
        if ("1".equals(printType)) {
            //刷新“待收件”界面
            EventBus.getDefault().post(new TargetEvent(0));
        } else if ("2".equals(printType)) {
            //刷新“待打印”界面
            EventBus.getDefault().post(new TargetEvent(2));
        }
    }


    private void initClickListenr() {
        //已配对的列表
        mPairehAdapter.setConnectClickListener(new BluetoothSearchAdapter.OnItemConnectClickListener() {
            @Override
            public void onConnect(int position) {
                dialogLoading = WaitDialog.show(BluetoothSearchActivity.this,"连接中...").setCanCancel(true);

                try {
                    if (mBtAdapter.isDiscovering()) {
                        mBtAdapter.cancelDiscovery();
                    }

                    //获取蓝牙mvc地址
                    String info = mPaireDevicesData.get(position);
                    if (!info.contains("   ")) {
                        return;
                    }
                    String[] infoMessage = info.split("   ");
                    toothAddress = infoMessage[1];
                    bluetoothName = infoMessage[0];
                    if (!toothAddress.contains(":")) {
                        return;
                    }


                    printThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new HPRTPrinterHelper(thisCon, HPRTPrinterHelper.PRINT_NAME_A300);
                                int portOpen = HPRTPrinterHelper.PortOpen("Bluetooth," + toothAddress);
                                HPRTPrinterHelper.logcat("portOpen:" + portOpen);
                                message = new Message();
                                message.what = portOpen;
                                message.obj = bluetoothName;
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                dialogLoading.doDismiss();
                            }
                        }
                    });
                    printThread.start();
                } catch (Exception e) {
                    dialogLoading.doDismiss();
                    e.printStackTrace();
                }

            }
        });

        //配对列表的删除
        mPairehAdapter.setOnItemDeleteClickListener(new BluetoothSearchAdapter.OnItemDeleteClickListener() {
            @Override
            public void onClick(int position) {
                if (mBtAdapter == null) {
                    return;
                }
                Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
                for (BluetoothDevice device : bondedDevices) {
                    unpairDevice(device);
                }

                mPaireDevicesData = getPairedData();
                mPairehAdapter.setData(mPaireDevicesData);
            }
        });

        //新设备列表的取消
        mNewAdapter.setOnItemDeleteClickListener(new BluetoothSearchAdapter.OnItemDeleteClickListener() {
            @Override
            public void onClick(int position) {
                mNewDeviceData.remove(position);
                mNewAdapter.setData(mPaireDevicesData);
            }
        });

        //新设备列表的连接
        mNewAdapter.setConnectClickListener(new BluetoothSearchAdapter.OnItemConnectClickListener() {
            @Override
            public void onConnect(int position) {
                dialogLoading = WaitDialog.show(BluetoothSearchActivity.this,"连接中...").setCanCancel(true);
                try {
                    if (mBtAdapter.isDiscovering()) {
                        mBtAdapter.cancelDiscovery();
                    }

                    //获取蓝牙mvc地址
                    String info = mNewDeviceData.get(position);
                    if (!info.contains("   ")) {
                        return;
                    }
                    String[] infoMessage = info.split("   ");
                    toothAddress = infoMessage[1];
                    if (!toothAddress.contains(":")) {
                        return;
                    }

                    printThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new HPRTPrinterHelper(thisCon, HPRTPrinterHelper.PRINT_NAME_A300);
                                int portOpen = HPRTPrinterHelper.PortOpen("Bluetooth," + toothAddress);
                                HPRTPrinterHelper.logcat("portOpen:" + portOpen);
                                message = new Message();
                                message.what = portOpen;
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                dialogLoading.doDismiss();
                            }
                        }
                    });
                    printThread.start();
                } catch (Exception e) {
                    dialogLoading.doDismiss();
                    e.printStackTrace();
                }
            }
        });
    }


    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {

        }
    }

    private void initNewList() {
        rlAddMachine.setLayoutManager(new LinearLayoutManager(this));
        rlAddMachine.setFocusable(false);
        rlAddMachine.setNestedScrollingEnabled(false);
        mNewAdapter = new BluetoothSearchAdapter(this, mNewDeviceData);
        rlAddMachine.setAdapter(mNewAdapter);
        mNewAdapter.notifyDataSetChanged();
    }

    private void initPairedList() {
     /*   List<BluetoothMessage> bluetoothMessages = mBluetoothDao.queryBuilder().list();
        for (BluetoothMessage bluetoothMessage : bluetoothMessages) {
            mPaireDevicesData.add(bluetoothMessage.getBluetoothMessage());
        }*/
        rlHaveMate.setLayoutManager(new LinearLayoutManager(this));
        rlHaveMate.setFocusable(false);
        rlHaveMate.setNestedScrollingEnabled(false);
        mPairehAdapter = new BluetoothSearchAdapter(this, mPaireDevicesData);
        rlHaveMate.setAdapter(mPairehAdapter);
    }

    private void initData() {
        thisCon = this.getApplicationContext();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mBluetoothDao = mDaoSession.getBluetoothMessageDao();

        mShared = SharedPreferencesUtil.getSharedPreference();
        printType = mShared.getString("printType", "1");

        PFun = new PublicFunction(thisCon);

        mPrintTimer = new Timer();

        //监听蓝牙连接状态的广播
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, intent);

        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

        IntentFilter scanIntent = new IntentFilter();
        scanIntent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        scanIntent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        scanIntent.addAction(ACTION_PAIRING_REQUEST);
        scanIntent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        scanIntent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        scanIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mScaneceiver, scanIntent);
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(BluetoothSearchActivity.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BluetoothSearchActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        110);
            } else {
                scanBluetooth();    //扫描蓝牙设备
            }
        } else {
            scanBluetooth();    //扫描蓝牙设备
        }

    }

    private List<String> getPairedData() {
        mBluetoothDao.deleteAll();
        List<String> list = new ArrayList<>();
        List<BluetoothMessage> bluetoothMessages = new ArrayList<>();
        //获取蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //得到当前的已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName() + "   " + device.getAddress());
                bluetoothMessages.add(new BluetoothMessage(device.getName() + "   " + device.getAddress()));
            }
            //暂存到数据库中
            mBluetoothDao.saveInTx(bluetoothMessages);
        }

        return list;
    }

    @OnClick({R.id.iv_back, R.id.bt_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_search:
                startSearch();
                break;
        }
    }

    private void startSearch() {
        if (EnableBluetooth()) {    //判断蓝牙是否打开
            requestPermission();
        } else {
            ToastUtil.showShort("请先打开蓝牙！");
        }
    }

    private void scanBluetooth() {
        mProgress.setVisibility(View.VISIBLE);
        //更新一下配对的设备
        mPaireDevicesData = getPairedData();
        mPairehAdapter.setData(mPaireDevicesData);

        mNewDeviceData.clear();
        mProgress.setVisibility(View.VISIBLE);
        strAddressList.clear();
        //若是启动了扫描，就关闭扫描
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        //扫描
        int intStartCount = 0;
        while (!mBtAdapter.startDiscovery() && intStartCount < 3) {
            LogUtil.e("BlueTooth", "扫描尝试失败");
            intStartCount++;
        }

        if (intStartCount == 3) {
            mProgress.setVisibility(View.GONE);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                try {
                    HPRTPrinterHelper.PortClose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            tvMateMache.setText("请连接打印机！");
        }
    };

    //扫描结束
    public final BroadcastReceiver mScaneceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    if (device.getBluetoothClass().getMajorDeviceClass() == 1536) {
                        if (!strAddressList.contains(device.getAddress())) {
                            Bundle b = intent.getExtras();
                            //蓝牙信号强度
                            String object = String.valueOf(b.get("android.bluetooth.device.extra.RSSI"));
                            int valueOf = Integer.valueOf(object);
                            float power = (float) ((Math.abs(valueOf) - 59) / (10 * 2.0));
                            float pow = (float) Math.pow(10, power);
                            strAddressList.add(device.getAddress());
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            //搜索的蓝牙设备
                            mNewDeviceData.add(device.getName() + "   " + device.getAddress() + "   " + decimalFormat.format(pow) + " m");
                            mNewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                    default:
                        break;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgress.setVisibility(View.GONE);
            }
        }
    };

    private String packageCode;      //标识码
    private String barcode;     //运单号
    private String date;    //标识码和日期
    private String siteName;    //中转站
    private String mail_id;    //订单号
    private String Receiver;
    private String Receiver_Phone;
    private String Receiver_address;
    private String Receiver_address1;
    private String Sender;
    private String Sender_Phone;
    private String Sender_address;
    private String Sender_address1;
    private String number;     //驿站代码
    private String goods_name;     //商品名称
    private String weight;     //计费重量
    private String express_id;     //快递公司id
    private String printTime;     //打印时间
    private String code;     //标识码

    private void initPannelData() {
        mail_id = mShared.getString("mail_id", "");
        barcode = mShared.getString("yundanhao", "");
        date = mShared.getString("transit", "");
        siteName = mShared.getString("place", "");
        Receiver = mShared.getString("collect_name", "");
        Receiver_Phone = mShared.getString("collect_phone", "");
        Receiver_address = mShared.getString("collect_region", "");
        Receiver_address1 = mShared.getString("collect_address", "");
        packageCode = mShared.getString("code", "");

        Sender = mShared.getString("send_name", "");
        Sender_Phone = mShared.getString("send_phone", "");
        Sender_address = mShared.getString("send_region", "");
        Sender_address1 = mShared.getString("send_address", "");
        number = mShared.getString("number", "");
        goods_name = mShared.getString("goods_name", "");
        weight = mShared.getString("weight", "");
        express_id = mShared.getString("express_id", "");
        printTime = DateUtil.getCurrentTimeFormat("yyyy-MM-dd");
    }

    private void printPanel() {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
           // pum.put("[packageCode]", packageCode);
            pum.put("[packageCode]", "800-045-016 02");
            pum.put("[barcode]", barcode);
            pum.put("[date]", date);
            pum.put("[siteName]", siteName);
            pum.put("[Receiver]", Receiver);
            pum.put("[Receiver_Phone]", Receiver_Phone);
            pum.put("[Receiver_address]", Receiver_address);
            pum.put("[Receiver_address_all]", Receiver_address + Receiver_address1);
            pum.put("[Receiver_address1]", Receiver_address1);
            pum.put("[Sender]", Sender);
            pum.put("[Sender_Phone]", Sender_Phone);
            pum.put("[Sender_address]", Sender_address);
            pum.put("[Sender_address_all]", Sender_address + Sender_address1);
            pum.put("[Sender_address1]", Sender_address1);
            pum.put("[servicePhone]", "4008 6666");
            pum.put("[wight]", weight);
            pum.put("[printTime]", printTime);
            pum.put("[stageCode]", number);
            pum.put("[goodName]", goods_name);
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("ZhongTong.txt");//打印模版放在assets文件夹里
            String path = new String(InputStreamToByte(afis), "utf-8");//打印模版以utf-8无bom格式保存
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                path = path.replace(string, pum.get(string));
            }
            // HPRTPrinterHelper.printText(path);

            HPRTPrinterHelper.openEndStatic(true);//开启
            HPRTPrinterHelper.PrintData(path);//打印机打印

            InputStream inbmp = this.getResources().getAssets().open("ic_logo_mini.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inbmp);
            HPRTPrinterHelper.Expanded("20", "10", bitmap, (byte) 0);//第一联 顶部兵兵logo

            InputStream inbmp6 = this.getResources().getAssets().open("ic_send_logo.png");
            Bitmap bitmap6 = BitmapFactory.decodeStream(inbmp6);
            HPRTPrinterHelper.Expanded("525", "100", bitmap6, (byte) 0);// 第一联 派
            InputStream inbmp7 = this.getResources().getAssets().open("ic_receive_logo.png");
            Bitmap bitmap7 = BitmapFactory.decodeStream(inbmp7);
            HPRTPrinterHelper.Expanded("525", "675", bitmap7, (byte) 0);// 第二联 收
            InputStream inbmp8 = this.getResources().getAssets().open("ic_post_logo.png");
            Bitmap bitmap8 = BitmapFactory.decodeStream(inbmp8);
            HPRTPrinterHelper.Expanded("525", "1120", bitmap8, (byte) 0);// 第三联 寄
            InputStream inbmp4 = this.getResources().getAssets().open("ic_code.png");
            Bitmap bitmap4 = BitmapFactory.decodeStream(inbmp4);
            HPRTPrinterHelper.Expanded("443", "940", bitmap4, (byte) 0);//二维码
            InputStream inbmp3 = this.getResources().getAssets().open("ic_logo_mini.png");
            Bitmap bitmap3 = BitmapFactory.decodeStream(inbmp3);
            HPRTPrinterHelper.Expanded("20", "975", bitmap3, (byte) 0);//第二联 兵兵logo

            fastLogoBig = "";
            fastLogoMini = "";

            setLogoData();   //设置logo

            InputStream inbmp5 = this.getResources().getAssets().open(fastLogoMini);
            Bitmap bitmap5 = BitmapFactory.decodeStream(inbmp5);
            HPRTPrinterHelper.Expanded("410", "490", bitmap5, (byte) 0);//第一联 快递公司logo

            InputStream inbmp1 = this.getResources().getAssets().open(fastLogoBig);
            Bitmap bitmap1 = BitmapFactory.decodeStream(inbmp1);
            HPRTPrinterHelper.Expanded("20", "605", bitmap1, (byte) 0);// 第二联 快递公司logo

            InputStream inbmp2 = this.getResources().getAssets().open(fastLogoBig);
            Bitmap bitmap2 = BitmapFactory.decodeStream(inbmp2);
            HPRTPrinterHelper.Expanded("310", "1115", bitmap2, (byte) 0);//第三联 快递公司logo

            /*  if ("1".equals(BluetoothSearchActivity.paper)) {
                HPRTPrinterHelper.Form();
            }*/
            HPRTPrinterHelper.Form();
            HPRTPrinterHelper.Print();

            int endStatus = HPRTPrinterHelper.getEndStatus(16);//获取打印状态
            HPRTPrinterHelper.openEndStatic(false);//关闭

            if (endStatus == 0) {
                //从已处理界面传递过来
                if ("3".equals(printType)) {
                    finish();
                } else {
                    //打印成功，跳转到打印详情列表
                    actionToPrintDetail();
                }
            } else {
                ToastUtil.showShort("打印失败！");
            }


        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
        }
    }

    private void setLogoData() {
        switch (express_id) {
            case "201":    //中通
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;
            case "202":    //顺丰
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;
            case "203":    //韵达
                fastLogoBig = "ic_zhongtong_big.png";
                fastLogoMini = "ic_zhongtong_mini.png";
                break;
            case "204":    //申通
                fastLogoBig = "ic_shentong_big.png";
                fastLogoMini = "ic_shentong_mini.png";
                break;
            case "205":    //德邦
                fastLogoBig = "ic_shentong_big.png";
                fastLogoMini = "ic_shentong_mini.png";
                break;
            case "206":    //天天
                fastLogoBig = "ic_tiantian_big.png";
                fastLogoMini = "ic_tiantian_mini.png";
                break;
            case "207":    //EMS
                fastLogoBig = "ic_tiantian_big.png";
                fastLogoMini = "ic_tiantian_mini.png";
                break;
            case "208":    //优速
                fastLogoBig = "ic_yousu_big.png";
                fastLogoMini = "ic_yousu_mini.png";
                break;
            case "209":    //圆通
                fastLogoBig = "ic_yuantong_big.png";
                fastLogoMini = "ic_yuantong_mini.png";
                break;
            case "210":    //百世
                fastLogoBig = "ic_baishi_big.png";
                fastLogoMini = "ic_baishi_mini.png";
                break;
        }
    }


    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    //EnableBluetooth
    private boolean EnableBluetooth() {
        boolean bRet = false;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            if (mBtAdapter.isEnabled())
                return true;
            mBtAdapter.enable();
        }
        return bRet;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        if (printThread != null) {
            Thread dummy = printThread;
            printThread = null;
            dummy.interrupt();
        }
        try {
            HPRTPrinterHelper.PortClose();
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (printThread != null) {
            Thread dummy = printThread;
            printThread = null;
            dummy.interrupt();
        }

    }


    @PermissionYes(110)
    private void getMultiYes(List<String> grantedPermissions) {
    }

    @PermissionNo(110)
    private void getMultiNo(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, 110).show();
        }
    }

}
