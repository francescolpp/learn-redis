package pooling;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolingTest {

    public static final int ORIGINAL_PORT = 6379;
    private static GenericContainer redis;
    private static JedisPool jedisPool;

    @BeforeAll
    static void setUpClass() {
        redis = new GenericContainer<>("redis:6.2.6-alpine")
                .withExposedPorts(ORIGINAL_PORT)
                .waitingFor(Wait.forListeningPort());
        redis.start();
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(2);
        jedisPoolConfig.setMaxTotal(2);
        jedisPool = new JedisPool(jedisPoolConfig, redis.getHost(), redis.getMappedPort(ORIGINAL_PORT));
    }

    @AfterAll
    static void teardownClass() {
        redis.stop();
    }

    @Test
    void connectionPoolTest() {
        jedisPool.getResource().set("key", "value");
        Assertions.assertEquals("value", jedisPool.getResource().get("key"));
    }
}
