package Game.Battleship.controller;

import Game.Battleship.entity.*;
import Game.Battleship.repository.*;
import Game.Battleship.service.GameStatusService;
import Game.Battleship.service.InstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/api/")
public class GameController {

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

    /* ------------------------------- */
    /* Authentication and Registration */
    /* ------------------------------- */

    @RequestMapping(path = "newplayer", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody Player newPlayer) {
        String email = newPlayer.getEmail();
        if (email.isEmpty()) {
            return new ResponseEntity<>("No email given", HttpStatus.FORBIDDEN);
        } else if (!pRepo.findByEmail(email).isEmpty()) {
            Player player = pRepo.findByEmail(email).get(0);
            if (player != null) {
                return new ResponseEntity<>("Email already used", HttpStatus.CONFLICT);
            }
        }
        //pRepo.save(new Player("Chloe", "O'Brian", "c.obrian@ctu.gov"));
        pRepo.save(newPlayer);
        return new ResponseEntity<>("User added", HttpStatus.CREATED);
    }

    @RequestMapping(path = "checkEmail", method = RequestMethod.POST)
    public ResponseEntity<String> checkEmail(@RequestBody String email) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No email given", HttpStatus.FORBIDDEN);
        }
        List<Player> players = pRepo.findByEmail(email);
        if (!players.isEmpty()) {
            if (players.get(0) != null) {
                return new ResponseEntity<>("email already in use", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("email available", HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "status",method = RequestMethod.GET)
    public ResponseEntity<String> checkStatus(Authentication authentication){
        if (authentication != null){
            return new ResponseEntity<>("Logged In", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not logged in", HttpStatus.OK);
    }

    @RequestMapping(path = "profile",method = RequestMethod.GET)
    public String[] profile(Authentication authentication){
        if (authentication == null) return null;
        String email = authentication.getName();
        Player player = pRepo.findByEmail(email).get(0);
        String[] info = new String[4];
        info[0]= player.toString();
        info[1] = authentication.getName();
        int wins = 0;
        int losses = 0;
        for (GamePlayer gp : player.getGamePlayers()){
            if (gp.getGame().getFinished()){
                if(gp.getWinner()) wins++;
                else losses++;
            }
        }
        info[2]= Integer.toString(wins);
        info[3] = Integer.toString(losses);
        return info;
    }

    /* ----------------- */
    /* Makes Game Tables */
    /* ----------------- */

    @RequestMapping("games")
    public List<Object> getGamesList(Authentication authentication){
        List<Game> games = gRepo.findAll();
        List<Object> gameMapList = new ArrayList<>();
        for (Game game : games) {
            // putGameInfo no return, puts in data
            putGameInfo(game, gameMapList, authentication);
        }
        return gameMapList;
    }

    private void putGameInfo(Game game, List<Object> gameMapList, Authentication authentication){
        Set<GamePlayer> gamePlayerSet = game.getGamePlayerSet(); // Pulls from Data

        /* Fills gameplayers */
        List<Object> gamePlayerList = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayerSet) {
            putGamePlayerInfo(gamePlayer, gamePlayerList);
        }

        /* Makes Link */
        String gameLink = "api/game/" + game.getid() + "/"; //link to watch this particular game
        if (authentication == null){
            gameLink = gameLink + "viewer";
        } else {
            String linkString = "viewer";
            for (GamePlayer gamePlayer : gamePlayerSet) {
                if (gamePlayer.getPlayer().getEmail().equals(authentication.getName())){
                    linkString = gamePlayer.getId().toString();
                }
            }
            gameLink = gameLink + linkString;
        }

        /* Creates object to send */
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("id",game.getid());
        gameInfo.put("createdate",game.getcreateDate());
        gameInfo.put("gamePlayers",gamePlayerList);
        gameInfo.put("complete",game.getFinished());
        gameInfo.put("link",gameLink);
        gameMapList.add(gameInfo);
    }

    private void putGamePlayerInfo(GamePlayer gamePlayer, List<Object> gamePlayerList){
        Map<String, Object> gamePlayerInstance = new HashMap<>();
        gamePlayerInstance.put("id",gamePlayer.getId());
        gamePlayerInstance.put("player",getOpenInfo(gamePlayer.getPlayer()));
        // gamePlayer instance creates and returns object to add to parent object
        gamePlayerList.add(gamePlayerInstance);
    }

    private Map<String, Object> getOpenInfo(Player player){
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("id", player.getid());
        playerInfo.put("first name",player.getFirstName());
        playerInfo.put("email",player.getEmail());
        return playerInfo;
    }

    /* -------------------- */
    /* Get Game information */
    /* -------------------- */

    @RequestMapping("game/{gameid}/{gpid}")
    public LinkedHashMap<String, Object> gamePlayerInfo(@PathVariable Long gameid, @PathVariable String gpid, Authentication authentication) {
        /* Construct object to send back */
        LinkedHashMap<String, Object> gameInfo = new LinkedHashMap<>();
        /* Get gp info */
        if (!gpid.equals("viewer")) {
            GamePlayer gp = gpRepo.findOne(Long.parseLong(gpid));
            if (Objects.equals(authentication.getName(), gp.getPlayer().getEmail())){
                gameInfo.put(gp.getPlayer().toString(), getGPinfo(gp, true));
                if (gp.opponent()!=null) {
                    gameInfo.put(gp.opponent().getPlayer().toString(), getGPinfo(gp.opponent(), false));
                }
                return gameInfo;
            }
        }
        Game game = gRepo.findOne(gameid);
        // If game has not been created make a different return request
            for (GamePlayer gp : game.getGamePlayerSet()) {
                gameInfo.put(gp.getPlayer().toString(), getGPinfo(gp, false));
            }


        return gameInfo;
    }

    private Map<String, Object> getGPinfo(GamePlayer gp, Boolean authorized){
        Map<String, Object> gamePlayerInfo = new HashMap<>();// Make Map
        // Player hits
        if (gp.opponent()!=null) {
            gamePlayerInfo.put("Hits", hitsOn(gp));
            if (authorized){
                gamePlayerInfo.put("Ships", gpShips(gp));
                gamePlayerInfo.put("Salvos", gpSalvos(gp));
                if (gp.getSalvos().size() <= gp.opponent().getSalvos().size()){
                    gamePlayerInfo.put("Turn", true);
                }
            }
        } else {
            if (authorized){
                gamePlayerInfo.put("Ships", gpShips(gp));
            }
        }

        return gamePlayerInfo;
    }

    // Returns JSON of ships for Gameplayer
    private HashMap<String,List> gpShips(GamePlayer gp){
        HashMap<String,List> ships = new HashMap<>();
        for (Ship ship:gp.getShips()){
            ships.put(ship.getShipType(),ship.getLocation());
        }
        return ships;
    }

    // Returns Array of GP Ship Locations
    private List<String> gpShipLocations(GamePlayer gp){
        List<String> shipLocations = new ArrayList<>();
        for (Ship ship:gp.getShips()){
            shipLocations.addAll(ship.getLocation());
        }
        return shipLocations;
    }

    // Return Map of Hits on GP : Turn of Hit
    private HashMap<String, Integer> hitsOn(GamePlayer gp){
        HashMap<String, Integer> hits = new HashMap<>(); // List of Hashmaps that will be returned
        List<String> ships = gpShipLocations(gp);
        for(Salvo salvo:gp.opponent().getSalvos()){
            for (String coordinate:salvo.getAttacks()){
                if (ships.contains(coordinate)){
                    hits.put(coordinate,salvo.getTurn());
                }
            }
        }
        return hits;
    }

    // Returns JSON of Salvos for Gameplayer
    private HashMap<Integer,Object> gpSalvos(GamePlayer gp){
        HashMap<Integer,Object> allSalvos = new HashMap<>();
        for (Salvo salvo:gp.getSalvos()){
            allSalvos.put(salvo.getTurn(),salvo.getAttacks());
        }
        return allSalvos;
    }

    /// Check Sunk ships and add to gamePlayerInfo ///


    /* --------------------- */
    /* Join and Create Game  */
    /* --------------------- */

    @RequestMapping(path = "join/{gameid}", method = RequestMethod.POST)
    public ResponseEntity<String> joinGame(@PathVariable Long gameid, Authentication authentication, @RequestBody Map<String, List<String>> ships){
        if (authentication == null){
            return new ResponseEntity<>("Not Logged In", HttpStatus.FORBIDDEN);
        }
        // add check to see how many open games
        // function to check if ship placement is okay
        if (!checkShipPlacement(ships)){
            return new ResponseEntity<>("Bad ship placement", HttpStatus.BAD_REQUEST);
        }
        Game game = gRepo.findOne(gameid);
        Player player = pRepo.findByEmail(authentication.getName()).get(0);
        if (game.getPlayerSet().contains(player)){
            return new ResponseEntity<>("Already in game", HttpStatus.CONFLICT);
        }
        GamePlayer gp = gpRepo.save(new GamePlayer(game, player));
        for (String key: ships.keySet()){
            Ship newShip = new Ship(key, gp, ships.get(key));
            sRepo.save(newShip);
        }

        /* Refactored
        for (Map.Entry<String, List<String>> shipEntry: ships.entrySet()){
            Ship newShip = sRepo.save(new Ship(shipEntry.getKey(), gp, shipEntry.getValue()));
        }


        Map<String, Integer> test = new HashMap<>();
        test.put("test", 5);
        Integer x = test.get("test");

        */

        return new ResponseEntity<>("Joined game", HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "newgame", method = RequestMethod.POST)
    public ResponseEntity<String> createGame(Authentication authentication, @RequestBody Map<String, List<String>> ships){
        if (authentication == null){
            return new ResponseEntity<>("Not Logged In", HttpStatus.FORBIDDEN);
        }
        // add check to see how many open games

        // function to check if ship placement is okay
        if (!checkShipPlacement(ships)){
            return new ResponseEntity<>("Bad ship placement", HttpStatus.BAD_REQUEST);
        }
        Player player = pRepo.findByEmail(authentication.getName()).get(0);
        Game game = gRepo.save(new Game());
        GamePlayer gp = gpRepo.save(new GamePlayer(game, player));
        for (Map.Entry ship: ships.entrySet()){
            // System.out.println(ship.getValue()); TYPE
            Ship newShip = sRepo.save(new Ship(ship.getKey().toString(), gp, (List<String>) ship.getValue()));
        }
        return new ResponseEntity<>("Game created", HttpStatus.CREATED);
    }

    /// Check Ship placement ///
    //@RequestMapping(path = "api/checkShips", method = RequestMethod.POST)
    private Boolean checkShipPlacement(@RequestBody Map<String, List<String>> ships){
        String[] requiredShips = {"Carrier", "Battleship", "Destroyer", "Cruiser", "Submarine"};
        List<String> reqShips = new ArrayList<String>(Arrays.asList(requiredShips));
        for (Map.Entry ship: ships.entrySet()){
            if (checkSingleShip(ship.getKey().toString(), (List<String>) ship.getValue())){
                reqShips.remove(ship.getKey().toString());
            }
        }
        return reqShips.isEmpty();
    }

    private Boolean checkSingleShip(String shipName, List<String> coordinates){
        if (Objects.equals(shipName, "Carrier")) return checkShipCoor(5, coordinates);
        else if (Objects.equals(shipName, "Battleship")) return checkShipCoor(4, coordinates);
        else if (Objects.equals(shipName, "Destroyer")) return checkShipCoor(2, coordinates);
        else return checkShipCoor(3, coordinates);
    }

    // check if coordinates continuous
    private Boolean checkShipCoor(long length ,List<String> coordinates){
        // put list into array and sort
        // variables for horizontal based on first 2
        for (int i=0;i<length;i++){
            // rules for checking first and second characters
        }
        return true;
    }

    /* ---------------------- */
    /* Fire and Check Salvos  */
    /* ---------------------- */

    @RequestMapping(path = "salvo/{gp}", method = RequestMethod.POST)
    public ResponseEntity<String> fire(@PathVariable long gp, Authentication authentication, @RequestBody List<String> coordinates){
        GamePlayer gamePlayer = gpRepo.findOne(gp);
        if(!Objects.equals(authentication.getName(), gamePlayer.getPlayer().getEmail())){
            return new ResponseEntity<>("Cheater", HttpStatus.FORBIDDEN);
        }
        // check if 3 salvos have been taken
        if (gamePlayer.getSalvos().size() > gamePlayer.opponent().getSalvos().size()){
            return new ResponseEntity<>("Not your turn", HttpStatus.FORBIDDEN);
        }
        int turns = gamePlayer.getSalvos().size();
        int thisTurn = turns + 1;
        Salvo salvo = saRepo.save(new Salvo(gamePlayer,thisTurn,coordinates));
        if (GameStatusService.gameStatus(gamePlayer, gpRepo, gRepo)) {
            return new ResponseEntity<>("You Won", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Shots Fired", HttpStatus.ACCEPTED);
    }

    /* --------*/
    /* Records */
    /* --------*/

    @RequestMapping("record")
    public Map<String, Map<String, Long>> record(Authentication authentication){
        Map<String, Map<String, Long>> data = new HashMap<>();
        List<Player> players = pRepo.findAll();
        for (Player player : players){
            Map<String, Long> record = new HashMap<>();
            long wins = 0;
            long losses = 0;
            for (GamePlayer gp : player.getGamePlayers()){
                Game g = gp.getGame();
                if (g.getFinished()){
                    if (gp.getWinner()){
                        wins++;
                    } else {
                        losses++;
                    }
                }
            }
            record.put("wins", wins);
            record.put("losses", losses);
            data.put(player.toString(), record);
        }
        return data;
    }

    /* ------------- */
    /* Instructions  */
    /* ------------- */

    @RequestMapping("getInstruction/{gp}")
    public String getInstruction(@PathVariable Long gp){
        GamePlayer player = gpRepo.findOne(gp);
        return InstructionService.setInstruction(player);

        //return new ResponseEntity<>(player.getPlayer().getFirstName() + ": " + player.getInstruction(), HttpStatus.OK);
    }

}