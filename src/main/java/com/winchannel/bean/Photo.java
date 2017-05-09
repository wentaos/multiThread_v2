package com.winchannel.bean;

import java.io.Serializable;

/**
 * Created by Wator on 2017/5/7.
 */
public class Photo implements Serializable {

    private long id;
    private String imgId;
    private String imgUrl;
    private String imgAbsPath;
    /**
     * VISIT_PHOTO表中 本身并不包含，这里是中途存放方便使用
     */
    private String funcCode;
    public Photo(){}

    public Photo(Long id, String imgId, String imgUrl, String imgAbsPath) {
        this.id = id;
        this.imgId = imgId;
        this.imgUrl = imgUrl;
        this.imgAbsPath = imgAbsPath;
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

    private static final long serialVersionUID = 3466625674488254661L;
}
