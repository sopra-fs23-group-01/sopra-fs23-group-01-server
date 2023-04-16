package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.constant.Theme;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import java.util.ArrayList;

public class RoomGetDTO {
    private long roomId;
    private Theme theme;
    private RoomProperty roomProperty;
    private int maxPlayersNum;

    private long roomOwnerId;

    public long getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(long roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }

    public ArrayList<User> getRoomPlayers() {
        return roomPlayers;
    }

    public void setRoomPlayers(ArrayList<User> roomPlayers) {
        this.roomPlayers = roomPlayers;
    }

    private ArrayList<User> roomPlayers;

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

    public RoomProperty getRoomProperty() {
        return roomProperty;
    }

    public void setRoomProperty(RoomProperty roomProperty) {
        this.roomProperty = roomProperty;
    }

    public int getMaxPlayersNum() {
        return maxPlayersNum;
    }

    public void setMaxPlayersNum(int maxPlayersNum) {
        this.maxPlayersNum = maxPlayersNum;
    }
}
