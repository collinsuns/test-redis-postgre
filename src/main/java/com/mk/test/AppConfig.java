package com.mk.test;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

@Configuration
@ComponentScan(basePackages = "com.mk.test")
public class AppConfig {
    @Autowired
    private JedisConfigProperties jedisConfigProp;

    @Autowired
    private GenericObjectPoolConfig poolConfig;

    @Bean("jedisPoolConfig")
    public GenericObjectPoolConfig getPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        if (jedisConfigProp.getPoolMaxIdle() != null) {
            poolConfig.setMaxIdle(jedisConfigProp.getPoolMaxIdle());
        }
        if (jedisConfigProp.getPoolMaxTotal() != null) {
            poolConfig.setMaxTotal(jedisConfigProp.getPoolMaxTotal());
        }

        if (jedisConfigProp.getPoolMinIdle() != null) {
            poolConfig.setMinIdle(jedisConfigProp.getPoolMinIdle());
        }

        return poolConfig;
    }

    @Bean("jedisCluster")
    public JedisCluster getJedisCluster() throws Exception {

        if (jedisConfigProp == null) {
            throw new ServiceException("node address error !");
        }

        int clusterConnTimeout = 3000;
        int soTimeout = 1000;
        int maxRedirections = 5;

        if ((jedisConfigProp.getClusterConnectionTimeout() != null) || (jedisConfigProp.getClusterSoTimeout() != null)
                || (jedisConfigProp.getClusterMaxRedirections() != null)) {
            clusterConnTimeout = jedisConfigProp.getClusterConnectionTimeout().intValue();
            soTimeout = jedisConfigProp.getClusterSoTimeout().intValue();
            maxRedirections = jedisConfigProp.getClusterMaxRedirections().intValue();
        }

        String[] serverArray = jedisConfigProp.getClusterNodes().split(",");// 获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            if (ipPortPair.length != 2) {
                throw new ParseException("node address error !", ipPort.length() - 1);
            }
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        if (poolConfig == null) {
            poolConfig = new GenericObjectPoolConfig();
        }

        return new JedisCluster(nodes, clusterConnTimeout, soTimeout, maxRedirections, poolConfig);

    }
}
