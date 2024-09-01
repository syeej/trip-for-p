package team.seventhmile.tripforp.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void signupTest() throws Exception {
		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("testPassword");
		userDto.setNickname("testNickname");
		userDto.setRole(Role.USER);

		Mockito.doNothing().when(userService).register(any(UserDto.class));

		mockMvc
			.perform(MockMvcRequestBuilders.post("/api/users/registration")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDto)))
			.andExpect(status().isOk())
			.andExpect(content().string("User registered successfully"));

	}

	@Test
	void checkDuplicatedNicknameTest() throws Exception {
		String nickname = "testUser";

		// 중복된 닉네임일 경우
		Mockito.when(userService.isDuplicatedNickname(eq(nickname))).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/nickname-verification")
				.param("nickname", nickname))
			.andExpect(status().isConflict())
			.andExpect(content().string("true"))
			.andDo(print());

		// 중복되지 않은 닉네임일 경우
		Mockito.when(userService.isDuplicatedNickname(eq(nickname))).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/nickname-verification")
				.param("nickname", nickname))
			.andExpect(status().isOk())
			.andExpect(content().string("false"))
			.andDo(print());
	}

}
