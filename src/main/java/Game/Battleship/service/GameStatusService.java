package Game.Battleship.service;

import Game.Battleship.entity.GamePlayer;
import org.springframework.stereotype.Service;

@Service
public class GameStatusService {

    public static Boolean gameStatus(GamePlayer gp){
        if(gp.opponent().remainingShipCoordinates.isEmpty()) {
            gp.getGame().setFinished(true);
            return true;
        }
        return false;
    }
}

