package com.winchannel.utils;

import com.winchannel.bean.IDInfo;
import com.winchannel.cleanUtil.OptionPropUtil;
import com.winchannel.data.Constant;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class IDInfoUtil {

    private static String resourceFile = "ID_INFO.properties";

    private static String ID_INFO_PATH = OptionPropUtil.ID_INFO_PATH();

    private static String resourceFilePath = ID_INFO_PATH+resourceFile;
    private static Properties prop = new Properties();

    private static boolean isPathExists = false;

    /**
     * 检查文件是否存在
      */
    static {
        File path = new File(ID_INFO_PATH);
        if(!path.exists()){// 目录不存在
            path.mkdirs();
            isPathExists = true;
        } else if(path.exists() && !path.isDirectory()){
            throw new RuntimeException(ID_INFO_PATH+"不是目录，请重新配置！");
        }

        File propFile = new File(resourceFilePath);
        if(!propFile.exists()){
            try {
                boolean createSuccess = propFile.createNewFile();
                if(!createSuccess){
                    throw new RuntimeException(resourceFilePath+" 文件创建失败！");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 记录每个线程的IDInfo数据
     */
    public static boolean saveIDInfoPoint(IDInfo idInfo){
        String threadName=idInfo.getThreadName();
        long startId = idInfo.getStartId();
        long currId = idInfo.getCurrId();
        long endId = idInfo.getEndId();
        String key = threadName;
        String value = startId+"-"+currId+"-"+endId;
        // 设置数据
        return setValue(key,value);
    }


    /**
     * 获取一个IDInfo
     */
    public static IDInfo getIDInfo(String threadName){
        String value = getValue(threadName);
        if(value==null || value.trim().length()==0){
            return null;
        }
        String[] idInfos = value.split("-");
        IDInfo idInfo = new IDInfo();
        if(idInfos!=null && idInfos.length==3) {
            idInfo.setThreadName(threadName)
                    .setStartId(Long.parseLong(idInfos[0]))
                    .setCurrId(Long.parseLong(idInfos[1]))
                    .setEndId(Long.parseLong(idInfos[2]));
        }
        return idInfo;
    }


    /**
     * 根据历史信息得到最大ID
     */
    public static Long getHIS_MAX_ID(int hisThreadNum){
        List<IDInfo> list = getIDInfoListFromProp(hisThreadNum);
        if(list!=null && list.size()>0){
            for (int i=0;i<list.size()-1;i++){
                for (int j=i+1;j<list.size();j++){
                    IDInfo info1 = list.get(i);
                    IDInfo info2 = list.get(j);
                    long l1 = info1.getEndId();
                    long l2 = info2.getEndId();
                    if(l2>l1){
                        IDInfo temp = info1;
                        list.set(i,info2);
                        list.set(j,temp);
                    }
                }
            }
            return list.get(0).getEndId();
        }
        return null;
    }



    /**
     * 获取对应的IDInfo
     * hisThreadNum:上次线程数
     */
    public static List<IDInfo> getIDInfoListFromProp(int hisThreadNum){
        return getIDInfoListFromProp(1,hisThreadNum);
    }

    public static List<IDInfo> getIDInfoListFromProp(int start,int end){
        List<IDInfo> idInfoList = new ArrayList<IDInfo>();
        for(int i=start;i<=end;i++){
            String threadName = "Thread-"+i;// 保存时的Key就是线程名称
            String value = getValue(threadName);
            if(value==null || value.trim().length()==0){
                continue;
            }
            String[] idInfos = value.split("-");
            if(idInfos!=null && idInfos.length==3){
                IDInfo idInfo = new IDInfo();
                idInfo.setThreadName(threadName)
                        .setStartId(Long.parseLong(idInfos[0]))
                        .setCurrId(Long.parseLong(idInfos[1]))
                        .setEndId(Long.parseLong(idInfos[2]));
                idInfoList.add(idInfo);
            }
        }
        return idInfoList;
    }



    /**
     * 设置当前线程数
     */
    public  static boolean setHIS_THREAD_NUM(int HIS_THREAD_NUM){
        return setValue(Constant.HIS_THREAD_NUM,HIS_THREAD_NUM+"");
    }




    /**
     * 设置ID_POOL数据
     */
    public  static boolean setID_POOL(String id_pool){
        return setValue(Constant.ID_POOL,id_pool);
    }

    /**
     * 获取 ID_POOL
     */
    public static long[] getID_POOL(){
        long[] ID_POOL = null;
        String ID_POOL_STR = getValue(Constant.ID_POOL);
        if(ID_POOL_STR!=null && ID_POOL_STR.trim().length()>0){
            String[] id_pool = ID_POOL_STR.split("-");
            ID_POOL = new long[id_pool.length];
            for(int i=0;i<id_pool.length;i++){
                ID_POOL[i] = Long.parseLong(id_pool[i]);
            }
        }
        return ID_POOL;
    }














    /**
     * 核心方法
     * 获取属性值
     */
    public static String getValue(String key){
        String value = null;
        try{
            InputStream in = new FileInputStream(resourceFilePath);
            prop.load(in);
            value = prop.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static boolean setValue(String key,String value){
        try{
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
