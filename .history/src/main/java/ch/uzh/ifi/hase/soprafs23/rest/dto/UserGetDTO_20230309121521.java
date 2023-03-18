package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

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
