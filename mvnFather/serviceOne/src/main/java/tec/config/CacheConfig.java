package tec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tec.cache.ICacheDAO;
import tec.cache.impl.RedisClusterDAOImpl;
import tec.cache.util.RedisClusterFactory;

@Configuration
@RefreshScope
public class CacheConfig {
    @Value("${user.commonHostAndPortConf}")
    private String commonHostAndPortConf;
    @Bean("redisClusterFactory")
    public RedisClusterFactory redisClusterFactory()
    {
        RedisClusterFactory redisClusterFactory = new RedisClusterFactory();
        return redisClusterFactory;
    }
    @Bean("commonCache")
    public ICacheDAO commonCache(RedisClusterFactory redisClusterFactory)
    {
        RedisClusterDAOImpl cacheDAO = new RedisClusterDAOImpl();
        redisClusterFactory.setHostAndPortConf(commonHostAndPortConf);
        redisClusterFactory.setCacheDAO(cacheDAO);
        redisClusterFactory.initialize();
        return cacheDAO;
    }

}
