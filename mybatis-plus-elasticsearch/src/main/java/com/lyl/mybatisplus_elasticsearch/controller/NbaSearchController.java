package com.lyl.mybatisplus_elasticsearch.controller;

import cn.hutool.core.util.StrUtil;
import com.lyl.mybatisplus_elasticsearch.common.base.BaseController;
import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 罗亚龙
 * @date 2021/12/23 14:19
 */
@RestController
public class NbaSearchController extends BaseController {

    /**
     * 导入数据库中全部的数据到es中
     *
     * @return success
     */
    @PostMapping("/importAll")
    public String importAll() {
        nbaPlayerService.insertAll();
        return "success";
    }

    /**
     * 通过名字查找球员
     *
     * @param displayNameEn 英文名字
     * @return list
     */
    @GetMapping("/searchMatch")
    public List<NbaPlayer> searchMatch(@RequestParam(required = false,value = "displayNameEn") String displayNameEn) {
        return nbaPlayerService.searchMatch("displayNameEn", displayNameEn);
    }

    /**
     * 通过国家或队名查询数据
     * @param country 国家
     * @param teamName 队名
     * @return list
     */
    @GetMapping("/searchTerm")
    public List<NbaPlayer> searchTerm(@RequestParam(required = false, value = "country") String country,
                                      @RequestParam(required = false, value = "teamName") String teamName) {
        System.out.println("country = " + country + " , teamName = " + teamName);
        return StrUtil.isNotBlank(country) ?
                nbaPlayerService.searchTerm("country.keyword", country) :
                nbaPlayerService.searchTerm("teamName.keyword", teamName);
    }

    /**
     * 通过字母查询球员
     * @param displayNameEn 英文名称
     * @return list
     */
    @GetMapping("/searchMatchPrefix")
    public List<NbaPlayer> searchMatchPrefix(@RequestParam(required = false,value = "displayNameEn") String displayNameEn){
        System.out.println("displayNameEn = " + displayNameEn);
        return nbaPlayerService.searchMatchPrefix("displayNameEn",displayNameEn);
    }


}
