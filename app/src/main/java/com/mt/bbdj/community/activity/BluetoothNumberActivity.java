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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.BluetoothMessage;
import com.mt.bbdj.baseconfig.db.gen.BluetoothMessageDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.model.PrintNumberModel;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.BluetoothSearchAdapter;
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

import HPRTAndroidSDKA300.HPRTPrinterHelper;
import HPRTAndroidSDKA300.PublicFunction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothNumberActivity extends BaseActivity {

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            dialogLoading.doDismiss();
            if (msg.what != 0) {
                ToastUtil.showShort("连接失败，请重试！");
            } else {
                String bluetoothName = (String) msg.obj;
                tvMateMache.setText(bluetoothName);

                for (HashMap<String,String> data : dataList) {
                    String code = data.get("code");
                    String qrcode = data.get("qrcode");
                    code = StringUtil.handleNullResultForString(code);
                    qrcode = StringUtil.handleNullResultForString(qrcode);
                    printPanel(code,qrcode);    //打印面单
                }
            }
        }
    };

    private Timer mPrintTimer;
    private List<HashMap<String, String>> dataList;


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
            BluetoothNumberActivity.paper = paper;
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


    private void initClickListenr() {
        //已配对的列表
        mPairehAdapter.setConnectClickListener(new BluetoothSearchAdapter.OnItemConnectClickListener() {
            @Override
            public void onConnect(int position) {
                dialogLoading = WaitDialog.show(BluetoothNumberActivity.this, "连接中...").setCanCancel(true);

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
                mNewAdapter.setData(mNewDeviceData);
            }
        });

        //新设备列表的连接
        mNewAdapter.setConnectClickListener(new BluetoothSearchAdapter.OnItemConnectClickListener() {
            @Override
            public void onConnect(int position) {
                dialogLoading = WaitDialog.show(BluetoothNumberActivity.this, "连接中...").setCanCancel(true);
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
            if (ContextCompat.checkSelfPermission(BluetoothNumberActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BluetoothNumberActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
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

    private void initPannelData() {
        Intent intent = getIntent();
        PrintTagModel printData = (PrintTagModel) intent.getSerializableExtra("printData");
        dataList = printData.getData();
    }

    private void printPanel(String code,String qrcode) {
        try {
            HashMap<String, String> pum = new HashMap<String, String>();
            // pum.put("[packageCode]", packageCode);
            pum.put("[tag]", code);
            Set<String> keySet = pum.keySet();
            Iterator<String> iterator = keySet.iterator();
            InputStream afis = this.getResources().getAssets().open("number.txt");//打印模版放在assets文件夹里
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
            HPRTPrinterHelper.Expanded("35", "30", bitmap, (byte) 0);//第一联 顶部兵兵logo

            if ("1".equals(BluetoothNumberActivity.paper)) {
                HPRTPrinterHelper.Form();
            }

            HPRTPrinterHelper.PrintQR(HPRTPrinterHelper.BARCODE, "340", "55", "2", "6", qrcode);

            HPRTPrinterHelper.Form();
            HPRTPrinterHelper.Print();

            int endStatus = HPRTPrinterHelper.getEndStatus(16);//获取打印状态
            HPRTPrinterHelper.openEndStatic(false);//关闭

            if (endStatus == 0) {
                //打印成功，跳转到打印详情列表
               // finish();
            } else {
                ToastUtil.showShort("打印失败！");
            }

        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
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

            if (mScaneceiver != null) {
                unregisterReceiver(mScaneceiver);
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
