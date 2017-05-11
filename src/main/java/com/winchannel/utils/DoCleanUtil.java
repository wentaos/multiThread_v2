package com.winchannel.utils;

import com.winchannel.bean.Photo;
import com.winchannel.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cleanUtil")
public class DoCleanUtil {
    private static final Logger logger = LoggerFactory.getLogger(DoCleanUtil.class);

    @Autowired
    private PhotoService photoService;


    /**
     * 根据T_ID_POOL处理对应的图片资源
     */
    public boolean cleanPathHandler(long curr_id) {

        Photo photo = null;
        synchronized (DoCleanUtil.class){
            photo = photoService.getPhotoOne(curr_id);
        }

        if (photo == null) {
            return true;// 直接忽略
        }
        String absolutePath = photo.getImgAbsPath();
        String imgUrl = photo.getImgUrl();
        boolean containsDot2B = false;
        // 判断是否包含 dot2B
        if (imgUrl != null && imgUrl.contains("dot2B")) {// 说明是新数据
            containsDot2B = true;
        }

        // 根据Photo的IMG_ID 去查询其他表中的 FUNC_CODE信息
        String FUNC_CODE = "";
        synchronized (DoCleanUtil.class){
            FUNC_CODE = photoService.getFuncCodeByPhoto(photo);
        }

        // 获取到 FUNC_CODE
        if (FUNC_CODE != null && FUNC_CODE.length() > 0) {

            // 判断该Photo是否已经是一个符合规则的路径
            photo.setFuncCode(FUNC_CODE);
            if (CleanFileTool.isTruePath2(photo)) {
                // 如果是符合规则的路径，就继续下一个Photo
                return true;
            }

            // clean FUNC_CODE path 得到 D:/Photo_Test/photos/FUNC_CODE 这层目录
            @SuppressWarnings("unused")
            String funcCodeFullPath = CleanFileTool.cleanFuncCodePath(FUNC_CODE);

            // 处理日期目录  得到 D:/Photo_Test/photos/FUNC_CODE/2017-01-23 这层目录
            String date = CleanFileTool.cleanDatePath(FUNC_CODE, photo.getImgUrl());

            // 开始move文件
            // 在原绝对路径基础上加上FUNC_CODE目录
            String newAbsPath = "";
            // 移动文件到新目录
            // 注意：方法内需要对路径中的分隔符处理
            boolean moveFileOk = false;

            if (containsDot2B) {// 有绝对路径数据
                newAbsPath = CleanFileTool.getNewAbsPath(absolutePath, FUNC_CODE,date);
                moveFileOk = CleanFileTool.movePhoto(absolutePath, newAbsPath);
            } else {
                newAbsPath = CleanFileTool.getNewAbsPath(new String[]{imgUrl, FUNC_CODE,date});
                moveFileOk = CleanFileTool.movePhoto(new String[]{imgUrl, newAbsPath});
            }

            if (moveFileOk) {
                // 更新数据库:需要更新photo的 absolute_path 和 img_url
                String newImgUrl = CleanFileTool.getNewImgUrl(photo.getImgUrl(), FUNC_CODE,date);
                // TODO ？对于老数据绝对路径需不需要保存
                photo.setImgAbsPath(newAbsPath);// 修改绝对路径
                if(newImgUrl!=null){
                    photo.setImgUrl(newImgUrl);// 修改img_url
                }
                synchronized (DoCleanUtil.class){
                    photoService.updatePhoto(photo);
                }
            }
            return true;
        }
        return false;
    }

    public void wait(int million){
        try{
            Thread.sleep(million);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
