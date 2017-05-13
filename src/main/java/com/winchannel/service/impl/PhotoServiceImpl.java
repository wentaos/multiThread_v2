package com.winchannel.service.impl;

import com.winchannel.bean.Photo;
import com.winchannel.dao.IDInfoDao;
import com.winchannel.dao.PhotoDao;
import com.winchannel.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service("photoService")
public class PhotoServiceImpl implements PhotoService{

    @Autowired
    private PhotoDao photoDao;


    // selectNextIdPoolFromBaseQueryByEndId
    /********************新版本增加功能**************************/
    @Override
    public List<Photo> getPhotoListByBaseQuery() {
        return photoDao.selectPhotoListByBaseQuery();
    }

    @Override
    public Long getMaxIdByBaseQuery() {
        return photoDao.selectMaxIdByBaseQuery();
    }

    /**
     * 最新使用的查询FUNC_CODE方法
     * @param photo
     * @return
     */
    @Override
    public String getFuncCodeByXml(Photo photo){
        String fc = null;
        if(photo!=null){
            fc = photoDao.getFunCodeByPhoto(photo);
        }
        return fc;
    }


    /**
     * 获取下一ID_POOL 数据
     * @param endId
     * @return
     */
    @Override
    public List<Long> getNextIdPoolFromBaseQueryByEndId(Long endId) {
        return photoDao.selectNextIdPoolFromBaseQueryByEndId(endId);
    }


    @Override
    public List<Long> getPhotoIdByFcQuerys() {




        return null;
    }




    /********************原有功能**************************/

    @Override
    public Photo getPhotoOne(long id) {
        Photo photo = photoDao.selectPhotoOne(id);
        return photo;
    }

    @Override
    public Photo getFirstPhotoOne() {
        List<Photo> photoList = photoDao.selectPhotoList();
        if(photoList!=null && photoList.size()>0){
            return photoList.get(0);
        }
        return null;
    }


    @Deprecated
    @Override
    public String getFuncCodeByPhoto(Photo photo) {
        String fc = null;
        if(photo!=null){
            String imgId = photo.getImgId();
            fc = photoDao.getFuncCodeByImgId(imgId);
        }
        return fc;
    }






    @Override
    public boolean updatePhoto(Photo photo) {
        try{
            int record = photoDao.updatePhoto(photo);
            if(record>=1){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getPhotoMinId() {
        try{
            Long maxId = photoDao.selectPhotoMinId();
            return maxId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getPhotoMaxId() {
        try{
            Long maxId = photoDao.selectPhotoMaxId();
            return maxId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }











    /**
     * 测试方法
     * @param ID
     */
    @Override
    public void updatePhotoImgId(long ID) {
        photoDao.updatePhotoImgId(ID);
    }
}
