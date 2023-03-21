package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Identity;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.constant.Role;

public class Player extends User {

    private Identity identity;
    private PlayerStatus playerStatus;
    private int bonus;
    private Role role;
    private boolean ready;


    public Identity getIdentity() {
        return identity;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public int getBonus() {
        return bonus;
    }

    public Role getRole(){
        return role;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setRole(Role role){
        this.role = role;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }


}