package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = true)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private String password;

    @Column
    private String email;

    @Column
    private UserGender gender;

    @Column
    private float rateDe=0;

    @Column
    private float rateUn=0;

    @Column
    private String intro="Let's Go!!!";
  @JsonFormat(pattern="dd-MM-yyyy")
  @Column
  private Date registerdate;;
  
  @JsonFormat(pattern="dd-MM-yyyy")
  @Column(nullable = true)
  private Date birthday;

  public long getId() {
    return id;
  }

  public void setId(long l) {
    this.id = l;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
}

  public void setPassword(String password) {
    this.password = password;
}

public Date getRegisterDate() {
  return registerdate;
}

public void setRegisterDate(Date registerdate) {
  this.registerdate = registerdate;
}

public Date getBirthday() {
  return birthday;
}

public void setBirthday(Date birthday) {
  this.birthday = birthday;
}

public void setBirthday(LocalDate of) {
}

public Object getLastUpdated() {
    return null;
}

public boolean isPresent() {
    return false;
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
}
