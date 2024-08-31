package team.seventhmile.tripforp.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.user.AlreadyUsedEmailException;
import team.seventhmile.tripforp.global.exception.user.WithdrawnUserException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	public void testRegister_NewUser() {
		// Given
		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("testPassword");
		userDto.setNickname("testNickname");
		userDto.setRole(Role.USER);

		when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
		when(bCryptPasswordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

		// When
		userService.register(userDto);

		// Then
		verify(userRepository, times(1)).save(argThat(user ->
			user.getEmail().equals(userDto.getEmail()) &&
				user.getNickname().equals(userDto.getNickname()) &&
				user.getPassword().equals("encodedPassword") &&
				!user.getIsDeleted() &&
				user.getRole() == Role.USER
		));
	}

	@Test
	public void testRegister_AlreadyUsedEmail() {
		// Given
		User existingUser = User.builder()
			.email("test@example.com")
			.password("password123")
			.nickname("existingUser")
			.isDeleted(false)
			.role(Role.USER)
			.build();

		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("testPassword");
		userDto.setNickname("testNickname");
		userDto.setRole(Role.USER);

		when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(existingUser));

		// When & Then
		try {
			userService.register(userDto);
		} catch (AlreadyUsedEmailException e) {
			assertThat(e.getMessage()).isEqualTo("이미 사용중인 이메일입니다.");
		}
	}

	@Test
	public void testRegister_WithdrawnUser() {
		// Given
		User withdrawnUser = User.builder()
			.email("test@example.com")
			.password("password123")
			.nickname("withdrawnUser")
			.isDeleted(true)
			.role(Role.USER)
			.build();

		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("testPassword");
		userDto.setNickname("testNickname");
		userDto.setRole(Role.USER);

		when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(withdrawnUser));

		// When & Then
		try {
			userService.register(userDto);
		} catch (WithdrawnUserException e) {
			assertThat(e.getMessage()).isEqualTo("탈퇴한 사용자입니다.");
		}
	}

	@Test
	public void testIsDuplicatedNickname() {
		// Given
		String nickname = "testuser";
		when(userRepository.existsByNickname(nickname)).thenReturn(true);

		// When
		Boolean result = userService.isDuplicatedNickname(nickname);

		// Then
		assertThat(result).isTrue();
		verify(userRepository, times(1)).existsByNickname(nickname);
	}

}
