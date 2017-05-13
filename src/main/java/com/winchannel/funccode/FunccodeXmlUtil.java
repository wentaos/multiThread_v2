package com.winchannel.funccode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析Func_Code SQL xml配置
 */
public class FunccodeXmlUtil {

    private static String resourceFilePath = "spring/config/func_code_sql.xml";
    private static String resourceFilePath1 = "D:\\func_code_sql.xml";

    private static String TYPE_SPE = "SPE";
    private static String TYPE_RPT = "RPT";


    public static void main(String[] args) {
        Map<String, Object> sqlMap = getSqlList();

        // 系统平台自自身
        String sfaSql = (String) sqlMap.get("sfaSql");
        // 个性化定制sql
        List<String> speSqlList = (ArrayList) sqlMap.get("speList");

        System.out.println(sfaSql);
        System.out.println(speSqlList.size());

    }

    /**
     * 获取查询的sql
     * @return Map<String, Object>
     */
    public static Map<String, Object> getSqlList() {

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        List<String> speList = new ArrayList<String>();

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File(resourceFilePath1));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Element root_querys = document.getRootElement();

        // 全部子标签
        List<Element> sql_List = root_querys.elements();

        if(sql_List!=null && sql_List.size()>0){
            for (Element e : sql_List) {
                String type = e.attributeValue("type");
                // 定制类型
                if (TYPE_SPE.equals(type)) {
                    String speSql = e.getText();
                    speSql = cleanSql(speSql);
                    if(speSql.trim().length()!=0){
                        speList.add(speSql);
                    }
                }
            }
        }

        // 系统平台sql元素
        Element select_RPT = root_querys.element("select_RPT");
        String rptSql = select_RPT.getText();
        rptSql = cleanSql(rptSql);
        Element baseQuery = root_querys.element("baseQuery");
        String baseQuerySql = baseQuery.getText();
        sqlMap.put("baseQuerySql",checkBaseQueryOrder(baseQuerySql));
        if(rptSql.trim().length()!=0){
            sqlMap.put("rptSql", rptSql);
        }
        sqlMap.put("speList", speList);
        return sqlMap;
    }


    public static String getBaseQuerySql(){
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File(resourceFilePath1));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root_querys = document.getRootElement();
        Element baseQuery = root_querys.element("baseQuery");
        String baseQuerySql = baseQuery.getText();
        baseQuerySql = checkBaseQueryOrder(baseQuerySql);
        return baseQuerySql;
    }


    /**
     * 获取 根据FUNC_CODE查询 img_id的sql
     * baseFcQuery fcQuery
     */
/*
    public static String[] getBaseFcQuerys(){
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File(resourceFilePath1));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root_querys = document.getRootElement();
        Element baseFcQuery = root_querys.element("baseFcQuery");
        List<Element> fcQuerys = baseFcQuery.elements();
        String[] fcQueryArr = null;
        if(fcQuerys!=null && fcQuerys.size()>0){
            fcQueryArr = new  String[fcQuerys.size()];
            for (int i=0;i<fcQuerys.size();i++){
                String resourceSql = fcQuerys.get(i).getText();
                resourceSql = cleanSql(resourceSql);
                fcQueryArr[i] = resourceSql;
            }
        }
        return fcQueryArr;
    }
*/










    public static String cleanSql(String sql) {
        sql = sql.replace("\n", " ");
        sql = sql.replace("\t", " ");
        sql = sql.trim();
        if(sql.contains(";")){
            sql = sql.replace(";","");
        }
        return sql;
    }

    public static String checkBaseQueryOrder(String baseQuerySql){
        if(baseQuerySql.contains("order\\s+by\\s+ID")
                || baseQuerySql.contains("order\\s+by\\s+id")
                || baseQuerySql.contains("order\\s+by\\s+Id")
                || baseQuerySql.contains("order\\s+by\\s+iD")
                || baseQuerySql.contains("order\\s+by\\s+photoId")
                || baseQuerySql.contains("order\\s+by\\s+photoID")){
            return baseQuerySql;
        }
        baseQuerySql += " ORDER BY ID";// 根据ID排序
        return baseQuerySql;
    }


}
