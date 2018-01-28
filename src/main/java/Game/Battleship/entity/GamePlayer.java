package Game.Battleship.entity;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date joinDate = new Date();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    // Not working
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Salvo> salvos = new HashSet<>();

    public GamePlayer(){}

    public GamePlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        System.out.println();
    }

    public Long getid() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game firstName) {
        this.game = game;
    }

    public GamePlayer opponent(){
        List<GamePlayer> players = new ArrayList<>();
        players.addAll(this.getGame().getGamePlayerSet());
        if (players.size()<2){
            return null;
        } else if (players.get(0).getid() != this.id){
            return players.get(0);
        } else  {
            return players.get(1);
        }
    }

    public Date getCreateDate() {
        return joinDate;
    }

    public Set<Ship> getShips(){
        return ships;
    }

    public Set<Salvo> getSalvos(){
        return salvos;
    }

}
