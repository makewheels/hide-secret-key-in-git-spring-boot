package com.github.makewheels;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.util.Base64;

@RestController
@RequestMapping("key")
public class KeyController {

    @GetMapping("generateKeyPair")
    @ApiOperation(value = "生成公私钥对")
    public String generateKeyPair() {
        KeyPair keyPair = RSAUtil.generateKeyPair();
        if (keyPair == null)
            return null;
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("publicKey", publicKey);
        jsonObject.put("privateKey", privateKey);
        return jsonObject.toJSONString();
    }
}
