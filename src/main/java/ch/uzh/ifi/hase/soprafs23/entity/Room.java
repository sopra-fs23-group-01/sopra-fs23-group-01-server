package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


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
@Table(name = "ROOM")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_sequence")
    @SequenceGenerator(name = "room_sequence", sequenceName = "room_sequence", allocationSize = 1, initialValue = 1)
    private long roomId;

    @Column(nullable = true)
    private Theme theme;

    @Column(nullable = false)
    private long roomOwnerId;

    @Column(nullable = true)
    private int maxPlayersNum;

//    @Column(nullable = false, unique = true)
//    private String token;

    @Column(nullable = false)
    private RoomProperty roomProperty;

    @OneToMany(mappedBy = "room")
    private List<User> roomPlayers = new ArrayList<>();

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }

    public RoomProperty getRoomProperty() {
        return roomProperty;
    }

    public void setRoomProperty(RoomProperty roomProperty) {
        this.roomProperty = roomProperty;
    }

    public int getMaxPlayersNum() {
        return maxPlayersNum;
    }

    public List<User> getRoomPlayers() {
        return roomPlayers;
    }

    public void addRoomPlayer(Optional<User> user) {
        if (user.isPresent()) {
            User owner = user.get();
            this.roomPlayers.add(owner);
        }
    }
    public long getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(long roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }
    public void setMaxPlayersNum(int maxPlayersNum) {
        this.maxPlayersNum = maxPlayersNum;
    }

    public void assignCardsAndRoles() {
        // shuffle the players list
        Collections.shuffle(roomPlayers);

        // assign role and card to each player
        for (int i = 0; i < roomPlayers.size(); i++) {
            User player = roomPlayers.get(i);
            if (i == 0) {
                player.setRole(false);
                player.setCard("pear");
            } else {
                player.setRole(true);
                player.setCard("apple");
            }
        }

    }
}
