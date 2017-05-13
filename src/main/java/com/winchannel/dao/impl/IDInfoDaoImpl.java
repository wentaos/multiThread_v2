package com.winchannel.dao.impl;


import com.winchannel.bean.Photo;
import com.winchannel.dao.IDInfoDao;
import com.winchannel.data.Constant;
import com.winchannel.utils.DBUtil;
import com.winchannel.cleanUtil.OptionPropUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IDInfoDaoImpl  implements IDInfoDao {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.driverClassName}")
    private String driver;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String passWord;

    @Override
    public void insertInfoPhoto(Photo prop) {
        Connection conn = DBUtil.getConnection(driver, dbUrl, userName, passWord);
        PreparedStatement pstmt;
        String table_name = OptionPropUtil.IS_TEST() ? "VISIT_PHOTO_T" : "VISIT_PHOTO";
        table_name="VISIT_PHOTO";

        try {
            String sql = "INSERT INTO " + table_name + "(ID,IMG_ID,IMG_URL) VALUES(?,?,?)";
            if(OptionPropUtil.IS_TEST()){
                sql = "INSERT INTO " + table_name + "(ID,IMG_ID,IMG_URL) VALUES(?,?,?)";
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, 999999999);// 这里使用9位最大值
            pstmt.setString(2, prop.getImgId());
            pstmt.setString(3, prop.getImgUrl());

            pstmt.executeUpdate();

            DBUtil.closeDbResources(conn, pstmt, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateInfoPhoto(Photo prop) {
        Connection conn = DBUtil.getConnection(driver, dbUrl, userName, passWord);
        PreparedStatement pstmt;
        String table_name = OptionPropUtil.IS_TEST() ? "VISIT_PHOTO_T" : "VISIT_PHOTO";
        table_name="VISIT_PHOTO";

        try {
            String sql = "UPDATE " + table_name + " SET IMG_URL=? WHERE IMG_ID=?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,prop.getImgUrl());
            pstmt.setString(2, prop.getImgId());
            pstmt.executeUpdate();

            DBUtil.closeDbResources(conn, pstmt, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Photo selectInfoPhoto() {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        Photo photo = null;
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        table_name="VISIT_PHOTO";

        try {
            String queryCol = "ID,IMG_ID,IMG_URL,ABSOLUTE_PATH";
            String sql = "SELECT "+queryCol+" FROM "+table_name+" WHERE IMG_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Constant.IMG_ID_FALG);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
            }

            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photo;
    }
}
