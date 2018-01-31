package Game.Battleship.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer")
    private GamePlayer gamePlayer;

    @ElementCollection
    @JoinColumn(name = "Salvos")
    private List<String> salvo = new ArrayList<>();

    public Salvo(){}

    public Salvo(GamePlayer gameplayer, int turn, List<String> attacks) {
        this.gamePlayer = gameplayer;
        this.turn = turn;
        this.salvo = attacks;
        for (String coor: attacks) {
            String toRemove = null;
            for (String shipCoor : gameplayer.opponent().remainingShipCoordinates) {
                if (coor.equals(shipCoor)) {
                    toRemove = coor;
                }
            }
            if (toRemove != null) gameplayer.opponent().remainingShipCoordinates.remove(toRemove);
        }
    }

    public long getId() {
        return id;
    }

    public List<String> getAttacks(){
        return salvo;
    }

    public int getTurn(){
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

}
