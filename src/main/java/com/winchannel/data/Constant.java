package com.winchannel.data;

public class Constant {

	/**
	 * THREAD_NUM:线程数
	 */
	public static final String THREAD_NUM = "THREAD_NUM";


	/**
	 * PHOTO_PATH：需要处理的文件目录
	 */
	public static final String PHOTO_PATH = "PHOTO_PATH";

	/**
	 * RUN_TASK_TIME_LEN:定时任务运行时长 分钟
	 */
	public static final String RUN_TASK_TIME_LEN = "RUN_TASK_TIME_LEN";

	/**
	 * REDUCE_ID_NUM:一个线程一次处理的ID数
	 */
	public static final String REDUCE_ID_NUM = "REDUCE_ID_NUM";

	/**
	 * IS_TEST:是否使用测试数据库中测试数据表 XXX_T
	 */
	public static final String IS_TEST = "IS_TEST";

	/**
	 * LOOP_SAVE_COUNT :遍历多少条记录保存一次ID信息:数据越小越利于重启连续；过小也会增加数据库update访问次数
	 */
	public static final String LOOP_SAVE_COUNT = "LOOP_SAVE_COUNT";

	/**
	 * 数据库类型
	 */
	public static final String SQLSERVER = "SQLSERVER";
	public static final String MYSQL = "MYSQL";
	public static final String DB_TYPE="DB_TYPE";

	/**
	 * 数据库ID信息唯一标志
	 */
	public static final String IMG_ID_FALG = "IMG_ID_FALG";

	/**
	 * 存放ID_INFO信息的目录
	 */
	public static final String ID_INFO_PATH = "ID_INFO_PATH";

}