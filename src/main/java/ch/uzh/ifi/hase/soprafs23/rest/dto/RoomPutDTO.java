package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Identity;
import ch.uzh.ifi.hase.soprafs23.constant.RoomProperty;
import ch.uzh.ifi.hase.soprafs23.constant.Theme;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.List;

public class RoomPutDTO {
    private long roomId;
    private List<Player> members;
    private List<Player> winners;
    private Identity winningIdentity;
    private RoomProperty roomProperty;
    private Theme theme;
    private int maxPlayersNum;

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setMaxPlayersNum(int maxPlayersNum) {
        this.maxPlayersNum = maxPlayersNum;
    }

    public Theme getTheme() {
        return theme;
    }

    public int getMaxPlayersNum() {
        return maxPlayersNum;
    }


    public void setRoomProperty(RoomProperty roomProperty) {
        this.roomProperty = roomProperty;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public void setWinners(List<Player> winners) {
        this.winners = winners;
    }

    public void setWinningIdentity(Identity winningIdentity) {
        this.winningIdentity = winningIdentity;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public RoomProperty getRoomProperty() {
        return roomProperty;
    }

    public List<Player> getMembers() {
        return members;
    }

    public List<Player> getWinners() {
        return winners;
    }

    public Identity getWinningIdentity() {
        return winningIdentity;
    }

}
