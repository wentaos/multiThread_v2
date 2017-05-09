package com.winchannel.thread;


import com.winchannel.bean.IDInfo;
import com.winchannel.data.DistResource;
import com.winchannel.service.IDInfoService;
import com.winchannel.service.impl.IDInfoServiceImpl;
import com.winchannel.utils.DoCleanUtil;
import com.winchannel.utils.PropUtil;
import com.winchannel.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("cleanThread")
@Scope("prototype")// 多例
public class DoCleanThread extends Thread {

    @Autowired
    private IDInfoService idInfoService = (IDInfoServiceImpl)SpringContextUtil.getBean("idInfoService");

    @Autowired
    private DoCleanUtil cleanUtil = (DoCleanUtil)SpringContextUtil.getBean("cleanUtil");

    /**
     * ID信息
     */
    private IDInfo idInfo;
    /**
     * 是否完成
     */
    private boolean isComplete;
    /**
     * 是否结束
     */
    private boolean isStop;

    /**
     * 两个构造函数
     */
    public DoCleanThread(){super();}
    public DoCleanThread(String name){super(name);}


    /**
     * 核心方法
     */
    @Override
    public void run(){
        // 一个轮询完成标识
        this.isComplete = false;
        // 线程停止标识
        this.isStop = false;

        if (this.idInfo==null){
            throw new RuntimeException("Thread IDInfo IS NULL!");
        }

        while (!this.isStop){

            this.idInfo.setThreadName(this.getName());
            long start = this.idInfo.getCurrId();
            long end = this.idInfo.getEndId();
            // 遍历ID
            for (long id=start;id<=end;id++){
                // 处理图片、ID数据
                boolean cleanSHUN = cleanUtil.cleanPathHandler(id);

                System.out.println(this.getName()+" 完成第" + id + "个数据!");
                cleanUtil.wait(100);
                // 每完成一个，修改一次 idInfo中信息
                idInfo.setCurrId(id);
                // 检查LOOP次数 是否满100*N，是则update ID信息
                if(id%PropUtil.LOOP_SAVE_COUNT()==0){

                }
            }

            this.isComplete = true;

            if (DistResource.IS_STOP){// 分配完结
                this.isComplete = false;
                this.isStop = true;
            }

            if (this.isComplete){
                // 再次分配ID资源
                DistResource.distId(this.idInfo);
            }

        }


    }














    public IDInfo getIdInfo() {
        return idInfo;
    }

    public void setIdInfo(IDInfo idInfo) {
        this.idInfo = idInfo;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setIsStop(boolean isStop) {
        this.isStop = isStop;
    }
}
