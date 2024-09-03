package team.seventhmile.tripforp.domain.user.service;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public TokenService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveRefreshToken(String username, String refreshToken) {
		// Refresh token을 Redis에 저장 (만료 시간: 24시간)
		redisTemplate.opsForValue().set(username, refreshToken, 1, TimeUnit.DAYS);
	}

	public String getRefreshToken(String username) {
		return redisTemplate.opsForValue().get(username);
	}

	public void deleteRefreshToken(String username) {
		redisTemplate.delete(username);
	}
}

