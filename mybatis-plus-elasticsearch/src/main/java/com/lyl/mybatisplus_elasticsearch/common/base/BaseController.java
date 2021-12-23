package com.lyl.mybatisplus_elasticsearch.common.base;

import com.lyl.mybatisplus_elasticsearch.service.NbaPlayerService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 罗亚龙
 * @date 2021/12/23 14:20
 */
@Component
public class BaseController {

    @Resource
    public NbaPlayerService nbaPlayerService;
}
