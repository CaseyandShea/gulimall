package com.learn.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    OSS ossClient;
    @Test
    void contextLoads() throws FileNotFoundException {
        ossClient.putObject("education-project","EA1A7364.JPG",new FileInputStream("/Users/casey/Downloads/精修/调好色/EA1A7364.JPG"));
        ossClient.shutdown();

        System.out.println("上传成功");
    }

}
