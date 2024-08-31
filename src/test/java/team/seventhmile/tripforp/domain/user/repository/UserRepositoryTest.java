package team.seventhmile.tripforp.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.config.QuerydslConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
@Import(QuerydslConfig.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
	public void setUp() {
		// Given
		testUser = User.builder()
			.email("test@example.com")
			.password("password123")
			.nickname("testuser")
			.isDeleted(false)
			.role(Role.USER)
			.build();
		userRepository.save(testUser);
	}

	@Test
	public void testFindByEmail() {
		// When
		Optional<User> foundUser = userRepository.findByEmail("test@example.com");

		// Then
		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
		assertThat(foundUser.get().getNickname()).isEqualTo("testuser");
	}

	@Test
	public void testExistsByNickname() {
		// When
		Boolean exists = userRepository.existsByNickname("testuser");

		// Then
		assertThat(exists).isTrue();
	}

	@Test
	public void testExistsByNickname_NotFound() {
		// When
		Boolean exists = userRepository.existsByNickname("nonExistentNickname");

		// Then
		assertThat(exists).isFalse();
	}
}
