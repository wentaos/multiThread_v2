package com.winchannel.dao.impl;

import com.winchannel.bean.Photo;
import com.winchannel.dao.PhotoDao;
import com.winchannel.funccode.FunccodeXmlUtil;
import com.winchannel.utils.DBUtil;
import com.winchannel.cleanUtil.OptionPropUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class PhotoDaoImpl implements PhotoDao {
    private static Logger logger = Logger.getLogger(PhotoDaoImpl.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.driverClassName}")
    private String driver;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String passWord;


    /**
     * 是否开启使用 根据 FUNC_CODE查询PhotitId处理数据
     */
    boolean IS_FUNC_CODE_PART = OptionPropUtil.IS_FUNC_CODE_PART();


    /*******************************************新版本增加功能********************************************/



    /**
     * 从baseQuery查询中得到结果集最大ID
     */
    @Override
    public Long selectMaxIdByBaseQuery() {
        // 代替查询全部Photo数据的sql
        String baseQuerySql = FunccodeXmlUtil.getBaseQuerySql();
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt = null;
        Long maxId = null;
        try {
            pstmt = conn.prepareStatement(baseQuerySql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                maxId = rs.getLong("ID");
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }


    @Override
    public List<Long> selectNextIdPoolFromBaseQueryByEndId(Long endId) {
        List<Long> ID_POOL = null;
        String baseQuerySql = FunccodeXmlUtil.getBaseQuerySql();
        // 组装 baseQuerySql eg: select top 100 p.ID from (baseQuerySql);
        baseQuerySql = "SELECT TOP "+ OptionPropUtil.REDUCE_ID_NUM()+" p.ID FROM "
                +"("+baseQuerySql+") p WHERE p.ID>"+endId;

        logger.info("Next ID_POOL: baseQuerySql===>>>"+baseQuerySql);

        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt = null;

        try {
            ID_POOL = new ArrayList<Long>();
            pstmt = conn.prepareStatement(baseQuerySql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("ID");
                ID_POOL.add(id);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return ID_POOL;
        }
        logger.info("Next ID_POOL: SIZE===>>>"+ID_POOL.size());
        return ID_POOL;
    }












    @Override
    public List<Photo> selectPhotoListByBaseQuery(){
        // 代替查询全部Photo数据的sql
        String baseQuerySql = FunccodeXmlUtil.getBaseQuerySql();
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        List<Photo> photoList = new ArrayList<Photo>();
        try {
            pstmt = conn.prepareStatement(baseQuerySql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Photo photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
                photoList.add(photo);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photoList;
    }




    /**
     * 目前最新使用的查询FUNC_CODE的方法： 从XML配置的sql查询中获取
     * @param photo
     * @return
     */
    @Override
    public String getFunCodeByPhoto(Photo photo){
        Map<String,Object> sqlMap = FunccodeXmlUtil.getSqlList();
        // 系统平台自身sql
        String sfaSql = (String)sqlMap.get("sfaSql");
        // 定制sql
        List<String> speSqlList = (List<String>)sqlMap.get("speList");

        String funcCode = null;

        // 先查询系统平台自身的
        funcCode = selectFuncCodeFromRPT(sfaSql,photo.getBizDate(),photo.getId());
        if(funcCode !=null && funcCode.trim().length()>0){// 说明获取到了
            return funcCode;
        }

        // 继续查询 其他业务表
        if(speSqlList!=null && speSqlList.size()>0){
            for (String sql:speSqlList){
                funcCode = getFuncCodeFromOneBiz(sql, photo.getBizDate(), photo.getImgId());
                if(funcCode !=null && funcCode.trim().length()>0){// 说明获取到了
                    return funcCode;// 获取到直接返回
                }
            }
        }

        return funcCode;
    }



    /**
     * 系统平台自身表 RPT_PHOTO 查询
     * @param sql
     * @param bizDate
     * @param photoId
     * @return
     */
    //    select imgId AS PHOTO_ID,bizDate AS BIZ_DATE,funcCode AS FUNC_CODE,imgUrl AS IMG_URL from RPT_PHOTO WHERE bizDate='${bizDate}' AND imgId=${imgId}
    public String selectFuncCodeFromRPT(String sql,String bizDate,long photoId) {
        String fixSql = "SELECT funcCode AS FUNC_CODE FROM RPT_PHOTO WHERE bizDate='${bizDate}' AND imgId=${photoId}";
        sql = fixSql;// 目前sql是固定的，不实用配置文件配置，后续扩展的话再去掉该行赋值
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        Statement stmt = null;
        ResultSet rs = null;
        String funcCode = null;
        try {
            if(sql!=null && sql.trim().length()>0){
                if(sql.contains("${photoId}")){
                    sql.replace("${photoId}",photoId+"");
                }
                if(sql.contains("${bizDate}")){
                    sql.replace("${bizDate}",bizDate);
                }
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    funcCode = rs.getString("FUNC_CODE");
                    logger.info("获取到FUNC_CODE = " + funcCode);
                }
            }
            DBUtil.closeDbResources(conn, stmt, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcCode;
    }




    @Override
    public String getFuncCodeFromOneBiz(String sql,String bizDate,String imgId) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        Statement stmt = null;
        ResultSet rs = null;
        String funcCode = null;
        try {
            if(sql!=null && sql.trim().length()>0){
                if(sql.contains("${imgId}")){
                    sql.replace("${imgId}",imgId+"");
                }
                if(sql.contains("${bizDate}")){
                    sql.replace("${bizDate}",bizDate);
                }
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    funcCode = rs.getString("FUNC_CODE");
                    logger.info("获取到FUNC_CODE = " + funcCode);
                }
            }
            DBUtil.closeDbResources(conn, stmt, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcCode;
    }



    @Override
    public List<String> selectImgIdListByFcQuery(String fcQuerySql) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> imgIdList = new ArrayList<String>();

        try {
            pstmt = conn.prepareStatement(fcQuerySql);
            rs = pstmt.executeQuery();
            while (rs.next()){
                String imgId = rs.getString("IMG_ID");
                imgIdList.add(imgId);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgIdList;
    }




    @Override
    public List<Long> selectPhotoIdListBtImgId(String imgId) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Long> photoIdList = new ArrayList<Long>();

        try {

            String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
            String sql = "SELECT ID FROM "+table_name+" WHERE IMG_ID=?";
            pstmt =  conn.prepareStatement(sql);
            pstmt.setString(1,imgId);
            rs = pstmt.executeQuery();
            while (rs.next()){
                Long id = rs.getLong("ID");
                photoIdList.add(id);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoIdList;
    }
























    /****************************原有功能**********************************************/






    @Override
    public Photo selectPhotoOne(long id) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        Photo photo = null;
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try {
            String sql = "SELECT * FROM "+table_name+" WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            //int col = rs.getMetaData().getColumnCount();

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


    /**
     * 这是查询全部的
     * @return
     */
    @Override
    public List<Photo> selectPhotoList() {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        List<Photo> photoList = new ArrayList<Photo>();
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try {
            // 按照 ID 排序得到第一个
        	String oneSql = "";
        	if(OptionPropUtil.IS_MYSQL()){
        		oneSql = "SELECT ID,IMG_ID,IMG_URL,ABSOLUTE_PATH from "+table_name+" ORDER BY ID LIMIT 1";
        	} else if (OptionPropUtil.IS_SQLSERVER()){
        		oneSql = "SELECT top 1 ID,IMG_ID,IMG_URL,ABSOLUTE_PATH from "+table_name+" ORDER BY ID";
        	}
            pstmt = conn.prepareStatement(oneSql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Photo photo = new Photo();
                photo.setId(rs.getLong("ID"));
                photo.setImgId(rs.getString("IMG_ID"));
                photo.setImgUrl(rs.getString("IMG_URL"));
                photo.setImgAbsPath(rs.getString("ABSOLUTE_PATH"));
                photoList.add(photo);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photoList;
    }






    @Deprecated
    @Override
    public String getFuncCodeByImgId(String imgId) {
        String[] tabNames = null;
        String funcCode = null;
        if(OptionPropUtil.IS_TEST()){
        	tabNames = new String[]{"VISIT_INOUT_STORE_T", "MS_VISIT_ACVT_T", "VISIT_DIST_RULE_T", "VISIT_SEC_DISP_T"};
        } else {
        	tabNames = new String[]{"VISIT_INOUT_STORE", "MS_VISIT_ACVT", "VISIT_DIST_RULE", "VISIT_SEC_DISP"};
        }
        		
        try {
            logger.info("遍历FUNC_CODE相关的表数组 START ...");
            for (String TABLE_NAME : tabNames) {
                // 这里先用于测试
               synchronized (this.getClass()){
                   funcCode = selectFuncCodeByImgId(TABLE_NAME, imgId);
               }
                if (funcCode != null && funcCode.length() > 0) {
                    logger.info("获取到对应的 FUNC_CODE：FUNC_CODE=" + funcCode);
                    break;
                }
            }
            
            return funcCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 这个是旧版本 根据PHOTO表中的IMG_ID字段 查询某几张表的中的FUNC_CODE数据的方法，应该被舍弃
     * @param TABLE_NAME
     * @param imgId
     * @return
     */
    @Deprecated
    public String selectFuncCodeByImgId(String TABLE_NAME, String imgId) {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String funcCode = "";
        try {
        	String IMG_ID_COL_NAME = "IMG_IDX";
        	String MS_VISIT_ACVT = "MS_VISIT_ACVT";
        	 if(OptionPropUtil.IS_TEST()){
        		 MS_VISIT_ACVT = "MS_VISIT_ACVT_T";
        	 }
        	
        	if(MS_VISIT_ACVT.toLowerCase().equals(TABLE_NAME.toLowerCase())){
        		IMG_ID_COL_NAME = "IMG_ID";
        	}
        	
            String sql = "SELECT FUNC_CODE FROM " + TABLE_NAME + " WHERE "+IMG_ID_COL_NAME+"=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, imgId);
            logger.info("查询表" + TABLE_NAME + " 中 FUNC_CODE ... BY "+IMG_ID_COL_NAME+"=" + imgId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                funcCode = rs.getString("FUNC_CODE");
                logger.info("获取到FUNC_CODE = " + funcCode);
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return funcCode;
    }

    
    
    @Override
    public int updatePhoto(Photo photo) {

        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        // 设置事务为非自动提交
        try{
            // 设置手动处理事务
            conn.setAutoCommit(false);
            String sql  ="UPDATE "+table_name+" SET IMG_URL=?,ABSOLUTE_PATH=? WHERE ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,photo.getImgUrl());
            pstmt.setString(2,photo.getImgAbsPath());
            pstmt.setLong(3,photo.getId());
            int record = pstmt.executeUpdate();
            conn.commit();// 提交Update
            logger.info("Update Success! 事务提交！");
            DBUtil.closeDbResources(conn, pstmt, null);
            return record;
        }catch (Exception e){
            logger.error("Dao Update Photo Error!");
            try{
                logger.info("Update 回滚！");
                conn.rollback();
            }catch (SQLException sqle){
                sqle.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long selectPhotoMaxId() {
        Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        try{
            String oneSql = "SELECT top 1 max(ID) ID FROM "+table_name+"";
        	if(OptionPropUtil.IS_MYSQL()){
        		oneSql = "SELECT max(ID) ID FROM "+table_name+" LIMIT 1";
        	} else if (OptionPropUtil.IS_SQLSERVER()){
        		oneSql = "SELECT top 1 max(ID) ID FROM "+table_name+"";
        	}
        	
            pstmt = conn.prepareStatement(oneSql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Long ID = rs.getLong("ID");
                if (ID!=null){
                    return ID;
                }
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }








	@Override
	public long selectPhotoMinId() {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        
        try{
            String sql = "";
            if(OptionPropUtil.IS_MYSQL()){
            	sql = "SELECT min(ID) ID FROM "+table_name+" LIMIT 1";
        	} else if (OptionPropUtil.IS_SQLSERVER()){
        		sql = "SELECT top 1 min(ID) ID FROM "+table_name+"";
        	}
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                Long ID = rs.getLong("ID");
                if (ID!=null){
                    return ID;
                }
            }
            DBUtil.closeDbResources(conn, pstmt, rs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
	}











    /*********************下面的是测试方法*********************************/

    /**
     * 测试使用
     */
	@Override
	public void updatePhotoImgId(long ID) {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try{
            String sql = "update "+table_name+" set IMG_ID='img_id_ok' WHERE ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, ID);
            pstmt.executeUpdate();
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

    
    /**
     * 用于测试
     */
	@Override
	public void insertPhoto(Photo photo) {
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        String table_name = OptionPropUtil.IS_TEST()?"VISIT_PHOTO_T":"VISIT_PHOTO";
        try{
            String photoSql = "insert into "+table_name+"(ID,IMG_ID,IMG_URL,ABSOLUTE_PATH)"+" VALUES(?,?,?,?)";
            pstmt = conn.prepareStatement(photoSql);
            pstmt.setLong(1, photo.getId());
            pstmt.setString(2, photo.getImgId());
            pstmt.setString(3,photo.getImgUrl());
            pstmt.setString(4,photo.getImgAbsPath());
            pstmt.executeUpdate();
            
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	/**
	 * 用于测试
	 */
	@Override
	public void insertFuncCodeTable(String funcCodeTable,Photo photo){
		Connection conn = DBUtil.getConnection(driver,dbUrl,userName,passWord);
        PreparedStatement pstmt;
        try{
        	String IMG_ID_COL_NAME = "IMG_IDX";
        	if(funcCodeTable.toLowerCase().contains("MS_VISIT_ACVT".toLowerCase())){
        		IMG_ID_COL_NAME = "IMG_ID";
        	}
        	
            String funcCodeSql = "insert into "+funcCodeTable+"(ID,"+IMG_ID_COL_NAME+",FUNC_CODE"+") VALUES(?,?,?)";
            pstmt = conn.prepareStatement(funcCodeSql);
            pstmt.setLong(1,photo.getId());
            pstmt.setString(2, photo.getImgId());
            pstmt.setString(3, photo.getFuncCode());
            pstmt.executeUpdate();
            
            DBUtil.closeDbResources(conn, pstmt, null);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	
	
	
}
