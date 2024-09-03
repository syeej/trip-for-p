package team.seventhmile.tripforp.domain.user.service;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import team.seventhmile.tripforp.domain.user.entity.User;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

//		Collection<GrantedAuthority> collection = new ArrayList<>();
//
//		collection.add(new GrantedAuthority() {
//
//			@Override
//			public String getAuthority() {
//
//				return user.getRole().toString();
//			}
//		});

		return Collections.<GrantedAuthority>singletonList(
			new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
	}

	@Override
	public String getPassword() {

		return user.getPassword();
	}

	// user email
	@Override
	public String getUsername() {

		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}
}
