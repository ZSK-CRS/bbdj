package com.mt.bbdj.baseconfig.utls;

import java.util.Random;

/**
 * Author : ZSK
 * Date : 2019/1/26
 * Description :  数字处理
 */
public class IntegerUtil {

    /**
     * 随机生成指定范围的数值
     * @param min  最小值
     * @param max 最大值
     * @return
     */
    public static int getRandomInteger(int min,int max) {
        Random random = new Random();
        int i = random.nextInt((max-min)+min);
        return i;
    }

    /**
     * 转换字符串
     * @param date
     * @return
     */
    public static int getStringChangeToNumber(String date) {
        if (date == null || "".equals(date) || "null".equals(date)) {
            return 0;
        }
        int number = Integer.parseInt(date);
        return number;
    }


    public static float getStringChangeToFloat(String date) {
        if (date == null || "".equals(date)||"null".equals(date)) {
            return 0;
        }
        float number = Float.parseFloat(date);
        return number;
    }

    /**
     * 转换日期字符串
     * @param temporaryString
     * @return
     */
    public static int getDateStringToNumber(String temporaryString){
        if (temporaryString == null || "".equals(temporaryString)) {
            return 0;
        }
        String current = temporaryString.replace("-","");
        int currentInt = Integer.parseInt(current);
        return currentInt;
    }
}
