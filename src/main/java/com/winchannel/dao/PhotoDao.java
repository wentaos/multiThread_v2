package com.winchannel.dao;

import com.winchannel.bean.Photo;

import java.sql.SQLException;
import java.util.List;

public interface PhotoDao {


    /******************新版本增加功能****************************/


    String getFunCodeByPhoto(Photo photo);

    String getFuncCodeFromOneBiz(String sql,String bizDate,String imgId);

    Long selectMaxIdByBaseQuery();

    List<Long> selectNextIdPoolFromBaseQueryByEndId(Long endId);



    /**
     *根据img_id查询Photo的id数据
     */
    List<Long> selectPhotoIdListBtImgId(String imgId);

    List<String> selectImgIdListByFcQuery(String fcQuerySql);






    /**
     * 根据当前记录的markid获取下一个Photo
     */
    Photo selectPhotoOne(long id);

    List<Photo> selectPhotoList();

    List<Photo> selectPhotoListByBaseQuery();

    String getFuncCodeByImgId(String imgId);

    int updatePhoto(Photo photo);

    long selectPhotoMinId();
    
    long selectPhotoMaxId();
    
	void updatePhotoImgId(long ID);
	
	void insertPhoto(Photo photo);




    /**
     * 用于测试
     * @param funcCodeTable
     * @param photo
     */
    void insertFuncCodeTable(String funcCodeTable, Photo photo);
}
