package Game.Battleship.entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public Player() { }

    public Player(String first, String last, String email, String password) {
        this.firstName = first;
        this.lastName = last;
        this.email = email;
        this.password = password;
    }

    public Long getid() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<GamePlayer> getGamePlayers(){
        return gamePlayers;
    }

    public Set<Score> getPoints(){
        return scores;
    }

    public Object OpenInfo(){
        Map<Object, Object> playerInfo = new HashMap<>();
        playerInfo.put("id",id);
        playerInfo.put("first name",firstName);
        playerInfo.put("email",email);
        return playerInfo;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }
    /*public findByUserName(){

    }*/
}
