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

    @Override
    public void updatePhotoImgId(long ID) {
        photoDao.updatePhotoImgId(ID);
    }
}
