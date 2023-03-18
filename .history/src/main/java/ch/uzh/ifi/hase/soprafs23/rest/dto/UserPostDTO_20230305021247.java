package ch.uzh.ifi.hase.soprafs23.rest.dto;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

public class UserPostDTO {

  private String name;
  private long id;
  private String username;
  private String password;
  private UserStatus status;
  
  @JsonFormat(pattern="dd-MM-yyyy")
  private Date birthday;

  @JsonFormat(pattern="dd-MM-yyyy")
  private Date registerDate;
  private String token;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public long getId() {
    return id;
  }

  public void setID(Long id) {
    this.id = id;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
