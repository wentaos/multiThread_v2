package com.winchannel.service.impl;

import com.winchannel.bean.Photo;
import com.winchannel.dao.IDInfoDao;
import com.winchannel.service.IDInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("idInfoService")
public class IDInfoServiceImpl implements IDInfoService {

    @Autowired
    private IDInfoDao idInfoDao;

    @Override
    public void saveInfoPhoto(Photo prop) {
        if(prop!=null){
            idInfoDao.insertInfoPhoto(prop);
        }
    }

    @Override
    public void updateInfoPhoto(Photo prop) {
        if (prop!=null){
            idInfoDao.updateInfoPhoto(prop);
        }
    }

    @Override
    // TODO
    public Photo getInfoPhoto() {
        return null;
    }
}
