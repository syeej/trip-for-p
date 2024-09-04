package team.seventhmile.tripforp.domain.user.service;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public TokenService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveRefreshToken(String username, String refreshToken) {
		redisTemplate.opsForValue().set(username, refreshToken, 1, TimeUnit.DAYS);
	}

	public String getRefreshToken(String username) {
		Object token = redisTemplate.opsForValue().get(username);
		return (token != null) ? token.toString() : null;
	}

	public void deleteRefreshToken(String username) {
		redisTemplate.delete(username);
	}

	public boolean isKeyExists(String username) {
		Boolean hasKey = redisTemplate.hasKey(username);
		return hasKey != null && hasKey;
	}
}

