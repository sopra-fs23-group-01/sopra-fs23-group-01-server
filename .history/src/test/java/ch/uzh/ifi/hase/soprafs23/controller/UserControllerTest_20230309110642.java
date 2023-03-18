
package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    public void getAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("test Username1");
        user.setPassword("Test Password1");
        user.setToken("1");

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())));
    }


    //create user with valid input
    @Test
    public void createUser_validInput() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    //create user with invalid input
    @Test
    public void createUser_invalidInput() throws Exception {

        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setToken("1");
        user.setPassword("123");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("TestUser");
        userPostDTO.setPassword("123");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void getUser_validId() throws Exception {

        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setToken("1");
        user.setPassword("123");

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setUsername(user.getUsername());

        // when/then -> do the request + validate the result
        Mockito.when(userService.userProfileById(user.getId()))
                .thenReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO));

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void getUser_invalidId() throws Exception {

        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        user.setToken("1");
        user.setPassword("123");

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setUsername(user.getUsername());

        given(userService.userProfileById((long) 200))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO));

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_validInput() throws Exception {

        //given
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setUsername("Old Name");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId(1L);
        userPutDTO.setUsername("New Name");

        doNothing().when(userService).userEditProfile(Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/users/" + oldUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

    }

    @Test
    public void updateUser_invalidInput_IdNotFound() throws Exception {

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("newUsername");

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService)
                .userEditProfile(Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void loginUser_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setToken("1");
        user.setPassword("123");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("TestUser");
        userPostDTO.setPassword("123");

        given(userService.loginUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }
}
