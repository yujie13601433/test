package tec.cache.util;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import tec.cache.impl.RedisClusterDAOImpl;

import java.util.HashSet;
import java.util.Set;

public class RedisClusterFactory
{
	RedisClusterDAOImpl cacheDAO;
	String hostAndPortConf;
	
	private JedisCluster jedisCluster = null;
	private GenericObjectPoolConfig jedisPoolConfig;
	private int connectionTimeout = 1000;
	private int soTimeout = 1000;
	private int maxRedirections = 5;
	private String password = "yujie123";
	

	public String getHostAndPortConf()
	{
		return hostAndPortConf;
	}

	public void setHostAndPortConf(String hostAndPortConf)
	{
		this.hostAndPortConf = hostAndPortConf;
	}

	public RedisClusterDAOImpl getCacheDAO()
	{
		return cacheDAO;
	}

	public void setCacheDAO(RedisClusterDAOImpl cacheDAO)
	{
		this.cacheDAO = cacheDAO;
	}

	/**
	 * 初始化
	 */
	public void initialize()
	{
		if (StringUtils.isNotEmpty(hostAndPortConf))
		{
			constructorRedisSingle(hostAndPortConf);
		}
	}

	/**
	 * 通过参数构造redis single
	 * 参数定义参考 https://www.cnblogs.com/benwu/articles/8616141.html
	 * @return
	 */
	private void constructorRedisSingle(String hostAndPortConf)
	{
		String[] hostAndPorts = hostAndPortConf.split(",");
		Set<HostAndPort> redisClusterNodeList = new HashSet<HostAndPort>();
		for (String hostAndPort : hostAndPorts)
		{
			String[] ipAndPort = hostAndPort.split(":");
			String ip = ipAndPort[0];
			String port = ipAndPort[1];
			HostAndPort redisClusterHostAndPort = new HostAndPort(ip, Integer.valueOf(port));
			redisClusterNodeList.add(redisClusterHostAndPort);
			
		}
		
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(2000);

		jedisPoolConfig.setMaxIdle(800);
		//		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setMaxWaitMillis(1000);

		jedisPoolConfig.setTestOnBorrow(false);
		jedisPoolConfig.setTestOnReturn(false);
		//Idle时进行连接扫描
		jedisPoolConfig.setTestWhileIdle(true);
		//表示idle object evitor两次扫描之间要sleep的毫秒数
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(300000);
		//表示idle object evitor每次扫描的最多的对象数
		jedisPoolConfig.setNumTestsPerEvictionRun(-1);
		//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		jedisPoolConfig.setMinEvictableIdleTimeMillis(600000);
		
		jedisCluster = new JedisCluster(redisClusterNodeList, this.connectionTimeout, this.soTimeout, this.maxRedirections,
					password, this.jedisPoolConfig);
		cacheDAO.setJedisCluster(jedisCluster);
	}
}
