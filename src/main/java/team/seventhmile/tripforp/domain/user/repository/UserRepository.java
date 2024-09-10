package team.seventhmile.tripforp.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.seventhmile.tripforp.domain.user.dto.UserIdResponse;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Boolean existsByNickname(String nickname);

	@Query("SELECT new team.seventhmile.tripforp.domain.user.dto.UserIdResponse(u.id) FROM User u WHERE u.email = :email")
	Optional<UserIdResponse> findUserIdByEmail(@Param("email") String email);}
