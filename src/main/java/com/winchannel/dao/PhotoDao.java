package com.winchannel.dao;

import com.winchannel.bean.Photo;

import java.sql.SQLException;
import java.util.List;

public interface PhotoDao {

    /**
     * 根据当前记录的markid获取下一个Photo
     */
    Photo selectPhotoOne(long id);

    List<Photo> selectPhotoList();

    String getFuncCodeByImgId(String imgId);

    int updatePhoto(Photo photo);

    long selectPhotoMinId();
    
    long selectPhotoMaxId();
    
	void updatePhotoImgId(long ID);
	
	void insertPhoto(Photo photo);

	void inserFuncCodeTable(String funcCodeTable, Photo photo);

}
