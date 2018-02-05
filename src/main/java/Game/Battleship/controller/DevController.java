package Game.Battleship.controller;

import Game.Battleship.entity.*;
import Game.Battleship.repository.*;
import Game.Battleship.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dev/")
public class DevController {

    @Autowired
    private GameRepository gRepo;

    @Autowired
    private GamePlayerRepository gpRepo;

    @Autowired
    private PlayerRepository pRepo;

    @Autowired
    private ShipRepository sRepo;

    @Autowired
    private SalvoRepository saRepo;

    /* Depreciated
    @Autowired
    private ScoreRepository scRepo;
    */

    /* -------- */
    /* Testing  */
    /* -------- */

    @RequestMapping("remaining")
    public Map<String, List<String>> remaining(){
        Map<String, List<String>> info = new HashMap<>();
        for (GamePlayer gp : gpRepo.findAll()){
//            for (Ship ship: gp.getShips()){
//                gp.remainingShipCoordinates.addAll(ship.getLocation());
//            }
            info.put(gp.toString(),gp.getRemainingShipCoordinates());
            //System.out.println(gp.getPlayer().toString());
            //System.out.println(gp.remainingShipCoordinates);
        }
        return info;
    }

    /* ---------- */
    /* Load Data  */
    /* ---------- */

    @RequestMapping("loadStarterData")
    public ResponseEntity<String> loadStarterData(){
        Game g = gRepo.getOne((long)1);
        System.out.println(g.getFinished());
        g.setFinished(true);
        System.out.println(g.getFinished());
        //gRepo.save(g);
//        Player p = pRepo.findOne((long)1);
//        System.out.println(p.toString());
//        p.setFirstName("Jake");
//        System.out.println(p.toString());

        return new ResponseEntity<>("Loaded", HttpStatus.OK);
    }


    /* ------------- */
    /* Depreciated   */
    /* ------------- */

    @RequestMapping("PlayerScores")
    public Map<Player, Integer> scores() {
        Map<Player, Integer> scores = new HashMap<>();
        List<Player> players = pRepo.findAll();
        for (Player player : players){
            Set<Score> points = player.getPoints();
            int pointTotal = 0;
            for (Score score:points){
                pointTotal = pointTotal + score.getPoints();
            }
            scores.put(player, pointTotal);
        }
        //sort scores
        /*List<Object> obj = scores.entrySet().stream()
                //.sorted(Map.Entry.comparingByValue());
                .sorted(Map.Entry.<Player, Integer>comparingByValue())
                //.collect(Collectors.toMap());*/

        return scores;
    }

    @RequestMapping("score")
    public String scoreTable(Authentication authentication){
        return "hi";

    }

    @RequestMapping("playing/{id}")
    public Map<String, Object> playing(@PathVariable Long id){
        Map<String, Object> playing = new HashMap<>();
        GamePlayer gp = gpRepo.findOne(id);
        // Player Info
        playing.put("Player",gp.getPlayer().toString());
        // Player Board
        Set<Ship> ships = gp.getShips();
        List<String> spots = new ArrayList<>();
        for (Ship ship: ships){
            List<String> coordinates = ship.getLocation();
            spots.addAll(coordinates);
            // testing to see what this is for :/
            System.out.println(spots);
        }
        playing.put("Board",spots);
        //Enemy Board
        Set<Salvo> salvos = gp.getSalvos();
        Map<String, Object> shots = new HashMap<>();
        for (Salvo salvo: salvos){
            Integer turn = salvo.getTurn();
            shots.put(turn.toString(), salvo.getAttacks());
        }
        playing.put("Enemy",shots);

        // Return necessary in-game information
        return playing;
    }

    @RequestMapping("gp/salvos")
    public Map<String, Object> salvos(){
        Map<String, Object> salvos = new HashMap<>();
        List<GamePlayer> gameplayers = gpRepo.findAll();
        for (GamePlayer gp:gameplayers){
            Map<Object, Object> turns = new HashMap<>();
            for(Salvo salvo:gp.getSalvos()) {
                turns.put(salvo.getTurn(),salvo.getAttacks());
            }
            salvos.put(gp.getId().toString(),turns);
        }
        return salvos;
    }

}
