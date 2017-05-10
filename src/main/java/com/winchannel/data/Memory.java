package com.winchannel.data;


public class Memory {
    public static long DIST_MAX_ID = 0L;// 这里指的是已分配的ID中最大的
    public static long MAX_ID = 1000L;

    public static boolean isDist(){
        return DIST_MAX_ID<MAX_ID?true:false;
    }

}
