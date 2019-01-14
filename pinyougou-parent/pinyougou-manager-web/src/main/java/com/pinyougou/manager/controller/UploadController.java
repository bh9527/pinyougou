package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {
    //直接从配置文件中注入值,MVC第一行已经把文件放入容器
    @Value("${FILE_SERVER_URL}")
    private  String find_server_url;
    //返回结果封到Result
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //从原始文件名截取   substring从哪里开始                     最后一个点开始
        String exName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);

        try {
            //使用工具类,读取配置文件  client客户端
            FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
            //使用客户端上传,传入字节,和名字
            String fileId = client.uploadFile(file.getBytes(), exName);
            //拼接得到图片完整地址
            String url=find_server_url+fileId;
            return new Result(true, url);

        }catch (Exception e){
            e.printStackTrace();
            return new Result(true, "上传失败");
        }

    }

}
