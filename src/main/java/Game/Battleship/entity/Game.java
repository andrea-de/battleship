package Game.Battleship.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private Date createDate = new Date();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Game() { }

    public Game(Date date) {
        this.createDate = date;
    }

    public String getcreateDate() {
        return createDate.toString();
    }

    public long getid() {
        return id;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayers;
    }

    public Set<Player> getPlayerSet() {
        Set<Player> players = new HashSet<>();
        for (GamePlayer gp: this.getGamePlayerSet()){
            players.add(gp.getPlayer());
        };
        return players;
    }


    @Override
    public String toString() {
        return "Game ID: " + id + " Creation Date " + createDate.toString();
    }
}
