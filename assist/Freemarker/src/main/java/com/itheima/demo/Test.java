package com.itheima.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Test {

    public static void main(String[] args) throws IOException, TemplateException {
        //1.创建一个配置对象(参数为版本,否则报错)
        Configuration configuration=new Configuration(Configuration.getVersion());
        //2.设置模板所在 [目录](setDirectoryForTemplateLoading)
        configuration.setDirectoryForTemplateLoading(new File("C:\\Users\\XML\\IdeaProjects\\pinyougou\\assist\\Freemarker\\src\\main\\resources"));
        //3.设置字符集
        configuration.setDefaultEncoding("utf-8");
        //4.获取模板对象
        Template template = configuration.getTemplate("test.ftl");
        //5.创建数据模型(可以是对象,也可以是map)
        Map map=new HashMap();
        map.put("name","王大帅");
        map.put("message","欢迎来到神器的品优购世界");
        map.put("success",false);
        //创建list集合
        List goodsList=new ArrayList();
        //创建map集合
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        //创建map集合
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        //创建map集合
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        //存入到集合
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);

        //把list存到最开始的map中
        map.put("goodsList", goodsList);


        map.put("today",new Date());

        map.put("point",123456);
        //6.创建一个输出流对象[输出到自定义名字的页面]
        Writer out =new FileWriter("E:\\TEST\\TEST.html");
        //7.输出(参数1:数据, 参数2:输出流)
        template.process(map,out);
        //8.关闭流
        out.close();
    }
}
