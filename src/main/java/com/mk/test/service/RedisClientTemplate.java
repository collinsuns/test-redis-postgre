package com.mk.test.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;


@Repository("redisClientTemplate")
public class RedisClientTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(RedisClientTemplate.class);
    
    private static final String startCursor = "0";


    
    @Autowired
    @Qualifier("jedisCluster")
    private JedisCluster jedisCluster;
    

    
    /**
     * 设置单个值
     * 
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        String result = null;
        
        if (jedisCluster == null) {
            return result;
        }

        result = jedisCluster.set(key, value);

        return result;
    }

    /**
     * 获取单个值
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        String result = null;
        if (jedisCluster == null) {
            return result;
        }

        result = jedisCluster.get(key);

        return result;
    }
    
    
    /**
     * 查看某个KEY是否存在
     * 
     * @param key
     * @return
     */
    public Boolean exists(String key) {
    	Boolean result = false;

        if (jedisCluster == null) {
        	throw new RuntimeException("Get Redis Source failed");
        }

        result = jedisCluster.exists(key);
        return result;
    }
    

    /**
     * 获取Key集合
     * 
     * @param key
     * @return
     */
    public List<String> getAllKeys() {
    	List<String> result = new ArrayList<String>();
        //Jedis jedis = redisDataSource.getRedisClient();
    	   	
    	if(jedisCluster == null)
    	{
    		return result;
    	}
        
        Map<String, JedisPool> jedisPoolList =  jedisCluster.getClusterNodes();
        if (jedisPoolList == null || jedisPoolList.size() <= 0) {
            return result;
        }

        Jedis jedis = null;
        
        ArrayList<Jedis> jList = new ArrayList<Jedis>();
        
        Iterator<Entry<String, JedisPool>>  iter =  jedisPoolList.entrySet().iterator();
        	
        while(iter.hasNext())
        {
        	Entry<String, JedisPool> entry = iter.next();
        	JedisPool pool = entry.getValue();
        	String info = entry.getKey();
    		try{
        		jedis = pool.getResource();
    		}
    		catch(Exception e)
    		{
    			//Catch 可能的runtime exception
    			LOG.error("Redis get source failed at " + info,e);
    		}
    		
    		if(jedis != null)
    		{
    			//break;
    			jList.add(jedis);
    		}
        }

        if(jList.isEmpty()||jList.size()<=0)
        {
        	throw new RuntimeException("Get Redis Source failed");
        }
        
        //去掉可能重复的元素
    	
        HashSet<String> set = new HashSet<String>();  
        for(Jedis j : jList)
        {
        	String scanCursor = startCursor;
        	ScanParams params = new ScanParams();
        	params.count(100);
        	params.match("*");
        	do
        	{
        		ScanResult<String> sResult = j.scan(scanCursor,params);
        		if(sResult!=null&&sResult.getResult()!=null)
        		{
        			set.addAll(sResult.getResult());
        		}

        		scanCursor = sResult.getStringCursor();
        		
        	}
        	while(!scanCursor.equals(startCursor));
        	
        	j.close();
        }
        result.addAll(set);
        LOG.info("Redis get element number: " + result.size());
		
        return result;
    }


}