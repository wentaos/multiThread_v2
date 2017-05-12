package com.winchannel.bean;

import java.io.Serializable;

/**
 * 自定义Bean,用于封装照片信息的基本Bean对象
 */
public class Photo implements Serializable {

    private long id;
    private String imgId;
    private String imgUrl;
    private String imgAbsPath;
    private String bizDate;// 业务日期
    /**
     * VISIT_PHOTO表中 本身并不包含，这里是中途存放方便使用
     */
    private String funcCode;
    public Photo(){}

    public Photo(Long id, String imgId, String imgUrl, String imgAbsPath,String bizDate) {
        this.id = id;
        this.imgId = imgId;
        this.imgUrl = imgUrl;
        this.imgAbsPath = imgAbsPath;
        this.bizDate = bizDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgAbsPath() {
        return imgAbsPath;
    }

    public void setImgAbsPath(String imgAbsPath) {
        this.imgAbsPath = imgAbsPath;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public String getBizDate() {
        return bizDate;
    }

    public void setBizDate(String bizDate) {
        this.bizDate = bizDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private static final long serialVersionUID = 3466625674488254661L;
}
