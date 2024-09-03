package team.seventhmile.tripforp.global.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.domain.user.service.TokenService;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private SecretKey secretKey;
	private final TokenService tokenService;

	@Value("${jwt.secret}")
	private String secret;

	// 생성자에서 SecretKey 및 TokenService 주입
	@PostConstruct
	public void init() {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public String getUsername(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("username", String.class);
	}

	public String getRole(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("role", String.class);
	}

	public String getCategory(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.get("category", String.class);
	}

	public Boolean isExpired(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
			.getExpiration().before(new Date());
	}

	// JWT 토큰 발급
	public String createJwt(String category, String username, String role, Long expiredMs) {

		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}

	// Access 토큰 재발행
	public ResponseEntity<?> reissueToken(HttpServletRequest request,
		HttpServletResponse response) {
		// 리프레시 토큰 가져오기
		String refresh = getRefreshTokenFromCookie(request);
		if (refresh == null) {
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		// 리프레시 토큰 만료 확인
		try {
			isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// 사용자 정보 가져오기
		String username = getUsername(refresh);
		String role = getRole(refresh);

		// 리프레시 토큰이 유효한지 확인
		String redisRefreshToken = tokenService.getRefreshToken(username);

		if (!refresh.equals(redisRefreshToken)) {
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		// 새로운 JWT 생성
		String newAccess = createJwt("access", username, role, 600000L);
		String newRefresh = createJwt("refresh", username, role, 86400000L);

		// Redis에 새로운 Refresh Token 저장 (기존 토큰 대체)
		tokenService.saveRefreshToken(username, newRefresh);

		// 응답 설정
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setHttpOnly(true);
		// cookie.setSecure(true); // HTTPS를 사용하는 경우에만 주석 해제
		// cookie.setPath("/"); // 필요에 따라 경로를 설정
		return cookie;
	}

}
