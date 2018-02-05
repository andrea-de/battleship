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
        List<String> remainingCoordinates = gameplayer.opponent().getRemainingShipCoordinates();
        for (String coor: attacks) {
            List<String> toRemove = new ArrayList<>();
            for (String shipCoor : remainingCoordinates) {
                if (coor.equals(shipCoor)) {
                    toRemove.add(coor);
                }
            }
            if (!toRemove.isEmpty()) {
                remainingCoordinates.removeAll(toRemove);
                gameplayer.opponent().setRemainingShipCoordinates(remainingCoordinates);
            }
        }
    }

    // Salvo is created without needing to reference objects not yet instantiated
    public Salvo(GamePlayer gameplayer, int turn, List<String> attacks, Boolean devMode) {
        this.gamePlayer = gameplayer;
        this.turn = turn;
        this.salvo = attacks;
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
