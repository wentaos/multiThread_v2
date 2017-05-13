package com.winchannel.cleanThread;

import com.winchannel.cleanData.DistId;
import com.winchannel.cleanUtil.IDPoolPropUtil;
import com.winchannel.cleanUtil.OptionPropUtil;
import com.winchannel.data.Memory;
import com.winchannel.service.PhotoService;
import com.winchannel.service.impl.PhotoServiceImpl;
import com.winchannel.utils.DoCleanUtil;
import com.winchannel.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 新版本Clean线程
 */
@Component("cleanThread")
@Scope("prototype")// 多例
public class CleanThread extends Thread{
    @Autowired
    private PhotoService photoService = (PhotoServiceImpl)SpringContextUtil.getBean("photoService");
    @Autowired
    private DoCleanUtil cleanUtil = (DoCleanUtil) SpringContextUtil.getBean("cleanUtil");
    @Autowired
    private  DistId distId = (DistId) SpringContextUtil.getBean("distId");
    /**
     * ID_POOL
     */
    public List<Long> ID_POOL = null;

    /**
     * 是否完成一个轮询
     */
    private boolean isComplete;
    /**
     * 是否结束
     */
    private boolean isStop;

    /**
     * run() 中处理多少记录进行一次saveID_POOL()操作
     */
    private int LOOP_SAVE_COUNT
            = OptionPropUtil.LOOP_SAVE_COUNT();


    public CleanThread(){super();}
    public CleanThread(String name){
        super(name);
    }
    public CleanThread(String name,List<Long> ID_POOL) {
        super(name);
        this.ID_POOL = ID_POOL;
    }


    @Override
    public void run(){
        // 一个轮询完成标识
        this.isComplete = false;
        // 线程停止标识
        this.isStop = false;

        while (!this.isStop){
            String threadName = this.getName();
            // 进入到这里ID_POOL都应该有数据
            if(ID_POOL==null){
                return;// 直接结束掉线程
            }
            // 开始执行前将分配的ID记录到 Prop文件run()中首次保存，从0开始
            IDPoolPropUtil.saveID_POOL(threadName,0,ID_POOL);
            int id_pool_save_point_num = 0;
            // 根据ID_POOL进行处理数据
            for (int i=0;i<ID_POOL.size();i++) {
                long ID = ID_POOL.get(i);
                boolean cleanSHUN = cleanUtil.cleanPathHandler(ID);
                if (cleanSHUN) {
                    id_pool_save_point_num++;
                }
                if (id_pool_save_point_num % LOOP_SAVE_COUNT == 0) {
                    IDPoolPropUtil.saveID_POOL(threadName, i+1, ID_POOL);
                    id_pool_save_point_num = 0;// 重新计算
                }
            }
            // 处理好一个ID_POOL轮询保存一次ID_POOL，记录信息表示已处理好了这次ID_POOL
            IDPoolPropUtil.saveID_POOL(threadName, null, null);// ID_POOL设置为null，为记录数据为空值

            // 后面需要进行分配ID_POOL
            this.isComplete = true;

            if (DistId.IS_STOP){// 分配完结
                this.isComplete = false;
                this.isStop = true;
                IDPoolPropUtil.saveID_POOL(threadName, null, null);// ID_POOL设置为null，为记录数据为空值
            }

            if (this.isComplete){
                // 再次分配ID资源
                synchronized (CleanThread.class){
//                    Memory.MAX_ID = photoService.getPhotoMaxId();
//                    DistResource.distId(this.idInfo);// 内部设置 CURR_MAX_ID
//                    IDInfoUtil.saveIDInfoPoint(idInfo);
                    // 获取baseQuery中的最大ID
                    Memory.MAX_ID = photoService.getMaxIdByBaseQuery();
                    // 分配新的ID_POOL
                    List<Long> ID_POOL = distId.distIDPool(Memory.DIST_MAX_ID);
                    this.setID_POOL(ID_POOL);// 为线程重新设置 ID_POOL
                }
            }
        }


    }















    public List<Long> getID_POOL() {
        return ID_POOL;
    }

    public void setID_POOL(List<Long> ID_POOL) {
        this.ID_POOL = ID_POOL;
    }
}
