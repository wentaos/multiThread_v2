package com.winchannel.service;

import com.winchannel.bean.Photo;


public interface IDInfoService {

    void saveInfoPhoto(Photo prop);

    void updateInfoPhoto(Photo prop);

    Photo getInfoPhoto();
}
