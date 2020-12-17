package com.taotao.test.jedis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;

public class TestJedis {
    @Test
    public void test01() {
        /*Jedis jedis = new Jedis("192.168.186.102", 6379);
        jedis.set("key1234", "value");
        System.out.println(jedis.get("key1234"));
        jedis.close();*/
    }
    @Test
    public void test02() {
        /*JedisPool jedisPool = new JedisPool("192.168.186.102", 6379);
        Jedis jedis = jedisPool.getResource();
        jedis.set("key1001", "hahah");
        System.out.println(jedis.get("key1001"));
        jedis.close();
        jedisPool.close();*/
    }
    @Test
    public void test03() {
        /*HashSet<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.186.102", 7001));
        nodes.add(new HostAndPort("192.168.186.102", 7002));
        nodes.add(new HostAndPort("192.168.186.102", 7003));
        nodes.add(new HostAndPort("192.168.186.102", 7004));
        nodes.add(new HostAndPort("192.168.186.102", 7005));
        nodes.add(new HostAndPort("192.168.186.102", 7006));
        JedisCluster jedisCluster = new JedisCluster(nodes);

        jedisCluster.set("keycluster", "valuecluster");
        System.out.println(jedisCluster.get("keycluster"));
        jedisCluster.close();
*/
    }
}
