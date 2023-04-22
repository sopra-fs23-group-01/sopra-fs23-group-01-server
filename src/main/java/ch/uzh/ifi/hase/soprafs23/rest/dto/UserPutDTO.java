package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.ReadyStatus;
import ch.uzh.ifi.hase.soprafs23.constant.UserGender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserPutDTO {
    private Long id;
    private String username;
    private String token;
    private ReadyStatus readyStatus;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date registerDate;

    private String email;
    @JsonProperty("gender")
    private UserGender gender;

    private float rateDe;

    private float rateUn;

    private String intro;

    private String avatarUrl;
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    public float getRateDe() {
        return rateDe;
    }

    public void setRateDe(float rateDe) {
        this.rateDe = rateDe;
    }

    public float getRateUn() {
        return rateUn;
    }

    public void setRateUn(float rateUn) {
        this.rateUn = rateUn;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public ReadyStatus getReadyStatus() {
        return readyStatus;
    }

    public void setReadyStatus(ReadyStatus readyStatus) {
        this.readyStatus = readyStatus;
    }
}
