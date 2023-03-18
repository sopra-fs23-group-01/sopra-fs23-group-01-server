package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_success() {
        // given
        User user = new User();
        user.setUsername("username123");
        user.setPassword("password123");
        user.setToken("token123");
        user.setStatus(UserStatus.ONLINE);
        user.setRegisterDate(new Date());
        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        Assertions.assertNotNull(found);
        Assertions.assertEquals(user.getUsername(), found.getUsername());
        Assertions.assertEquals(user.getPassword(), found.getPassword());
        Assertions.assertEquals(user.getToken(), found.getToken());
        Assertions.assertEquals(user.getStatus(), found.getStatus());
        Assertions.assertEquals(user.getRegisterDate(), found.getRegisterDate());
    }

    @Test
    public void getOne_success() {
        // given
        User user = new User();
        user.setUsername("username123");
        user.setPassword("password123");
        user.setToken("token123");
        user.setStatus(UserStatus.ONLINE);
        user.setRegisterDate(new Date());
        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.getOne(user.getId());

        // then
        Assertions.assertNotNull(found);
        Assertions.assertEquals(user.getUsername(), found.getUsername());
        Assertions.assertEquals(user.getPassword(), found.getPassword());
        Assertions.assertEquals(user.getToken(), found.getToken());
        Assertions.assertEquals(user.getStatus(), found.getStatus());
        Assertions.assertEquals(user.getRegisterDate(), found.getRegisterDate());
    }
}
