package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

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
  @Column(name = "user_id")
  private long id;

  @Column(nullable = true)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column
  private ReadyStatus readyStatus;

  @Column(nullable = false)
  private String password;

  @Column
  private String email;

  @Column
  private Boolean aliveStatus;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

  @Column
  private GameStatus gameStatus;
  @Column
  private String card;// 玩家的牌
  @Column
  private Role role = Role.NOT_ASSIGNED;
  @Column
  private Long enteredRoom;// 玩家的牌



    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Column
    @JsonProperty("gender")
    private UserGender gender;

    @Column
    private float rateDe=0;

    @Column
    private float rateUn=0;

    @Column
    private int numOfGameDe=0;

    @Column
    private int numOfGameUn=0;

    public int getNumOfWinGameDe() {
        return numOfWinGameDe;
    }

    public void setNumOfWinGameDe(int numOfWinGameDe) {
        this.numOfWinGameDe = numOfWinGameDe;
    }

    public int getNumOfWinGameUn() {
        return numOfWinGameUn;
    }

    public void setNumOfWinGameUn(int numOfWinGameUn) {
        this.numOfWinGameUn = numOfWinGameUn;
    }

    @Column
    private int numOfWinGameDe=0;

    @Column
    private int numOfWinGameUn=0;

    public int getNumOfGameDe() {
        return numOfGameDe;
    }



    public void setNumOfGameDe(int numOfGameDe) {
        this.numOfGameDe = numOfGameDe;
    }

    public int getNumOfGameUn() {
        return numOfGameUn;
    }

    public void setNumOfGameUn(int numOfGameUn) {
        this.numOfGameUn = numOfGameUn;
    }



    @Column
    private String intro="Let's Go!!!";
    @JsonFormat(pattern="dd-MM-yyyy")
    @Column
    private Date registerdate;;

    @JsonFormat(pattern="dd-MM-yyyy")
    @Column(nullable = true)
    private Date birthday;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    Random random = new Random();
    int randomNumber = random.nextInt(14);
    @Column
    private String avatarUrl="https://robohash.org/"+randomNumber;
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }



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

public Long geEnteredRoom() {
  return enteredRoom;
}

public void setEnteredRoom(Long enteredRoom) {
  this.enteredRoom= enteredRoom;
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

    public ReadyStatus getReadyStatus() {
        return readyStatus;
    }

    public void setReadyStatus(ReadyStatus readyStatus) {
        this.readyStatus = readyStatus;
    }

    public Boolean getAliveStatus() {
        return aliveStatus;
    }

    public void setAliveStatus(Boolean aliveStatus) {
        this.aliveStatus = aliveStatus;
    }


}
