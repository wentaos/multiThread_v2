package com.winchannel.data;

import com.winchannel.bean.IDInfo;
import com.winchannel.utils.PropUtil;


public class DistResource {

    private static int REDUCE_ID_NUM = PropUtil.REDUCE_ID_NUM();

    public static boolean IS_STOP = false;

    /**
     * 分配ID
     */
    public static void distId(IDInfo idInfo){
        // 这里需要每次查询一次最大ID
        long max_id = Memory.MAX_ID;// 现在是写固定的
        // 分配资源时保证线程安全
        synchronized (DistResource.class){
            if(Memory.isDist()){// 可以分配：检查endId 是否 <= maxId
                // 将之前的当前最大ID作为现在的初始ID
                Long CURR_MAX_ID = Memory.CURR_MAX_ID;
                idInfo.setStartId(CURR_MAX_ID + 1);
                idInfo.setCurrId(CURR_MAX_ID + 1);
                if(max_id-REDUCE_ID_NUM>CURR_MAX_ID){
                    idInfo.setEndId(CURR_MAX_ID+REDUCE_ID_NUM);
                }else{
                    idInfo.setEndId(max_id);
                    IS_STOP = true;
                }

                // 重新设置CURR_MAX_ID
                Memory.CURR_MAX_ID = idInfo.getEndId();
            }

        }
    }




}
