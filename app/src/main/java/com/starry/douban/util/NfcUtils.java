package com.starry.douban.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.text.TextUtils;

import com.starry.log.Logger;

import java.io.IOException;

/**
 * nfc相关工具方法
 *
 * @author Starry Jerry
 * @since 18-8-26
 */

public class NfcUtils {

    /**
     * 字符序列转换为16进制字符串
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    /**
     * 解析Intent
     */
    public static String processIntent(Intent intent) {
        String deviceId = "";
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent == null) {
            return deviceId;
        }

        String[] techList = tagFromIntent.getTechList();
        String json = JsonUtil.toJson(techList);
        Logger.e(json);

        if (json.contains("MifareUltralight")) {
            deviceId = readMU(tagFromIntent);
        } else if (json.contains("MifareClassic")) {
            deviceId = readMC(tagFromIntent);
        }

        Logger.i("deviceId: " + deviceId);
        if (!TextUtils.isEmpty(deviceId) && deviceId.length() > 8) {
            //截取前8位
            return deviceId.toUpperCase().substring(0, 8);
        }
        return deviceId;
    }

    /**
     * MifareClassic格式标签
     * <p>
     * 第一扇区的第一块一般用于制造商占用块
     * <p>
     * 0-15个扇区：一个扇区对应4个块，所以总共有64个块，序号分别为0-63，第一个扇区对应：0-3块，第二个扇区对应：4-7块…
     * <p>
     * 每个扇区的最后一个块用来存放密码或控制位，其余为数据块，一个块占用16个字节，keyA占用6字节，控制位占用4字节，keyB占用6字节。
     *
     * @param tag Tag
     * @return 设备信息
     */
    private static String readMC(Tag tag) {
        String deviceId = "";
        StringBuilder metaData = new StringBuilder();
        //读取TAG
        MifareClassic mfc = MifareClassic.get(tag);
        try {
            //Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaData.append("卡片类型：")
                    .append(typeS)
                    .append("\n共")
                    .append(sectorCount)
                    .append("个扇区\n共")
                    .append(mfc.getBlockCount())
                    .append("个块\n存储空间: ")
                    .append(mfc.getSize())
                    .append("B\n");
            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                boolean auth = mfc.authenticateSectorWithKeyA(j,
                        MifareClassic.KEY_DEFAULT);
                int bCount;
                int bIndex;
                if (auth) {
                    metaData.append("Sector ")
                            .append(j)
                            .append(":验证成功\n");
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        String temp = bytesToHexString(data);
                        if (j == 0 && i == 0) {//第1个扇区的第1个块存储着卡片信息
                            deviceId = temp;
                        }
                        metaData.append("Block ")
                                .append(bIndex)
                                .append(" : ")
                                .append(temp)
                                .append("\n");
                        bIndex++;
                    }
                } else {
                    metaData.append("Sector ")
                            .append(j)
                            .append(":验证失败\n");
                }

            }
            Logger.i(metaData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    /**
     * MifareUltralight读取
     *
     * @param tag Tag
     * @return 设备信息
     */
    private static String readMU(Tag tag) {
        MifareUltralight light = MifareUltralight.get(tag);
        String deviceId = "";
        try {
            light.connect();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                byte[] bytes = light.readPages(i);
                String data = bytesToHexString(bytes);
                if (i == 0) {
                    deviceId = data;
                }
                stringBuilder.append(data).append("\n");
            }
            String temp = stringBuilder.toString();
            Logger.e(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceId;
    }

}
