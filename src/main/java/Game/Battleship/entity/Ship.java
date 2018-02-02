package Game.Battleship.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer")
    private GamePlayer gamePlayer;

    //@OneToOne(mappedBy="ship", fetch=FetchType.EAGER)
    //@PrimaryKeyJoinColumn
    // private ShipLocation shipLocation;

    @ElementCollection
    @JoinColumn(name = "ShipLocation")
    private List<String> shipLocation = new ArrayList<>();

    @OneToOne
    @PrimaryKeyJoinColumn
    private Other other;

    public Ship(){}

    public Ship(String shipType, GamePlayer gameplayer, List<String> shipLocation) {
        this.shipType = shipType;
        this.gamePlayer = gameplayer;
        this.shipLocation = shipLocation;
        //this.shipLocation.setCoordinates(shipLocation);
        gameplayer.remainingShipCoordinates.addAll(shipLocation);
    }

    public long getId() {
        return id;
    }

    public String getShipType(){
        return shipType;
    }

    public List<String> getLocation(){
        return shipLocation;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }


}
