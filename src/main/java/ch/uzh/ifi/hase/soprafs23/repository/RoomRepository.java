package ch.uzh.ifi.hase.soprafs23.repository;
//import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;


@Repository("roomRepository")
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room getOne(@RequestParam(required = true) Long id);
}

