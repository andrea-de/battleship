package Game.Battleship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Game.Battleship.entity.GamePlayer;

@Service
public class InstructionService {

    public static String setInstruction(GamePlayer gp){
        if (gp.opponent() == null){
            return "Wait for opponent to enter the stage.";
        } else if (true) {
            return "YOU WON!!!";
        } else if (gp.getSalvos().size() > gp.opponent().getSalvos().size()){
            return "Waiting for opponent salvos.";
        } else {
            return "Select three coordinates and press fire.";
        }
    }
}
