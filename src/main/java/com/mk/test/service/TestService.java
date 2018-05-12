package com.mk.test.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mk.test.dao.TestDao;
import com.mk.test.po.TestBean;

@Service
public class TestService {
    @Autowired
    RedisClientTemplate redisClientTemplate;
    
    @Autowired
    private TestDao testDao;

    @Transactional(propagation = REQUIRED)
    public void testService() {
        // redis
        redisClientTemplate.set("private_area_vin111", "hello vin111");
        String value = redisClientTemplate.get("private_area_vin111");
        System.out.println("redis value" + value);
        
        // postgres
        TestBean test = new TestBean();
        test.setPrice(new BigDecimal(1000.00));
        testDao.save( test);
        
        TestBean test2 = testDao.findFirstByPrice(new BigDecimal(1000.00) );
        System.out.println("find test2" + test2);
        
         
    }
}
