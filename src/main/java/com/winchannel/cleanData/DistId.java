package com.winchannel.cleanData;

import com.winchannel.cleanUtil.IDPoolPropUtil;
import com.winchannel.cleanUtil.OptionPropUtil;
import com.winchannel.data.Memory;
import com.winchannel.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 新版本ID资源分发
 */
@Component("distId")
public class DistId {
    @Autowired
    private PhotoService photoService;


    /**
     * 每次给一个线程分配的ID数
     */
    private static int REDUCE_ID_NUM = OptionPropUtil.REDUCE_ID_NUM();

    /**
     * 如果ID资源分配完毕了，那么该值为true,接触到的线程需要停止分配资源了
     */
    public static boolean IS_STOP = false;




    /**
     * 分配资源
     * endId: 当前所有线程中记录的最大ID
     */
    public List<Long> distIDPool(Long endId) {
        List<Long> ID_POOL = null;
        synchronized(DistId.class){
            // 每次都需要查询一次baseQuerySql中的maxId
            Long queryMaxId = photoService.getMaxIdByBaseQuery();
            // 使用 select top REDUCE_ID_NUM ID from baseQuery where id>endId 这个sql需要在原baseQuerySql 基础上改装，加上 top REDUCE_ID_NUM
            ID_POOL = photoService.getNextIdPoolFromBaseQueryByEndId(endId);

            // 比较最大ID
            if(ID_POOL==null || ID_POOL.size()==0){// 说明没有获取到比endId再大的
                IS_STOP = true;
            }else if (queryMaxId<=ID_POOL.get(ID_POOL.size()-1)){// ID_POOL中已经获取到了最大ID
                IS_STOP = true;
            }/*else {
                IS_STOP = false;
            }*/
            // 将刚分配的最大ID作为当前已分配的最大ID
            Memory.DIST_MAX_ID = ID_POOL.get(ID_POOL.size()-1);
            // 分配后保存成历史最大ID
            IDPoolPropUtil.saveMaxIdPoint();
        }
        return ID_POOL;
    }




}
