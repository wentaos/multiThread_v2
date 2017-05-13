package com.winchannel.cleanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IDPoolUtil {


    /**
     * 将 ID_POOL 转成String 以便存储到 Properties中
     */
    public static String parseID_POOL2Str(int startIndex,List<Long> ID_POOL){
        StringBuffer id_pool_str = new StringBuffer();
        for (int index=startIndex;index<ID_POOL.size()-1;index++){
            id_pool_str.append(ID_POOL.get(index)).append("-");
        }
        id_pool_str.append(ID_POOL.get(ID_POOL.size()-1));
        return id_pool_str.toString();
    }

    public static String parseID_POOL2Str(List<Long> ID_POOL){
        return parseID_POOL2Str(0,ID_POOL);
    }


    /**
     * 将ID_POOL的String形式转成List<Long>
     */
    public static List<Long> parseStr2ID_POOL(String idpools){
        List<Long> ID_POOL = new ArrayList<Long>();
        if(idpools!=null && idpools.trim().length()>0){
            String[] idArr = idpools.split("-");
            for (int i=0;i<idArr.length;i++){
                Long id = Long.parseLong(idArr[i]);
                ID_POOL.add(id);
            }
        }
        return ID_POOL;
    }

    /**
     * 向List中批量添加元素
     */
    public static <T> void  addBatch(List<T> totalList,List<T> cList){
        for (T t:cList){
            totalList.add(t);
        }
    }



}
