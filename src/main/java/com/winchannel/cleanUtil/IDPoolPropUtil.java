package com.winchannel.cleanUtil;

import com.winchannel.data.Constant;
import com.winchannel.data.Memory;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * 处理ID_INFO 目录下的ID_POOL 配置文件数据
 */
public class IDPoolPropUtil {

    private static String propType = ".properties";
    private static String ID_INFO_PATH = OptionPropUtil.ID_INFO_PATH();

    /**
     * 将当前线程数 设置为 最新历史线程
     */
    public  static boolean setHIS_THREAD_NUM(int HIS_THREAD_NUM){
        String resourceFile = ID_INFO_PATH+Constant.HIS_THREAD_NUM+propType;
        return setValue(resourceFile,Constant.HIS_THREAD_NUM,HIS_THREAD_NUM+"");
    }

    /**
     * 获取历史线程数
     */
    public  static int getHIS_THREAD_NUM(){
        String resourceFile = ID_INFO_PATH+Constant.HIS_THREAD_NUM+propType;
        String value = getValue(resourceFile,Constant.HIS_THREAD_NUM);
        if(value!=null && value.trim().length()>0){
            return Integer.parseInt(value);
        }
        return 0;
    }







    /**
     * 保存 ID_POOL 到对应的Prop文件
     */
    public static boolean saveID_POOL(String threadName,Integer startIndex,List<Long> ID_POOL){
        String id_pools = null;
        if (ID_POOL==null){// 这是需要清空数据的标识，因为一个ID_POOL被处理好了
            id_pools = "";
        } else {
            id_pools = IDPoolUtil.parseID_POOL2Str(startIndex,ID_POOL);
        }
        String propName = threadName+"_ID_POOL"+propType;// eg: Thread_1_ID_POOL.properties
        String idpoolResourcePath = ID_INFO_PATH+propName;
        // 先判断文件是否存在
        File idpoolProp = new File(idpoolResourcePath);
        if (!idpoolProp.exists()){
            try {
                idpoolProp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return setValue(idpoolResourcePath, threadName + "_ID_POOL", id_pools);
    }

    /**
     * 根据线程名称获取对应的历史线程的ID_POOL
     */
    public static List<Long> getIdPoolByThreadName(String threadName){
        String propName = threadName+"_ID_POOL"+propType;// eg: Thread_1_ID_POOL.properties
        String idpoolResourcePath = ID_INFO_PATH+propName;
        String idpools = getValue(idpoolResourcePath,threadName+"_ID_POOL");
        return IDPoolUtil.parseStr2ID_POOL(idpools);
    }


    /**
     * 最大ID记录和获取操作
     */
    public static boolean saveMaxIdPoint(){
        long distMaxId = Memory.DIST_MAX_ID;
        // 文件存储路径
        String maxIdFilePath = ID_INFO_PATH+"HIS_MAX_ID"+propType;
        File maxIdFile = new File(maxIdFilePath);
        if (!maxIdFile.exists()){
            try {
                maxIdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setValue(maxIdFilePath,"HIS_MAX_ID",distMaxId+"");
        return true;
    }

    /**
     * 获取历史最大ID
     */
    public static Long getHisMaxId(){
        // 文件存储路径
        String maxIdFilePath = ID_INFO_PATH+"HIS_MAX_ID"+propType;
        File maxIdFile = new File(maxIdFilePath);
        if(!maxIdFile.exists()){
            return null;
        }
        String maxIdStr = getValue(maxIdFilePath,"HIS_MAX_ID");
        if (maxIdStr!=null && maxIdStr.trim().length()>0){
            return Long.parseLong(maxIdStr);
        }
        return null;
    }


















    /**
     * 核心方法
     * 获取属性值
     */
    public static String getValue(String resourceFilePath,String key){
        String value = null;
        try{
            Properties prop = new Properties();
            InputStream in = new FileInputStream(resourceFilePath);
            prop.load(in);
            value = prop.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static boolean setValue(String resourceFilePath,String key,String value){
        try{
            Properties prop = new Properties();
            OutputStream out = new FileOutputStream(resourceFilePath,false);
            prop.setProperty(key,value);
            prop.store(out,key);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
