package com.mk.test.dao;

import java.math.BigDecimal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mk.test.po.TestBean;

@Repository
public interface TestDao extends CrudRepository<TestBean, Long> {
    public TestBean findFirstByPrice(BigDecimal price);
}
