package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

import ch.uzh.ifi.hase.soprafs23.constant.UserGender;
import com.fasterxml.jackson.annotation.JsonFormat;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
public class UserGetDTO {

  private Long id;
  private String username;
  private UserStatus status;
  
  @JsonFormat(pattern="dd-MM-yyyy")
  private Date birthday;

  @JsonFormat(pattern="dd-MM-yyyy")
  private Date registerDate;

    private String email;

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Date getRegisterDate() {
    return registerDate;
  }

  public void setRegisterDate(Date registerDate) {
    this.registerDate = registerDate;
  }

  
  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }
}
