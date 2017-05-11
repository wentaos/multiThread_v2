package com.winchannel.thread;


import com.winchannel.bean.IDInfo;
import com.winchannel.utils.DoCleanUtil;
import com.winchannel.utils.IDInfoUtil;
import com.winchannel.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("iDPoolCleanThread")
@Scope("prototype")// 多例
public class IDPoolCleanThread extends Thread {

    @Autowired
    private DoCleanUtil cleanUtil = (DoCleanUtil) SpringContextUtil.getBean("cleanUtil");


    private long[] id_pool;


    public IDPoolCleanThread() {
    }
    public IDPoolCleanThread(String name,long[] id_pool) {
        super(name);
        this.id_pool = id_pool;
    }

    public IDPoolCleanThread(String name,List<IDInfo> idInfoList) {
        super(name);
        List<Long> totalList = new ArrayList<Long>();
        this.id_pool = new long[idInfoList.size()];
        for (int i=0;i<idInfoList.size();i++){
            IDInfo info = idInfoList.get(i);
            List<Long> list = parseChildList(info.getCurrId(),info.getEndId());
            addAll(totalList,list);
        }
        this.id_pool = parseLongArr(totalList);
    }

    public IDPoolCleanThread(String name,List<IDInfo> idInfoList,long[] id_pools) {
        super(name);
        List<Long> totalList = new ArrayList<Long>();
        this.id_pool = new long[idInfoList.size()+id_pools.length];

        for (int i=0;i<idInfoList.size();i++){
            IDInfo info = idInfoList.get(i);
            List<Long> list = parseChildList(info.getCurrId(),info.getEndId());
            addAll(totalList,list);
        }
        this.id_pool = parseLongArr(totalList);
        int len0 = this.id_pool.length;
        for (int i=0;i<id_pools.length;i++){
            this.id_pool[len0+i] = id_pools[i];
        }
    }


    public List<Long> parseChildList(Long start,Long end){
        List<Long> list = new ArrayList<Long>();
        for (Long l=start;l<=end;l++){
            list.add(l);
        }
        return list;
    }

    public void addAll(List<Long> totalList,List<Long> childList){
        for (Long l:childList){
            totalList.add(l);
        }
    }

    public long[] parseLongArr(List<Long> totalList){
        long[] id_pools = new long[totalList.size()];
        for (int i=0;i<id_pools.length;i++){
            id_pools[i] = totalList.get(i);
        }
        return id_pools;
    }








    @Override
    public void run() {
        if(id_pool==null){
            return;
        }

        int id_pool_save_point_num = 0;
        // 初次运行先设置一次额数据
        IDInfoUtil.setID_POOL(buildID_POOL_STR(0,id_pool));
        // 处理对应ID的数据
        for (int i=0;i<id_pool.length;i++){
            long ID = id_pool[i];
            boolean cleanSHUN = cleanUtil.cleanPathHandler(ID);
            if (cleanSHUN){
                id_pool_save_point_num++;
            }
            cleanUtil.wait(100);
            if(id_pool_save_point_num % 10==0){
                IDInfoUtil.setID_POOL(buildID_POOL_STR(i+1,id_pool));
            }
        }
        // 完成之后清空
        IDInfoUtil.setID_POOL("");
    }


    public String buildID_POOL_STR(int startIndex,long[] id_pool){
        StringBuffer id_pool_str = new StringBuffer();
        for (int index=startIndex;index<id_pool.length-1;index++){
            id_pool_str.append(id_pool[index]).append("-");
        }
        id_pool_str.append(id_pool[id_pool.length-1]);
        return id_pool_str.toString();
    }

    public String buildID_POOL_STR(long[] id_pool){
        StringBuffer id_pool_str = new StringBuffer();
        for (int index=0;index<id_pool.length-1;index++){
            id_pool_str.append(id_pool[index]).append("-");
        }
        id_pool_str.append(id_pool[id_pool.length-1]);
        return id_pool_str.toString();
    }





    public long[] getId_pool() {
        return id_pool;
    }

    public void setId_pool(long[] id_pool) {
        this.id_pool = id_pool;
    }

}
