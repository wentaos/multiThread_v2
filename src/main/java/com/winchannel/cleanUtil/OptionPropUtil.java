package com.winchannel.cleanUtil;

import com.winchannel.data.Constant;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class OptionPropUtil {

    /**
     * 资源文件
     */
    private static String resourceFilePath = "spring/config/option.properties";
    private static Properties prop = new Properties();


    /**
     * THREAD_NUM
     * 线程数
     */
    public static int THREAD_NUM(){
        int def = 10;// 默认使用10个线程
        try{
            String THREAD_NUM = getValue(Constant.THREAD_NUM);
            if (THREAD_NUM!=null && THREAD_NUM.trim().length()>0){
                return Integer.parseInt(THREAD_NUM);
            }
            return def;
        }catch (Exception e){
            e.printStackTrace();
            return def;
        }
    }


    /**
     * PHOTO_PATH
     * 获取
     */
    public static String PHOTO_PATH(){
        try{
            String PHOTO_PATH = getValue(Constant.PHOTO_PATH);
            return PHOTO_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RUN_TASK_TIME_LEN
     * 定时任务运行时长
     */
    public static String RUN_TASK_TIME_LEN(){
        try{
            String RUN_TASK_TIME_LEN = getValue(Constant.RUN_TASK_TIME_LEN);
            return RUN_TASK_TIME_LEN;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * REDUCE_ID_NUM
     * 一个线程一次处理 的ID数
     * */
    public static int REDUCE_ID_NUM(){
        try{
            String REDUCE_ID_NUM = getValue(Constant.REDUCE_ID_NUM);
            if(REDUCE_ID_NUM!=null){
                return Integer.parseInt(REDUCE_ID_NUM);
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * IS_TEST
     * 是否是测试
     */
    public static boolean IS_TEST(){
        try{
            String IS_TEST = getValue(Constant.IS_TEST);
            if (IS_TEST!=null){
                return "1".equals(IS_TEST) ? true : false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }


    /**
     * DB_TYPE
     * 数据库类型
     */
    public static String DB_TYPE(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE);
            return DB_TYPE;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * LOOP_SAVE_COUNT
     * 遍历多少条记录保存一次ID信息:数据越小越利于重启连续；过小也会增加数据库update访问次数
     */
    public static int LOOP_SAVE_COUNT(){
        int def = 100;
        try{
            String LOOP_SAVE_COUNT = getValue(Constant.LOOP_SAVE_COUNT);
            if(LOOP_SAVE_COUNT!=null && LOOP_SAVE_COUNT.trim().length()>0){
                return Integer.parseInt(LOOP_SAVE_COUNT);
            }
            return def;// 默认100
        }catch (Exception e){
            e.printStackTrace();
            return def;
        }
    }


    /**
     * IS_MYSQL
     */
    public static boolean IS_MYSQL(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.MYSQL.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * IS_SQLSERVER
     */
    public static boolean IS_SQLSERVER(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.SQLSERVER.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ORACLE
     */
    public static boolean IS_ORACLE(){
        try{
            String DB_TYPE = getValue(Constant.DB_TYPE).trim();
            return Constant.ORACLE.equals(DB_TYPE)? true:false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ID_INFO_PATH
     * 存放 ID_INFO 的主目录
     * 如果是Windows D:/xxx/  如果是Linux 直接写 /usr/xxx/
     */
    public static String ID_INFO_PATH(){
        try{
            String ID_INFO_PATH = getValue(Constant.ID_INFO_PATH);
            return ID_INFO_PATH;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * IS_FUNC_CODE_PART
     * 是否处理一部分 FUNC_CODE
     */
    public static boolean IS_FUNC_CODE_PART(){
        try{
            String IS_FUNC_CODE_PART = getValue(Constant.IS_FUNC_CODE_PART);
            if(IS_FUNC_CODE_PART!=null && IS_FUNC_CODE_PART.trim().length()>0){
                return Boolean.parseBoolean(IS_FUNC_CODE_PART.trim().toLowerCase());
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * FUNC_CODE_LIST
     * 获取 FUNC_CODE_LIST
     */
    public static String[] FUNC_CODE_LIST(){
        String[] funcodes = null;
        try{
            String FUNC_CODE_LIST = getValue(Constant.FUNC_CODE_LIST);
            if(FUNC_CODE_LIST!=null && FUNC_CODE_LIST.trim().length()>0){
                funcodes = FUNC_CODE_LIST.trim().split(",");
            }
            return funcodes;
        }catch (Exception e){
            e.printStackTrace();
            return funcodes;
        }
    }


















    /**
     * 核心方法
     * 获取属性值
     */
    public static String getValue(String key){
        String value = null;
        try{
            InputStream in = OptionPropUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
            prop.load(in);
            value = prop.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static void setValue(String key,String value){
        try{
            String path = OptionPropUtil.class.getClassLoader().getResource(resourceFilePath).getPath();
            OutputStream out = new FileOutputStream(path,false);
            prop.setProperty(key,value);
            prop.store(out, key);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
