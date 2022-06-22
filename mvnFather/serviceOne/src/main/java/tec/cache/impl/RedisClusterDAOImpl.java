package tec.cache.impl;

import tec.cache.ICacheDAO;
import redis.clients.jedis.JedisCluster;
import tec.cache.ICacheDAO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisClusterDAOImpl implements ICacheDAO
{
	JedisCluster jedisCluster;

	public JedisCluster getJedisCluster()
	{
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster)
	{
		this.jedisCluster = jedisCluster;
	}

	public static byte[] serialize(Object object)
	{
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return baos.toByteArray();
		}
		catch (Exception localException)
		{
		}
		return null;
	}

	public static Object deserialize(byte[] bytes)
	{
		ByteArrayInputStream bais = null;
		try
		{
			if (bytes == null || bytes.length == 0)
			{
				return null;
			}
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception localException)
		{
		}
		return null;
	}
	@Override
	public Object get(String key)
	{
		Object obj = null;
		try
		{
			byte[] value = jedisCluster.get(key.getBytes());
			obj = deserialize(value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public String getString(String key)
	{
		Object obj = null;
		try
		{
			byte[] value = jedisCluster.get(key.getBytes());
			obj = deserialize(value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return (String)obj;
	}

	@Override
	public Long getCard(String key)
	{
		Long l = 0L;
		try
		{
			l = jedisCluster.scard(key.getBytes());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		return l;
	}

	@Override
	public boolean add(String key, Object value)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean addString(String key, String value)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean add(String key, Object value, boolean timeType)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean add(String key, Object value, long expireInMilliSeconds)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			jedisCluster.pexpire(key.getBytes(), expireInMilliSeconds);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean sAdd(String key, Object value, long expireInMilliSeconds)
	{
		try
		{
			jedisCluster.sadd(key.getBytes(), serialize(value));
			jedisCluster.pexpire(key.getBytes(), expireInMilliSeconds);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean sAdd(String key, Object value, boolean timeType)
	{
		try
		{
			jedisCluster.sadd(key.getBytes(), serialize(value));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean replace(String key, Object value)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean replace(String key, Object value, long expireInMilliSeconds)
	{
		try
		{
			jedisCluster.set(key.getBytes(), serialize(value));
			jedisCluster.pexpire(key.getBytes(), expireInMilliSeconds);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete(String key)
	{
		try
		{
			jedisCluster.del(key.getBytes());
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteString(String key)
	{
		try
		{
			jedisCluster.del(key.getBytes());
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean expireAt(String key, Date date)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean expire(String key, long timeout, TimeUnit timeUnit)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean setNx(String key, Object value)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<byte[]> lRange(byte[] key, long start, long end)
	{
		List<byte[]> list = null;
		try
		{
			list = jedisCluster.lrange(key, start, end);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Long getExpire(String key)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0L;
	}

	@Override
	public Long getExpire(String key, TimeUnit timeUnit)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0L;
	}

	@Override
	public Object getStringKey(String key)
	{
		try
		{
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
}
