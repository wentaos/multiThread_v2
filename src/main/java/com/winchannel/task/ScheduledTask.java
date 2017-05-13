package com.winchannel.task;

import com.winchannel.bean.IDInfo;
import com.winchannel.data.DistResource;
import com.winchannel.data.Memory;
import com.winchannel.service.PhotoService;
import com.winchannel.thread.DoCleanThread;
import com.winchannel.thread.IDPoolCleanThread;
import com.winchannel.utils.IDInfoUtil;
import com.winchannel.utils.LogUtil;
import com.winchannel.cleanUtil.OptionPropUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTask {

    LogUtil logger = new LogUtil(ScheduledTask.class).log(ScheduledTask.class);

    @Autowired
    private PhotoService photoService;

//    Calendar calendar = Calendar.getInstance();

    // 线程数:默认10
    int THREAD_NUM = OptionPropUtil.THREAD_NUM();
    // 存放 ID_INFO 信息的文件路径配置
    String ID_INFO_PATH = OptionPropUtil.ID_INFO_PATH();

    // 上次程序运行线程数目
    int HIS_THREAD_NUM = IDInfoUtil.getHIS_THREAD_NUM();



    // 获取上一次运行时最大ID:作为分配的起始点
    Long HIS_MAX_ID = IDInfoUtil.getHIS_MAX_ID(HIS_THREAD_NUM);

    @Scheduled(cron = "${RUN_CRON}")
    public void cleanFileDirTask() {
        logger.info("INFO");

        // 将历史最大ID设置为当前最大ID
        Memory.DIST_MAX_ID = HIS_MAX_ID;

        // 设置新的历史线程数
        IDInfoUtil.setHIS_THREAD_NUM(THREAD_NUM);

        // 需要探测是否配置了 ID_INFO_PATH
        if(ID_INFO_PATH==null || ID_INFO_PATH.trim().length()==0){
            throw new NoSuchFieldError("没有找到对应的 ID_INFO_PATH配置！");
        }


        // 需要判断当前使用的线程数 和 历史线程数
        // 这种情况：有历史线程ID信息，可以将历史ID信息完全分配给当先的线程，不需要创建 IDPool线程
        if (THREAD_NUM >= HIS_THREAD_NUM && HIS_THREAD_NUM!=0){
            for (int i = 1; i <= THREAD_NUM; ++i) {
                String threadName = "Thread-"+i;
                DoCleanThread doCleanThread = new DoCleanThread(threadName);
                IDInfo idInfo = new IDInfo();
                if(i <= HIS_THREAD_NUM){// 分配历史数据
                    idInfo = IDInfoUtil.getIDInfo(threadName);
                }else {
                    // 这里：已经分配完了历史的ID信息，接下来根据历史的最大ID进行最新的分配
                    Memory.DIST_MAX_ID = HIS_MAX_ID;// 设置当前最大ID
                    // 分配之前查询MAX_ID
                    synchronized (ScheduledTask.class){
                        long MAX_ID = photoService.getPhotoMaxId();
                        Memory.MAX_ID = MAX_ID;
                        DistResource.distId(idInfo);
                    }
                }
                doCleanThread.setIdInfo(idInfo);// 为线程设置ID组daz
                doCleanThread.start();
            }

            // 检查之前是否有剩下的 ID_POOL 信息，有的话需要将之前的ID_POOL信息拼接在一起使用
            long[] id_pools = IDInfoUtil.getID_POOL();
            // 说明之前就存在剩下的数据
            if(id_pools!=null && id_pools.length>0){
                // 创建IDPOOL线程，同时有了需要处理的数据
                IDPoolCleanThread idPoolCleanThread = new IDPoolCleanThread("Thread-ID_POOL",id_pools);
                idPoolCleanThread.start();
            }

        } else if(HIS_THREAD_NUM==0){// 说明是第一次运行，没有历史线程信息
            // 分配之前查询MAX_ID
            synchronized (ScheduledTask.class){
                long MAX_ID = photoService.getPhotoMaxId();
                Memory.MAX_ID = MAX_ID;
            }
            for (int i = 1; i <= THREAD_NUM; ++i) {
                String threadName = "Thread-"+i;
                DoCleanThread doCleanThread = new DoCleanThread(threadName);
                IDInfo idInfo = new IDInfo();
                // 线程首次分配
                DistResource.distId(idInfo);
                doCleanThread.setIdInfo(idInfo);// 为线程设置ID组
                doCleanThread.start();
            }
        } else {// 这种情况：历史的ID信息分配不完，需要将剩下的ID信息交给 IDPool线程处理

            for (int i = 1; i <= THREAD_NUM; ++i) {
                String threadName = "Thread-"+i;
                DoCleanThread doCleanThread = new DoCleanThread(threadName);
                IDInfo idInfo = IDInfoUtil.getIDInfo(threadName);
                doCleanThread.setIdInfo(idInfo);// 为线程设置ID组
                doCleanThread.start();
            }

            // 检查之前是否有剩下的 ID_POOL 信息，有的话需要将之前的ID_POOL信息拼接在一起使用
            long[] id_pools = IDInfoUtil.getID_POOL();
            // 说明之前就存在剩下的数据
            if(id_pools!=null && id_pools.length>0){
                // 得到剩下的需要ID_POOL线程处理的ID信息
                List<IDInfo> idInfoList = IDInfoUtil.getIDInfoListFromProp(THREAD_NUM+1,HIS_THREAD_NUM);
                // 创建IDPOOL线程，同时有了需要处理的数据
                IDPoolCleanThread idPoolCleanThread = new IDPoolCleanThread("Thread-ID_POOL",idInfoList,id_pools);
                idPoolCleanThread.start();
            }else{
                // 得到剩下的需要ID_POOL线程处理的ID信息
                List<IDInfo> idInfoList = IDInfoUtil.getIDInfoListFromProp(THREAD_NUM+1,HIS_THREAD_NUM);
                // 创建IDPOOL线程，同时有了需要处理的数据
                IDPoolCleanThread idPoolCleanThread = new IDPoolCleanThread("Thread-ID_POOL",idInfoList);
                idPoolCleanThread.start();
            }




        }

    }



}
