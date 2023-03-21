package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface RoomRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User getOne(@RequestParam(required = true) Long id);
}
