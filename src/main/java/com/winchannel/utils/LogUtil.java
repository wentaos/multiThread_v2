package com.winchannel.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogUtil {

    private Class clazz = null;
    private Logger logger = null;



    public LogUtil(){}
    public LogUtil(Class clazz){
        this.clazz = clazz;
    }

    public LogUtil log(Class clazz){
        logger = LoggerFactory.getLogger(clazz);
        this.setLogger(logger);
        return this;
    }


    public void info(String info) {
        logger.info(this.clazz.getName()+"-"+info);
    }

    public void debug(String debug){
        logger.debug(this.clazz.getName()+"-"+debug);
    }

    public void error(String error) {
        logger.error(this.clazz.getName()+"-"+error);
    }



    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
