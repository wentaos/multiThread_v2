package com.winchannel.task;

import com.winchannel.bean.IDInfo;
import com.winchannel.data.DistResource;
import com.winchannel.thread.DoCleanThread;
import com.winchannel.utils.LogUtil;
import com.winchannel.utils.PropUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class ScheduledTask {

    LogUtil logger = new LogUtil(ScheduledTask.class).log(ScheduledTask.class);


    Calendar calendar = Calendar.getInstance();

    // 线程数:默认10
    int THREAD_NUM = PropUtil.THREAD_NUM();
    // 存放 ID_INFO 信息的文件路径配置
    String ID_INFO_PATH = PropUtil.ID_INFO_PATH();

    @Scheduled(cron = "0 47 11 * * ?")
    public void cleanFileDirTask() {
        logger.info("INFO");

        // 需要探测是否配置了 ID_INFO_PATH
        if(ID_INFO_PATH==null || ID_INFO_PATH.trim().length()==0){
            throw new NoSuchFieldError("没有找到对应的 ID_INFO_PATH配置！");
        }

        for (int i = 1; i <= THREAD_NUM; ++i) {
            String threadName = "Thread"+i;
            DoCleanThread doCleanThread = new DoCleanThread(threadName);
            IDInfo idInfo = new IDInfo();
            // 进行第一次资源检查分配
            DistResource.distId(idInfo);
            doCleanThread.setIdInfo(idInfo);// 为线程设置ID组
            doCleanThread.start();
        }

    }



}
