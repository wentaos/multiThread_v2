package com.winchannel.utils;

import com.sun.media.sound.DLSInfo;
import com.winchannel.bean.IDInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class IDInfoUtil {

    private static String resourceFile = "ID_INFO.properties";

    private static String ID_INFO_PATH = PropUtil.ID_INFO_PATH();

    private static String resourceFilePath = ID_INFO_PATH+resourceFile;
    private static Properties prop = new Properties();

    private static boolean isPathExists = false;

    // 检查文件是否存在
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
    public static void saveIDInfoPoint(IDInfo idInfo){
        String threadName=idInfo.getThreadName();
        long startId = idInfo.getStartId();
        long currId = idInfo.getCurrId();
        long endId = idInfo.getEndId();
        String key = threadName;
        String value = startId+"-"+currId+"-"+endId;
        // 设置数据
        setValue(key,value);
    }


    /**
     * 获取对应的IDInfo
     * hisThreadNum:上次线程数
     */
    public static List<IDInfo> getIDInfoListFromProp(int hisThreadNum){
        List<IDInfo> idInfoList = new ArrayList<IDInfo>();
        for(int i=1;i<=hisThreadNum;i++){
            String threaNmae = "Thread-"+i;// 保存时的Key就是线程名称
            String value = getValue(threaNmae);
            if(value==null || value.trim().length()==0){
                continue;
            }
            String[] idInfos = value.split("-");
            if(idInfos!=null && idInfos.length==3){
                IDInfo idInfo = new IDInfo();
                idInfo.setThreadName(threaNmae)
                        .setStartId(Long.parseLong(idInfos[0]))
                        .setCurrId(Long.parseLong(idInfos[1]))
                        .setEndId(Long.parseLong(idInfos[2]));
                idInfoList.add(idInfo);
            }
        }
        return idInfoList;
    }






    /**
     * 核心方法
     * 获取属性值
     */
    public static String getValue(String key){
        String value = null;
        try{
            InputStream in = PropUtil.class.getClassLoader().getResourceAsStream(resourceFilePath);
            prop.load(in);
            value = prop.getProperty(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    public static void setValue(String key,String value){
        try{
            String path = PropUtil.class.getClassLoader().getResource(resourceFilePath).getPath();
            OutputStream out = new FileOutputStream(path,false);
            prop.setProperty(key,value);
            prop.store(out,key);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
