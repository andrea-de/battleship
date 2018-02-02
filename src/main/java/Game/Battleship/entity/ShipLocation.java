// Depreciated Entity

package Game.Battleship.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@Entity
public class ShipLocation {

    @Id
    //@GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    //private List

    //@ManyToOne(fetch= FetchType.EAGER)
    //@JoinColumn(name = "ship")

    @MapsId
    @OneToOne
    @JoinColumn(name="ship")
    private Ship ship;

    //private List<String> coordinates = new ArrayList<>();

    private List<String> coordinates = new ArrayList<>();

    public ShipLocation(){}

    public ShipLocation(List<String> shipLocation){
        this.coordinates = shipLocation;
    }

    public Long getId() {
        return id;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }
}
