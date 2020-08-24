package cn.link.inkmall.pms.controller;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.pms.utils.OssTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("pms/oss")
public class OssController {

    @Autowired
    OssTemplate ossTemplate;

    @GetMapping("policy")
    public ResponseVo<Map<String, String>> getPolicy(){

        Map<String, String> policy = ossTemplate.getPolicy();

        return ResponseVo.ok(policy);
    }
}
