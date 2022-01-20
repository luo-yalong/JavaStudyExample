package com.lyl.ms;

import com.lyl.ms.dao.StockDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MsApplicationTests {

    @Autowired
    private StockDao stockDao;

    @Test
    void testStockDao() {
        stockDao.selectList(null)
                .forEach(System.out::println);
    }

}
