package com.winchannel.bean;

import java.io.Serializable;

/**
 * 封装处理的ID和线程信息
 */
public class IDInfo implements Serializable{

    private String threadName;

    private long startId;
    private long currId;
    private long endId;

    public IDInfo(){}

    public String getThreadName() {
        return threadName;
    }

    public IDInfo setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public long getStartId() {
        return startId;
    }

    public IDInfo setStartId(long startId) {
        this.startId = startId;
        return this;
    }

    public long getCurrId() {
        return currId;
    }

    public IDInfo setCurrId(long currId) {
        this.currId = currId;
        return this;
    }

    public long getEndId() {
        return endId;
    }

    public IDInfo setEndId(long endId) {
        this.endId = endId;
        return this;
    }

    private static final long serialVersionUID = 1L;
}
