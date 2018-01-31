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

import java.util.*;

@RestController
//@RequestMapping("/api")
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

    @Autowired
    private ScoreRepository scRepo;

    /* ------------------------------- */
    /* Authentication and Registration */
    /* ------------------------------- */

    @RequestMapping(path = "api/newplayer", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody Player newPlayer) {
        System.out.println(newPlayer.getEmail());
        String email = newPlayer.getEmail();
        if (email.isEmpty()) {
            return new ResponseEntity<>("No email given", HttpStatus.FORBIDDEN);
        } else if (pRepo.findByEmail(email).isEmpty()==false) {
            Player player = pRepo.findByEmail(email).get(0);
            if (player != null) {
                return new ResponseEntity<>("Email already used", HttpStatus.CONFLICT);
            }
        }
        //pRepo.save(new Player("Chloe", "O'Brian", "c.obrian@ctu.gov"));
        pRepo.save(newPlayer);
        return new ResponseEntity<>("User added", HttpStatus.CREATED);
    }

    @RequestMapping(path = "/checkEmail", method = RequestMethod.POST)
    public ResponseEntity<String> checkEmail(@RequestBody String email) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No email given", HttpStatus.FORBIDDEN);
        }
        List<Player> players = pRepo.findByEmail(email);
        if (players.isEmpty()){
            //System.out.println("no players with that email");
        } else {
            if (players.get(0) != null) {
                return new ResponseEntity<>("email already in use", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("email available", HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/api/status",method = RequestMethod.GET)
    public ResponseEntity<String> checkStatus(Authentication authentication){
        if (authentication != null){
            return new ResponseEntity<>("Logged In", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not logged in", HttpStatus.OK);
    }

    /* ----------------- */
    /* Makes Game Tables */
    /* ----------------- */

    @RequestMapping("/api/games")
    public List<Object> getGamesList(Authentication authentication){
        List<Game> games = gRepo.findAll();
        List<Object> gameMapList = new ArrayList<>();
        for (Game game : games) {
            // putGameInfo no return, puts in data
            putGameInfo(game, gameMapList, authentication);
        }
        return gameMapList;
    }

    public void putGameInfo(Game game, List<Object> gameMapList, Authentication authentication){
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
                    linkString = gamePlayer.getid().toString();
                }
            }
            gameLink = gameLink + linkString;
        }

        /* Creates object to send */
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("id",game.getid());
        gameInfo.put("createdate",game.getcreateDate());
        gameInfo.put("gamePlayers",gamePlayerList);
        /* Learn to sort streams to make game table consistant */
        //gamePlayerSet.stream()
        //.sorted();
        //.filter(p -> p.getPlayer().getEmail());
        //.forEach(p -> System.out.println(p));
        //.sorted((a,b) -> a.getValue() - b.getValue()).collect(toList());
        gameInfo.put("link",gameLink);
        gameMapList.add(gameInfo);
    }

    public void putGamePlayerInfo(GamePlayer gamePlayer, List<Object> gamePlayerList){
        Map<String, Object> gamePlayerInstance = new HashMap<>();
        gamePlayerInstance.put("id",gamePlayer.getid());
        gamePlayerInstance.put("player",getOpenInfo(gamePlayer.getPlayer()));
        // gamePlayer instance creates and returns object to add to parent object
        gamePlayerList.add(gamePlayerInstance);
    }

    public Map<String, Object> getOpenInfo(Player player){
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("id", player.getid());
        playerInfo.put("first name",player.getFirstName());
        playerInfo.put("email",player.getEmail());
        return playerInfo;
    }

    /* -------------------- */
    /* Get Game information */
    /* -------------------- */

    @RequestMapping("/api/game/{gameid}/{gpid}")
    public LinkedHashMap<String, Object> gamePlayerInfo(@PathVariable Long gameid, @PathVariable String gpid, Authentication authentication) {
        /* Construct object to send back */
        LinkedHashMap<String, Object> gameInfo = new LinkedHashMap<>();
        /* Get gp info */
        if (!gpid.equals("viewer")) {
            GamePlayer gp = gpRepo.findOne(Long.parseLong(gpid));
            if (authentication.getName() == gp.getPlayer().getEmail()){
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

    public Map<String, Object> getGPinfo(GamePlayer gp, Boolean authorized){
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
    public HashMap<String,List> gpShips(GamePlayer gp){
        HashMap<String,List> ships = new HashMap<>();
        for (Ship ship:gp.getShips()){
            ships.put(ship.getShipType(),ship.getLocation());
        }
        return ships;
    }

    // Returns Array of GP Ship Locations
    public List<String> gpShipLocations(GamePlayer gp){
        List<String> shipLoations = new ArrayList<>();
        for (Ship ship:gp.getShips()){
            for (String coordinates:ship.getLocation()){
                shipLoations.add(coordinates);
            }
        }
        return shipLoations;
    }

    // Return Map of Hits on GP : Turn of Hit
    public HashMap<String, Integer> hitsOn(GamePlayer gp){
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
    public HashMap<Integer,Object> gpSalvos(GamePlayer gp){
        HashMap<Integer,Object> allSalvos = new HashMap<>();
        for (Salvo salvo:gp.getSalvos()){
            allSalvos.put(salvo.getTurn(),salvo.getAttacks());
        }
        return allSalvos;
    }

    /// Check Sunk ships and add to gamePlayerInfo ///


    /* -------------------- */
    /* Join and Creat Game  */
    /* -------------------- */

    @RequestMapping(path = "api/join/{gameid}", method = RequestMethod.POST)
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
            Ship newShip = sRepo.save(new Ship(key, gp, ships.get(key)));
        }
        // Refactored
        /*
        for (Map.Entry<String, List<String>> shipEntry: ships.entrySet()){
            Ship newShip = sRepo.save(new Ship(shipEntry.getKey(), gp, shipEntry.getValue()));
        }
        */

        Map<String, Integer> test = new HashMap<>();
        test.put("test", 5);
        Integer x = test.get("test");

        return new ResponseEntity<>("Joined game", HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "api/newgame", method = RequestMethod.POST)
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
    public Boolean checkShipPlacement(@RequestBody Map<String, List<String>> ships){
        String[] requiredShips = {"Carrier", "Battleship", "Destroyer", "Cruiser", "Submarine"};
        List<String> reqShips = new ArrayList<String>(Arrays.asList(requiredShips));
        for (Map.Entry ship: ships.entrySet()){
            if (checkSingleShip(ship.getKey().toString(), (List<String>) ship.getValue())){
                reqShips.remove(ship.getKey().toString());
            }
        }
        // Add duplicate coordinates
        if (reqShips.isEmpty()){
            return true;
            //return new ResponseEntity<>("Ships checked", HttpStatus.ACCEPTED);
        } else {
            return false;
            //return new ResponseEntity<>("All ships not placed", HttpStatus.BAD_REQUEST);
        }
    }

    private Boolean checkSingleShip(String shipName, List<String> coordinates){
        if (shipName == "Carrier"){
            return checkShipCoor(5,coordinates);
        } else if (shipName == "Battleship"){
            return checkShipCoor(4,coordinates);
        } else if (shipName == "Destroyer"){
            return checkShipCoor(2,coordinates);
        } else {
            return checkShipCoor(3,coordinates);
        }
    }

    private Boolean checkShipCoor(long length ,List<String> coordinates){
        // check if coordinates are in a row
        return true;
    }


    /// Check for too many games before joining or creating new one ///

    ///// Check authentication /////

    /* ---------------------- */
    /* Fire and Check Salvos  */
    /* ---------------------- */

    @RequestMapping(path = "api/salvo/{gp}", method = RequestMethod.POST)
    public ResponseEntity<String> fire(@PathVariable long gp, Authentication authentication, @RequestBody List<String> coordinates){
        GamePlayer gamePlayer = gpRepo.findOne(gp);
        if(authentication.getName()!=gamePlayer.getPlayer().getEmail()){
            return new ResponseEntity<>("Cheater", HttpStatus.FORBIDDEN);
        }
        // check if 3 salvos have been taken
        if (gamePlayer.getSalvos().size() > gamePlayer.opponent().getSalvos().size()){
            return new ResponseEntity<>("Not your turn", HttpStatus.FORBIDDEN);
        }
        int turns = gamePlayer.getSalvos().size();
        int thisTurn = turns + 1;
        Salvo salvo = saRepo.save(new Salvo(gamePlayer,thisTurn,coordinates));
        if (GameStatusService.gameStatus(gamePlayer)) {
            return new ResponseEntity<>("You Won", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Shots Fired", HttpStatus.ACCEPTED);
    }

    // do i need this?
    @RequestMapping("/api/gp/salvos")
    public Map<String, Object> ssalvos(){
        Map<String, Object> salvos = new HashMap<>();
        List<GamePlayer> gameplayers = gpRepo.findAll();
        for (GamePlayer gp:gameplayers){
            Map<Object, Object> turns = new HashMap<>();
            for(Salvo salvo:gp.getSalvos()) {
                turns.put(salvo.getTurn(),salvo.getAttacks());
            }
            salvos.put(gp.getid().toString(),turns);
        }
        return salvos;
    }

    // Working on section
    // Don't need ???? p'playing'
    @RequestMapping("/api/playing/{id}")
    public Map<String, Object> pplaying(@PathVariable Long id){
        Map<String, Object> playing = new HashMap<>();
        GamePlayer gp = gpRepo.findOne(id);
        // Player Info
        playing.put("Player",gp.getPlayer().toString());
        // Player Board
        Set<Ship> ships = gp.getShips();
        List<String> spots = new ArrayList<>();;
        for (Ship ship: ships){
            List<String> coordinates = ship.getLocation();
            for (String coordinate:coordinates){
                spots.add(coordinate);
            }
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

    /* --------*/
    /* Scoring */
    /* --------*/

    @RequestMapping("/api/PlayerScores")
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

    @RequestMapping("/api/score")
    public String scoreTable(Authentication authentication){
        return "hi";

    }

    /* ---------- */
    /* Load Data  */
    /* ---------- */

    @RequestMapping("/api/loadStarterData")
    public ResponseEntity<String> loadStarterData(){
        loadData();
        return new ResponseEntity<>("Loaded", HttpStatus.OK);
    }

    public void loadData(){
        Boolean check1 = !pRepo.findByEmail("j.bauer@ctu.gov").isEmpty();
        Boolean check2 = !pRepo.findByEmail("c.obrian@ctu.gov").isEmpty() && check1;
        Boolean check3 = !pRepo.findByEmail("k").isEmpty() && check2;
        Boolean check4 = !pRepo.findByEmail("dp@aol.com").isEmpty() && check3;
        Boolean check5 = !pRepo.findByEmail("md@.com").isEmpty() && check4;
        if (check5){
            return;
        }
        Player p1 = pRepo.save(new Player("Jack", "Bauer", "j.bauer@ctu.gov", "password"));
        p1.setPassword("j");
        pRepo.save(p1);
        Player p2 = pRepo.save(new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", "c"));
        Player p3 = pRepo.save(new Player("Kim", "Bauer", "k", "k"));
        p3.setPassword("k");
        pRepo.save(p3);
        Player p4 = pRepo.save(new Player("David", "Palmer", "dp@aol.com", "d"));
        Player p5 = pRepo.save(new Player("Michelle", "Dessler", "md@.com", "m"));
        Game g1 = gRepo.save(new Game());
        Game g2 = gRepo.save(new Game());
        Game g3 = gRepo.save(new Game());
        GamePlayer gp1 = gpRepo.save(new GamePlayer(g1, p1));
        GamePlayer gp2 = gpRepo.save(new GamePlayer(g1, p2));
        GamePlayer gp3 = gpRepo.save(new GamePlayer(g2, p4));
        GamePlayer gp4 = gpRepo.save(new GamePlayer(g2, p2));
        GamePlayer gp5 = gpRepo.save(new GamePlayer(g3, p3));
        List<String> shipLoc1 = Arrays.asList("A1", "A2", "A3");
        List<String> shipLoc2 = Arrays.asList("F8", "G8", "H8", "I8");
        List<String> shipLoc3 = Arrays.asList("C4", "D4", "E4", "F4");
        Ship s1 = sRepo.save(new Ship("Long", gp1, shipLoc1));
        Ship s2 = sRepo.save(new Ship("Short", gp1, shipLoc2));
        Ship s3 = sRepo.save(new Ship("Long", gp2, shipLoc3));
        Ship s4 = sRepo.save(new Ship("Long", gp3, shipLoc1));

        Score score2 = scRepo.save(new Score(g1, p1, "Lose", 0));
        Score score1 = scRepo.save(new Score(g1, p2, "Win", 3));
        Score score4 = scRepo.save(new Score(g2, p2, "Tie", 1));
        Score score3 = scRepo.save(new Score(g2, p4, "Tie", 1));
        Score score5 = scRepo.save(new Score(g3, p3, "Win", 3));
    }

    @RequestMapping("/api/loadMoreData")
    public ResponseEntity<String> loadMoreData(){
        loadSalvos();
        return new ResponseEntity<>("Loaded", HttpStatus.OK);
    }

    public void loadSalvos(){
        List<String> salvoLoc1 = Arrays.asList("A1", "B6", "I8");
        List<String> salvoLoc2 = Arrays.asList("A2", "E4", "F7");
        Salvo salvo1 = saRepo.save(new Salvo(gpRepo.findOne((long) 1), 1, salvoLoc1));
        Salvo salvo2 = saRepo.save(new Salvo(gpRepo.findOne((long) 1), 2, salvoLoc2));
        Salvo salvo3 = saRepo.save(new Salvo(gpRepo.findOne((long) 2), 1, salvoLoc1));
    }

    /* ------------- */
    /* Instructions  */
    /* ------------- */

    @RequestMapping("/api/getInstruction/{gp}")
    public String getInstruction(@PathVariable Long gp){
        GamePlayer player = gpRepo.findOne(gp);
        return InstructionService.setInstruction(player);

        //return new ResponseEntity<>(player.getPlayer().getFirstName() + ": " + player.getInstruction(), HttpStatus.OK);
    }
}