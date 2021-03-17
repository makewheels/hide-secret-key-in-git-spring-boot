package com.github.makewheels;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("key")
@Slf4j
public class KeyController {
    @Resource
    private KeyService keyService;

    @GetMapping("generateKeyPair")
    @ApiOperation(value = "生成公私钥对")
    public String generateKeyPair() {
        return JSON.toJSONString(keyService.generateKeyPair());
    }

    @GetMapping("generateKeyPairAndSave")
    @ApiOperation(value = "生成公私钥对，并保存到本地")
    public String generateKeyPairAndSave() {
        return JSON.toJSONString(keyService.generateKeyPairAndSave());
    }

    @GetMapping("encrypt")
    @ApiOperation(value = "加密")
    public String encrypt(@RequestParam String data) {
        return JSON.toJSONString(keyService.encrypt(data));
    }

    @GetMapping("decrypt")
    @ApiOperation(value = "解密")
    public String decrypt(@RequestParam String data) {
        return JSON.toJSONString(keyService.decrypt(data));
    }

    @Value("${demokey}")
    private String demokey;

    @GetMapping("testDemokey")
    @ApiOperation(value = "测试注入")
    public String testDemokey() {
        log.info(demokey);
        return demokey;
    }

}
