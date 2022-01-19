package com.lyl.ms.controller;

import com.lyl.ms.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("stock")
public class StockController {
    @Resource
    private OrderService orderService;

    @GetMapping("/kill")
    public String kill(Integer id) {
        //处理秒杀业务逻辑
        try {
                int orderId = orderService.kill(id);
                log.info("秒杀成功：订单ID = " + orderId);
                return "秒杀成功：订单ID = " + orderId;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("秒杀:{}", e.getMessage());
            return e.getMessage();
        }
    }
}
