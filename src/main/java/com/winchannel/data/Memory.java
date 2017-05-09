package com.winchannel.data;


public class Memory {
    public static long CURR_MAX_ID = 0L;
    public static final long MAX_ID = 1000L;

    public static boolean isDist(){
        return CURR_MAX_ID<MAX_ID?true:false;
    }

}
