package com.winchannel.funccode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析Func_Code SQL xml配置
 */
public class FunccodeUtil {

    private static String resourceFilePath = "spring/config/func_code_sql.xml";
    private static String resourceFilePath1 = "D:\\func_code_sql.xml";

    private static String TYPE_SPE = "SPE";
    private static String TYPE_SRA = "SRA";


    public static void main(String[] args) {
        Map<String, Object> sqlMap = getSqlList();

        // 系统平台自自身
        String sfaSql = (String) sqlMap.get("sfaSql");
        // 个性化定制sql
        List<String> speSqlList = (ArrayList) sqlMap.get("speList");

        System.out.println(sfaSql);
        System.out.println(speSqlList.size());

    }

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
                    speList.add(speSql);
                }
            }
        }

        // 系统平台sql元素
        Element select_SFA = root_querys.element("select_SFA");
        String sfaSql = select_SFA.getText();
        sfaSql = cleanSql(sfaSql);
        sqlMap.put("sfaSql", sfaSql);
        sqlMap.put("speList", speList);
        return sqlMap;
    }


    public static String cleanSql(String sql) {
        sql = sql.replace("\n", " ");
        sql = sql.replace("\t", " ");
        sql = sql.trim();
        return sql;
    }



}
