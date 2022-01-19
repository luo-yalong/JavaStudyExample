package com.lyl.ms.controller;

import com.lyl.ms.service.MsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:17
 */
@RestController
@RequestMapping("/ms")
@RequiredArgsConstructor
public class MsController {
    private final MsService msService;


    /**
     * 使用synchronized来保证不超买
     * @param id 商品id
     * @return 秒杀信息
     */
    @GetMapping("/kill")
    public String kill(Integer id){
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.kill(id);
    }

    /**
     * 乐观锁版本的秒杀
     * @param id 商品id
     * @return str
     */
    @GetMapping("/killByOptimismLock")
    public String killByOptimismLock(Integer id){
        if (id == null){
            //这里，只是简单的校验一下，可以使用jsr303搭配全局异常处理，校验参数
            return "商品id不能为空";
        }
        return msService.killByOptimismLock(id);
    }

}
