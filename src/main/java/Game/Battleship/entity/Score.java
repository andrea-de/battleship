package Game.Battleship.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private String result;

    private int points;

    private Date finishDate = new Date();

    public Score(){}

    public Score(Game game, Player player, String result, int points){
        this.game = game;
        this.player = player;
        this.points = points;
        this.result= result;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer(){
        return player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getResult() {
        return result;
    }

    public Date getFinishDate() {
        return finishDate;
    }
}
