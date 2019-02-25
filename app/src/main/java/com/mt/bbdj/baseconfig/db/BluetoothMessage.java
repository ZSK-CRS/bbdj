package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author : ZSK
 * Date : 2019/1/14
 * Description : 用来存储已配对蓝牙信息
 */
@Entity
public class BluetoothMessage {
    public String bluetoothMessage;

    @Generated(hash = 771501420)
    public BluetoothMessage(String bluetoothMessage) {
        this.bluetoothMessage = bluetoothMessage;
    }

    @Generated(hash = 1391737155)
    public BluetoothMessage() {
    }

    public String getBluetoothMessage() {
        return this.bluetoothMessage;
    }

    public void setBluetoothMessage(String bluetoothMessage) {
        this.bluetoothMessage = bluetoothMessage;
    }

  
}
