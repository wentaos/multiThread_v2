package com.winchannel.cleanTask;

import com.winchannel.cleanData.DistId;
import com.winchannel.cleanThread.CleanThread;
import com.winchannel.cleanUtil.IDPoolPropUtil;
import com.winchannel.cleanUtil.PropUtil;
import com.winchannel.service.PhotoService;
import com.winchannel.utils.IDInfoUtil;
import com.winchannel.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 新版本任务执行
 */
@Component
public class CleanTask {
    LogUtil logger;
    public CleanTask() {
        logger = new LogUtil(CleanTask.class).log(CleanTask.class);
    }

    @Autowired
    private PhotoService photoService;

    @Autowired
    private  DistId distId;


    /**
     * 当前线程数，必须
     */
    int THREAD_NUM = PropUtil.THREAD_NUM();
    /**
     * 上次程序运行线程数目
     */
    int HIS_THREAD_NUM = IDInfoUtil.getHIS_THREAD_NUM();

    /**
     * 获取上一次运行时最大ID:作为分配的起始点
     */
    Long HIS_MAX_ID = IDInfoUtil.getHIS_MAX_ID(HIS_THREAD_NUM);

    /**
     * 存放 ID_INFO 信息的文件路径配置，必须
     */
    String ID_INFO_PATH = PropUtil.ID_INFO_PATH();




    @Scheduled(cron = "${RUN_CRON}")
    public void cleanTask(){
        // 设置新的历史线程数
        IDPoolPropUtil.setHIS_THREAD_NUM(THREAD_NUM);
        // 需要探测是否配置了 ID_INFO_PATH
        if(ID_INFO_PATH==null || ID_INFO_PATH.trim().length()==0){
            throw new NoSuchFieldError("没有找到对应的 ID_INFO_PATH配置！");
        }

        /**
         * 根据当前线程数和历史线程数比较结果进行处理ID_POOL
         */
        if(HIS_THREAD_NUM==0){// 说明是第一次执行，直接创建线程运行即可
            Long endId = 0L;// 第一次运行就设置最小，具体的根据 baseQeury查询结果获取ID数据
            for (int i = 1; i <= THREAD_NUM; ++i) {
                String threadName = "Thread_"+i;
                CleanThread cleanThread = new CleanThread(threadName);
                // 分配ID_POOL
                List<Long> ID_POOL = distId.distIDPool(endId);
                cleanThread.setID_POOL(ID_POOL);
                cleanThread.start();
            }

        } else if (THREAD_NUM==HIS_THREAD_NUM){// 相等，直接分配
            // 到这里已经有了之前线程的ID_POOL数据，存放在ID_INFO/下的prop文件中
            // 获取之前的数据，分配给新线程
            for (int i = 1; i <= THREAD_NUM; ++i) {
                String threadName = "Thread_"+i;
                CleanThread cleanThread = new CleanThread(threadName);
                // 获取之前线程ID_POOL数据分配ID_POOL
                List<Long> ID_POOL = IDPoolPropUtil.getIdPoolByThreadName(threadName);// 得到上次执行剩余的ID_POOL数据
                cleanThread.setID_POOL(ID_POOL);
                cleanThread.start();
            }

        } else if (THREAD_NUM>HIS_THREAD_NUM){



        } else {// THREAD_NUM < HIS_THREAD_NUM



        }








    }












}
