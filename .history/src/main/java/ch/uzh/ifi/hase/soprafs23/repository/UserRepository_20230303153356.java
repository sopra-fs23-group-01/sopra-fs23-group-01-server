package ch.uzh.ifi.hase.soprafs23.repository;
//import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  //User findByStatus(UserStatus status);
  User getOne( Long id);
}

