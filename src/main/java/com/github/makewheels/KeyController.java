package com.github.makewheels;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("key")
public class KeyController {
    @Resource
    private KeyService keyService;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("generateKeyPair")
    @ApiOperation(value = "生成公私钥对")
    public String generateKeyPair() {
        return JSON.toJSONString(keyService.generateKeyPair());
    }

    @GetMapping("generateKeyPairAndSave")
    @ApiOperation(value = "生成公私钥对，并且保存到本地")
    public String generateKeyPairAndSave() {
        return JSON.toJSONString(keyService.generateKeyPair());
    }
}
