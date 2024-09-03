package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//DB에서 조회
		Optional<User> optionalUser = userRepository.findByEmail(username);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
			return new CustomUserDetails(user);
		}

		return null;
	}
}
