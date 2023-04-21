package ch.uzh.ifi.hase.soprafs23.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import org.junit.jupiter.api.Test;

class RoomTest {
    @Test
    void testAssignCardsAndRoles() {
        Room room = new Room();
        User user = new User();
        user.setName("Test User");
        user.setId((long) 1);
        user.setUsername("testuser");
        user.setToken("123456");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("testPassword");
        user.setRegisterDate(new Date());
        user.setBirthday(new Date());
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        room.addRoomPlayer(Optional.of(user));
        room.addRoomPlayer(Optional.of(user2));
        room.addRoomPlayer(Optional.of(user3));
        room.addRoomPlayer(Optional.of(user4));
        room.assignCardsAndRoles();

        List<User> roomPlayers = room.getRoomPlayers();
        boolean foundFalseRole = false;
        int falseRoleIndex = -1;
        for (int i = 0; i < roomPlayers.size(); i++) {
            User player = roomPlayers.get(i);
            if (!player.getRole() && player.getCard().equals("pear")) {
                assertFalse(foundFalseRole, "More than one player has false role");
                foundFalseRole = true;
                falseRoleIndex = i;
            } else {
                assertTrue(player.getRole());
                assertTrue(player.getCard().equals("apple"));
            }
            System.out.println(player.getRole());
            System.out.println(player.getCard());
        }
        assertTrue(foundFalseRole, "No player has false role");
        assertTrue(falseRoleIndex >= 0 && falseRoleIndex < roomPlayers.size(), "Invalid index for false role player");
    }


}