package team.seventhmile.tripforp.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import team.seventhmile.tripforp.domain.user.service.TokenService;
import team.seventhmile.tripforp.global.jwt.CustomLogoutFilter;
import team.seventhmile.tripforp.global.jwt.JwtFilter;
import team.seventhmile.tripforp.global.jwt.JwtUtil;
import team.seventhmile.tripforp.global.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtUtil jwtUtil;

    private final TokenService tokenService;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        LoginFilter loginFilter = new LoginFilter(
            authenticationManager(authenticationConfiguration), jwtUtil, tokenService);
        loginFilter.setFilterProcessesUrl("/api/users/signin");

        http
            .csrf((auth) -> auth.disable());

        http
            .formLogin((auth) -> auth.disable());

        http
            .httpBasic((auth) -> auth.disable());

        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/api").permitAll()
                // users
                .requestMatchers("/api/users/registration", "/api/users/nickname-verification",
                    "/api/users/signin", "/api/users/password/renewal", "/api/mails/**").permitAll()
                .requestMatchers("/api/users/reissue", "/api/users/signout", "/api/users/me/**",
                    "/api/users/deletion").hasAnyRole("ADMIN", "USER")
                // plans
                .requestMatchers(HttpMethod.GET, "/api/plans/me").hasRole("USER")
                .requestMatchers(HttpMethod.GET,"/api/plans", "/api/plans/popular-plans", "/api/plans/popular-places","/api/plans/*").permitAll()
                // plan-likes
                .requestMatchers("/api/plan-likes/**").hasAnyRole("USER")
                // magazines
                .requestMatchers(HttpMethod.GET, "/api/magazines", "/api/magazines/*").permitAll()
                // free-posts
                .requestMatchers(HttpMethod.GET, "/api/free-posts/me").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/free-posts", "/api/free-posts/*", "/api/free-posts/*/comments").permitAll()
                // review-posts
                .requestMatchers(HttpMethod.GET, "/api/review-posts/me").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/review-posts", "/api/review-posts/*", "/api/review-posts/*/comments").permitAll()
                // alan
                .requestMatchers("/api/alan", "/api/alan/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated());

        //JWTFilter 등록
        http
            .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
            .addFilterBefore(new CustomLogoutFilter(jwtUtil, tokenService), LogoutFilter.class);

        return http.build();
    }
}