package com.winchannel.service;


import com.winchannel.bean.Photo;

import java.sql.SQLException;
import java.util.List;

public interface PhotoService {
    Photo getPhotoOne(long id);

    Photo getFirstPhotoOne();

    String getFuncCodeByPhoto(Photo photo);

    boolean updatePhoto(Photo photo);

    long getPhotoMinId();

    long getPhotoMaxId();

    void updatePhotoImgId(long ID);
}
