package ch.uzh.ifi.hase.soprafs23.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserPutDTO {
    private Long id;

    private String username;
    private String intro;
    private String token;
    @JsonFormat(pattern="dd.MM.yyyy", locale = "de_CH")
    private Date birthday;
    @JsonFormat(pattern="dd.MM.yyyy", locale = "de_CH")
    private Date creationDate;
    private String gender;
    private String profilePictureLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
