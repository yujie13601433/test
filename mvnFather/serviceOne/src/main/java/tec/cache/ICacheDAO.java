package tec.cache;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by alabo on 2015/3/4.
 */
public interface ICacheDAO
{
	Object get(String key);
	
	String getString(String key);

	Long getCard(String key);

	boolean add(String key, Object value);
	
	boolean addString(String key, String value);

	boolean add(String key, Object value, boolean timeType);

	boolean add(String key, Object value, long expireInMilliSeconds);

	boolean sAdd(String key, Object value, long expireInMilliSeconds);

	boolean sAdd(String key, Object value, boolean timeType);

	boolean replace(String key, Object value);

	boolean replace(String key, Object value, long expireInMilliSeconds);

	boolean delete(String key);
	
	boolean deleteString(String key);

	boolean expireAt(String key, Date date);

	boolean expire(String key, long timeout, TimeUnit timeUnit);

	Boolean setNx(String key, Object value);

	List<byte[]> lRange(byte[] key, long start, long end);

	Long getExpire(String key);

	Long getExpire(String key, TimeUnit timeUnit);
	
	/**
	 * 用于取序列化后的value
	 * @param key
	 * @return
	 */
	Object getStringKey(String key);
}
