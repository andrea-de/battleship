package Game.Battleship.service;

import Game.Battleship.entity.GamePlayer;
import org.springframework.stereotype.Service;

@Service
public class GameStatusService {

    public static Boolean gameStatus(GamePlayer gp){
        if (gp.opponent() == null){
            System.out.println(gp.getPlayer().toString() + " has no opponent.");
        } else if (gp.opponent().remainingShipCoordinates == null){
            System.out.println(gp.getPlayer().toString() + "'s opponent never initialized ships");
        } else if(gp.opponent().remainingShipCoordinates.isEmpty()) {
            gp.getGame().finished = true;
            gp.winner = true;
            return true;
        }
        return false;
    }
}

