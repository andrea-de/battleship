package Game.Battleship.service;

import Game.Battleship.entity.*;
import Game.Battleship.repository.GamePlayerRepository;
import Game.Battleship.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameStatusService {

    public static Boolean gameStatus(GamePlayer gp, GamePlayerRepository gpRepo, GameRepository gRepo){
        if (gp.opponent() == null){
            System.out.println(gp.getPlayer().toString() + " has no opponent.");
        } else if (gp.opponent().getRemainingShipCoordinates() == null){
            System.out.println(gp.getPlayer().toString() + "'s opponent never initialized ships");
        } else if(gp.opponent().getRemainingShipCoordinates().isEmpty()) {
            Game g = gp.getGame();
            g.setFinished(true);
            gRepo.save(g);
            gp.setWinner(true);
            gpRepo.save(gp);
            return true;
        }
        return false;
    }
}

