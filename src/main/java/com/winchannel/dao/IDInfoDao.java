package com.winchannel.dao;

import com.winchannel.bean.Photo;

/**
 * Created by Wator on 2017/5/7.
 */
public interface IDInfoDao {
    /**
     * 第一次运行时保存
     */
    void insertInfoPhoto(Photo prop);

    /**
     * 修改里面的数据
     */
    void updateInfoPhoto(Photo prop);

    /**
     * 获取报错ID、线程信息的Photo
     * 使用一个固定的标识：img_id,这个属性值我们自己定义
     */
    Photo selectInfoPhoto();

}
