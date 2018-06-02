package com.zlm.libs.register;

import android.os.Environment;
import android.text.TextUtils;

import com.zlm.libs.register.model.RegisterInfo;
import com.zlm.libs.register.utils.DateUtils;
import com.zlm.libs.register.utils.EncryptUtils;
import com.zlm.libs.register.utils.StringCompressUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Cipher;

/**
 * @Description: 注册器处理类
 * @author: zhangliangming
 * @date: 2018-06-02 12:56
 **/
public class RegisterHelper {
    /**
     * 校验码
     */
    private static String mVerifyKey = EncryptUtils.md5("zlm-19901202", "19910117");
    /**
     * 注册码保存路径
     */
    private static String mRegisterCodeFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + ".zlm" + File.separator + "register.key";

    /**
     * 默认1天的时间
     */
    private static long mDefTime = DateUtils.getDateAfter(new Date(), 1).getTime();

    /**
     * @throws
     * @Description: 生成机器码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 13:07
     */
    public static String createMachineCode() throws Exception {
        String serial = android.os.Build.SERIAL;
        UUID uuid = UUID.nameUUIDFromBytes(serial.getBytes("utf-8"));
        return uuid.toString();
    }

    /**
     * @throws
     * @Description: 生成注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 13:07
     */
    public static String createRegisterCode() throws Exception {
        return createRegisterCode(createMachineCode());
    }

    /**
     * @throws
     * @Description: 生成注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 13:07
     */
    public static String createRegisterCode(String machineCode) throws Exception {
        String registerCode = EncryptUtils.des(machineCode, mVerifyKey, Cipher.ENCRYPT_MODE);
        return registerCode;
    }

    /**
     * @throws
     * @Description: 保存注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 16:52
     */
    public static boolean saveRegisterCode(String registerCode) {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setRegisterCode(registerCode);
        return saveRegisterCode(registerInfo);
    }

    /**
     * @throws
     * @Description: 删除注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 18:34
     */
    public static void cleanRegisterCode() {
        File registerCodeFile = new File(mRegisterCodeFilePath);
        registerCodeFile.deleteOnExit();
    }

    /**
     * @throws
     * @Description: 保存注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 16:52
     */
    public static boolean saveRegisterCode(RegisterInfo registerInfo) {
        try {
            if (!verify(registerInfo.getRegisterCode())) {
                return false;
            }
            File registerCodeFile = new File(mRegisterCodeFilePath);
            if (!registerCodeFile.getParentFile().exists()) {
                registerCodeFile.getParentFile().mkdirs();
            }
            //获取保存的数据
            String saveData = getSaveData(registerInfo);
            FileOutputStream os = new FileOutputStream(registerCodeFile);
            os.write(StringCompressUtils.compress(saveData, Charset.forName("utf-8")));
            os.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param registerInfo
     * @return
     */
    private static String getSaveData(RegisterInfo registerInfo) {
        JSONObject object = new JSONObject();
        try {
            if (TextUtils.isEmpty(registerInfo.getRegisterCode())) {
                object.put("registerCode", "");
            } else {
                object.put("registerCode", registerInfo.getRegisterCode());
            }
            if (registerInfo.getTime() == 0) {
                object.put("time", mDefTime);
            } else {
                object.put("time", registerInfo.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * @throws
     * @Description: 读取注册码
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 17:04
     */
    private static RegisterInfo readRegisterInfo() {

        RegisterInfo registerInfo = new RegisterInfo();
        File registerCodeFile = new File(mRegisterCodeFilePath);
        if (!registerCodeFile.exists()) {
            if (TextUtils.isEmpty(registerInfo.getRegisterCode())) {
                registerInfo.setRegisterCode("");
            }
            if (registerInfo.getTime() == 0) {
                registerInfo.setTime(mDefTime);
            }
            saveRegisterCode(registerInfo);

        } else {
            try {
                InputStream in = new FileInputStream(registerCodeFile);
                String registerInfoString = StringCompressUtils.decompress(in,
                        Charset.forName("utf-8"));
                JSONObject object = new JSONObject(registerInfoString);
                if (object.has("registerCode")) {
                    registerInfo.setRegisterCode(object.getString("registerCode"));
                }
                if (object.has("time")) {
                    registerInfo.setTime(object.getLong("time"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return registerInfo;

    }

    /**
     * @throws
     * @Description: 校验
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 13:11
     */
    public static void verify() throws Exception {
        RegisterInfo registerInfo = readRegisterInfo();
        String registerCode = registerInfo.getRegisterCode();
        if (TextUtils.isEmpty(registerCode)) {
            long time = registerInfo.getTime();
            if (time <= DateUtils.getDateAfter(new Date(), 0).getTime()) {
                exitApp();
            }
        } else if (!verify(registerCode)) {
            exitApp();
        }
    }

    /**
     * @throws
     * @Description: 校验
     * @param:
     * @return:
     * @author: zhangliangming
     * @date: 2018-06-02 13:11
     */
    private static boolean verify(String registerCode) throws Exception {
        String machineCode = createMachineCode();
        String verifyMachineCode = EncryptUtils.des(registerCode, mVerifyKey, Cipher.DECRYPT_MODE);
        return verifyMachineCode.equals(machineCode);
    }

    /**
     * 关闭应用
     */
    private static void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
