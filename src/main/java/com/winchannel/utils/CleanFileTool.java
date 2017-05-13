package com.winchannel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.winchannel.cleanUtil.OptionPropUtil;

import com.winchannel.bean.Photo;
import org.slf4j.LoggerFactory;

/**
 * 内容中：涉及到的路径是基于 D:/aaa/bbb/2017-02-12/j23h43h24234324h32ui4hf.jpg 这种格式
 * headPath         D:/aaa/bbb/
 * datePath         2017-02-12
 * dateFullPath     D:/aaa/bbb/2017-02-12
 */
public class CleanFileTool {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CleanFileTool.class);

    /**
     * 这些静态字段可专用于 在获取到的pathMap 中当作key获取对应的path
     * 暂时用不到
     */
    @Deprecated
    public static String FILE_NAME_PATH = "FILE_NAME_PATH";
    @Deprecated
    public static String DATE_PATH = "DATE_PATH";
    @Deprecated
    public static String DATE_FULL_PATH = "DATE_FULL_PATH";
    @Deprecated
    public static String HEAD_PATH = "HEAD_PATH";


    /**
     * 判断对应的 FUNC_CODE + DATE + photoname 是否存在
     * 如果存在就师傅和规则的，不用处理了
     */
    public static boolean isTruePath_bak(Photo photo) {
        String absPath = photo.getImgAbsPath();

        // 如果没有绝对路径，直接忽略掉
        if(absPath==null || absPath.trim().length()==0){
            return true;
        }
        // absPath 如：D:/Photo_Test/photos/2017-01-12/101.jpg
        String date = getDatePathFromUrl(absPath);
        if(date!=null){
            String photos = absPath.split(date)[0];// 得到 D:/Photo_Test/photos/
            String tail = photos.split("photos")[1];// 得到 /
            // 如果长度够长，中间会包含FUNC_CODE目录
            if(tail.length()>3){
                return true;
            }
        }
        return false;
    }

    // 通用版本：对于老数据，没有ABSOLUTE_PATH 数据
    public static boolean isTruePath2(Photo photo) {
        String func_code = photo.getFuncCode();
        // 2012年的数据没有ABSOLUTE_PATH数据,并且没有dot2B 这种分隔符
        String imgUrl = photo.getImgUrl();
        // 数据格式：.../photos/2012-01-01/xxx.jpg 	/media/Ddot1Adot2BPhoto_Testdot2Bphotosdot2B2017-01-01dot2B8.jpg

        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }

        // 如果
        if(imgUrl.contains("dot2B")){// 新型数据
            return isTurePath("dot2B",imgUrl,func_code);
        }else{
            return isTurePath("/",imgUrl,func_code);
        }

    }





    public static boolean isTurePath(String flag,String imgUrl,String trueFuncCode){
        // 为空直接忽略掉
        if(imgUrl==null || imgUrl.trim().length()==0){
            return true;
        }

        String afterStr = imgUrl.split("photos"+flag)[1];

        String[] arr = afterStr.split(flag);
        int flag_num = arr.length-1;// afterStr中 dot2B 的个数
        if(flag_num>=2){// 可能有错误路径 F0sdsadas_dsdafasddfsad/date/xxx.jpg
            String date = getDatePathFromUrl(afterStr);
            if(date!=null && date.trim().length()>0) {
                String funccode_id = afterStr.split(flag + date)[0];
                if(trueFuncCode.toLowerCase().trim().equals(
                        funccode_id.toLowerCase().trim()
                )){// 日期前面的目录 不是错误的目录，和正确的FUNC_CODE一样
                    return true;
                }else {
                    return false;
                }
            }

        }else if(flag_num==1){// afterStr 说明剩下的格式是: date/xxx.jpg

            String F0A_1x2x3x = afterStr.split(flag)[0];
            // 先得到日期：如果防寒日期，则不是正确的路径格式
            String date = getDatePathFromUrl(afterStr);
            if(date!=null){
                return false;
            }else if(F0A_1x2x3x!=null && F0A_1x2x3x.startsWith("F") && F0A_1x2x3x.length()>10){
                return false;
            }

        }else{// 其他的不做考虑，认为是正确的
            return true;
        }
        return true;
    }






    /**
     * 判断创建 FUNC_CODE 对应的目录
     */
    public static String cleanFuncCodePath(String FUNC_CODE) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String funcCodePath = PHOTO_PATH + "/" + FUNC_CODE;
        funcCodePath = createPath(funcCodePath);
        return funcCodePath;
    }

    /**
     * 对日期目录进行处理
     * 这里使用 imgUrl：在老数据中 ABSOLUTE_PATH 没有数据
     * imgUrl：/photos/2013-01-01/xxx.jpg
     */
    public static String cleanDatePath(String funcCodePath, String imgUrl) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String code_date_path = "";
        String date = "";
        if(imgUrl!=null && imgUrl.trim().length()>0){
            date = getDatePathFromUrl(imgUrl);
            if(date!=null){
                code_date_path=funcCodePath+ File.separator+date;
                code_date_path = createPath(PHOTO_PATH+ File.separator +code_date_path);
            }
            /*else{// 如果是意外情况的目录，没有date目录
                if(imgUrl.contains("dot2B")){
                    String afterStr = imgUrl.split("photos"+"dot2B")[1];
                    date_OR_F0A_1x2x3x = afterStr.split("dot2B")[0];
                    code_date_path=funcCodePath+ File.separator+date_OR_F0A_1x2x3x;
                    code_date_path = createPath(PHOTO_PATH+ File.separator +code_date_path);
                }else if(imgUrl.contains("photos/")){// 老数据使用 / 分割
                    String afterStr = imgUrl.split("photos"+"/")[1];
                    date_OR_F0A_1x2x3x = afterStr.split("/")[0];
                    code_date_path=funcCodePath+ File.separator+date_OR_F0A_1x2x3x;
                    code_date_path = createPath(PHOTO_PATH+ File.separator +code_date_path);
                }
            }*/
        }
        return date;
    }


    /**
     * 得到 headPath
     */
    public static String getHeadPath(String absolutePath) {
        if (absolutePath != null && absolutePath.length() > 0) {
            int lastP1 = absolutePath.lastIndexOf("/");
            String subPath = absolutePath.substring(0, lastP1);
            int lastP2 = subPath.lastIndexOf("/");
            String headPath = subPath.substring(0, lastP2);
            return headPath;
        }
        return null;
    }

    /**
     * 获取文件名部分apth
     */
    public static String getFileNamePath(String url) {
        if (url == null) {
            return null;
        }
        String fileNamePath = url.substring(url.lastIndexOf("/") + 1);
        return fileNamePath;
    }


    /**
     * 组装新的file 绝对路径
     * 加上 FUNC_CODE_PATH
     */
    public static String getNewAbsPath(String absolutePath, String funcCodePath,String date) {
        String headPath = OptionPropUtil.PHOTO_PATH();// getHeadPath(absolutePath);
//        String datePath = getDatePathFromUrl(absolutePath);
        String fileNamePath = getFileNamePath(absolutePath);
        String newAbsPath = headPath + funcCodePath + "/" + date + "/" + fileNamePath;
        return newAbsPath;
    }

    public static String getNewAbsPath(String[] paths) {
        String headPath = OptionPropUtil.PHOTO_PATH();
//        String datePath = getDatePathFromUrl(paths[0]);
        String fileNamePath = getFileNamePath(paths[0]);
        String newAbsPath = headPath + paths[1] + "/" + paths[2] + "/" + fileNamePath;
        return newAbsPath;
    }

    /**
     * 得到新的 IMG_URL
     * IMG_URL: /media/Ddot1Adot2BAPPdot2BSFA_demodot2Bwebappsdot2BROOTdot2B photosdot2B  这里可能有错误目录dot2B  2016-07-28dot2Bb5f6ebf8-eb7f-44ef-bd5d-8843bcd74706dot4Djpg.jpg
     * 老数据是 photos/2017-01-01/xxx.jpg
     */
    public static String getNewImgUrl(String oldImgUrl, String funcCodePath,String date) {
        String newImgUrl = null;
        if (oldImgUrl != null) {
//        	String date = getDatePathFromUrl(oldImgUrl);
            newImgUrl = oldImgUrl;// 保证原来的数据没问题

            if (date!=null) {
                // 替换
                String flag = "dot2B";
                if(oldImgUrl.contains("dot2B")){
                    flag = "dot2B";
                }else{// 就是 / 分割
                    flag = "/";
                }
                String var0 = oldImgUrl.split("photos"+flag)[1];
                if(var0!=null && var0.trim().length()>0){
                    String var1 = var0.split(date)[0];// 得到的可能是错误目录
                    if(var1!=null && var1.trim().length()>0){// 说明存在错误目录
                        newImgUrl = oldImgUrl.replace(var1+date, funcCodePath + flag + date);// 整体替换
                    }
                }

            }
        }
        return newImgUrl;
    }


    /**
     * 截取图片日期部分 2016-07-28
     * @param \ D:/APP/SFA_demo/webapps/ROOT/photos/2016-07-28/b5f6ebf8-eb7f-44ef-bd5d-8843bcd74706.jpg
     */
    public static String getDatePathFromUrl(String url) {
        String date = "";
        Pattern p = Pattern.compile("2\\d{3}-?[0-1][0-9]-?[0-3][0-9]");
        Matcher m = p.matcher(url);
        if(m.find()){
            date = m.group();
            return date;
        }
        return null;
    }






    /**
     * 判断目录是否存在，不存在则创建
     */
    public static String createPath(String path) {
        File p = new File(path);
        boolean flag = p.exists() && p.isDirectory();
        if (!flag) {// 不存在该目录
            p.mkdirs();
        }
        return path;
    }




    /**
     * 剪切文件
     */
    public static boolean movePhoto(String sourcePath, String destPath) {

        String sysSourcePath = sourcePath.replace("/", File.separator);// 换成系统的分隔符
        String sysDestPath = destPath.replace("/", File.separator);// 换成系统的分隔符

        // 复制0
        boolean copyOk = copyPhoto(sysSourcePath, sysDestPath);
        try {
            if (copyOk) {
                // 删除源文件
                deletePhoto(sysSourcePath);
            }
            // eg: D:/aaa/2017-02-23
            String dateFullPath = sysSourcePath.substring(0, sysSourcePath.lastIndexOf(File.separator));
            // 检查对应的原日期目录中是否还有图片，没有图片，删除整个日期目录
            if (isEmptyPath(dateFullPath)) {
                // 删除日期目录
                new File(dateFullPath).delete();
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 剪切文件
     */
    public static boolean movePhoto(String[] paths) {
        String PHOTO_PATH = OptionPropUtil.PHOTO_PATH();
        String sysSourcePath = PHOTO_PATH
                + paths[0].replace("/", File.separator);// imgUrl 换成系统的分隔符
        String sysDestPath = paths[1].replace("/", File.separator);// 换成系统的分隔符

        // 复制0
        boolean copyOk = copyPhoto(sysSourcePath, sysDestPath);
        try {
            if (copyOk) {
                // 删除源文件
                deletePhoto(sysSourcePath);
            }
            // eg: D:/aaa/2017-02-23
            String dateFullPath = sysSourcePath.substring(0, sysSourcePath.lastIndexOf(File.separator));
            // 检查对应的原日期目录中是否还有图片，没有图片，删除整个日期目录
            if (isEmptyPath(dateFullPath)) {
                // 删除日期目录
                new File(dateFullPath).delete();
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 检查目录中是否还存在文件
     */
    public static boolean isEmptyPath(String checkPath) {
        File path = new File(checkPath);
        String[] fileList = null;
        // 检查目录合法性
        if (checkPath != null && path.isDirectory()) {
            fileList = path.list();
        }
        // 没有文件
        if (fileList == null || fileList.length == 0) {
            return true;
        }
        return false;
    }


    /**
     * 复制单个文件
     */
    public static boolean copyPhoto(String sourcePath, String destPath) {
        logger.info("开始复制Photo ...");
        try {
            File photo = new File(sourcePath);

            // 判断photo是否是一个存在的文件，防止发生"系统找不到指定的路径"
            if(!photo.exists()){
                return false;
            }

            // 如果存在 再执行操作
            if (photo.exists()) {
                InputStream in = new FileInputStream(sourcePath); //读入原文件
                OutputStream out = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024 * 10];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                logger.info("复制Photo完成 ...");
                logger.info("文件大小 Length ：" + photo.length());
            }

            return true;
        } catch (IOException ioe) {
            logger.error("复制Photo失败 ...");
            ioe.printStackTrace();
            return false;
        }

    }


    /**
     * 文件删除
     */
    public static boolean deletePhoto(String sourcePath) {
        File photo = new File(sourcePath);
        if (photo.exists()) {
            System.out.print(sourcePath);
            photo.delete();
            return true;
        } else if (!photo.isAbsolute()) {// 不是绝对路径删除不了
            return false;
        }
        return false;
    }




}
