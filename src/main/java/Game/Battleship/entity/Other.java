package Game.Battleship.entity;

import javax.persistence.*;

@Entity
public class Other {

    @Id
    private long id;

    @MapsId
    @OneToOne
    @JoinColumn(name="id")
    public Ship ship;

    Other(){}


}
