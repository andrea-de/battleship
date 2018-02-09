package Game.Battleship.service;

import Game.Battleship.entity.*;
import Game.Battleship.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataLoaderService {

    public static void loadPlayers(PlayerRepository pRepo){
        Player p1 = pRepo.save(new Player("Jack", "Bauer", "j.bauer@ctu.gov", "password"));
        p1.setPassword("j");
        pRepo.save(p1);
        Player p2 = pRepo.save(new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", "c"));
        Player p3 = pRepo.save(new Player("Kim", "Bauer", "kim@alwaysHostage.org", "k"));
        p3.setPassword("k");
        pRepo.save(p3);
        Player p4 = pRepo.save(new Player("David", "Palmer", "presPalmer@allstate.com", "d"));
        Player p5 = pRepo.save(new Player("Michelle", "Dessler", "m.dessler@ctu.gov", "m"));
    }

    public static void loadGames(GameRepository gRepo, GamePlayerRepository gpRepo, PlayerRepository pRepo, ShipRepository sRepo, SalvoRepository saRepo){
        // Games
        Game g1 = gRepo.save(new Game());
        Game g2 = gRepo.save(new Game());
        Game g3 = gRepo.save(new Game());
        // Boot-loaded Ship constructor does not set remaining ships variable
        List<String> set1a = Arrays.asList("J8");
        List<String> set1b = Arrays.asList("C9", "C10", "A2", "A3", "G3", "H3", "I3", "D3", "D4", "D5", "D6", "F8", "G8", "H8", "J8");
        List<String> set2a = Arrays.asList("B2", "B3", "G7", "G9", "C6", "C7", "C8", "E5", "F5", "G5", "H5", "I5");
        List<String> set2b = Arrays.asList("E8", "E9", "B1", "B2", "B3", "G7", "G8", "G9", "C6", "C7", "C8", "C9", "E5", "F5", "G5", "H5", "I5");
        GamePlayer gp1 = gpRepo.save(new GamePlayer(g1, pRepo.findByEmail("j.bauer@ctu.gov").get(0), false, set1a));
        GamePlayer gp2 = gpRepo.save(new GamePlayer(g1, pRepo.findByEmail("c.obrian@ctu.gov").get(0), false, set2a));
        GamePlayer gp3 = gpRepo.save(new GamePlayer(g2, pRepo.findByEmail("c.obrian@ctu.gov").get(0), false, set2b));
        GamePlayer gp4 = gpRepo.save(new GamePlayer(g2, pRepo.findByEmail("presPalmer@allstate.com").get(0),false, set1b));
        GamePlayer gp5 = gpRepo.save(new GamePlayer(g3, pRepo.findByEmail("kim@alwaysHostage.org").get(0), false, set2b));
        // Set 1
        List<String> shipLoc1 = Arrays.asList("C9", "C10");
        List<String> shipLoc2 = Arrays.asList("A1", "A2", "A3");
        List<String> shipLoc3 = Arrays.asList("G3", "H3", "I3");
        List<String> shipLoc4 = Arrays.asList("D3", "D4", "D5", "D6");
        List<String> shipLoc5 = Arrays.asList("F8", "G8", "H8", "I8", "J8");
        // Set 2
        List<String> shipLoc6 = Arrays.asList("E8", "E9");
        List<String> shipLoc7 = Arrays.asList("B1", "B2", "B3");
        List<String> shipLoc8 = Arrays.asList("G7", "G8", "G9");
        List<String> shipLoc9 = Arrays.asList("C6", "C7", "C8", "C9");
        List<String> shipLoc10 = Arrays.asList("E5", "F5", "G5", "H5", "I5");
        // Ships for Gameplayer 1 & 4
        Ship s1 = sRepo.save(new Ship("Destroyer", gp1, shipLoc1, true));
        Ship s2 = sRepo.save(new Ship("Submarine", gp1, shipLoc2, true));
        Ship s3 = sRepo.save(new Ship("Cruiser", gp1, shipLoc3, true));
        Ship s4 = sRepo.save(new Ship("Battleship", gp1, shipLoc4, true));
        Ship s5 = sRepo.save(new Ship("Carrier", gp1, shipLoc5, true));
        Ship s6 = sRepo.save(new Ship("Destroyer", gp4, shipLoc1, true));
        Ship s7 = sRepo.save(new Ship("Submarine", gp4, shipLoc2, true));
        Ship s8 = sRepo.save(new Ship("Cruiser", gp4, shipLoc3, true));
        Ship s9 = sRepo.save(new Ship("Battleship", gp4, shipLoc4, true));
        Ship s10 = sRepo.save(new Ship("Carrier", gp4, shipLoc5, true));
        // Ships for Gameplayer 2 & 3
        Ship s11 = sRepo.save(new Ship("Destroyer", gp2, shipLoc6, true));
        Ship s12 = sRepo.save(new Ship("Submarine", gp2, shipLoc7, true));
        Ship s13 = sRepo.save(new Ship("Cruiser", gp2, shipLoc8, true));
        Ship s14 = sRepo.save(new Ship("Battleship", gp2, shipLoc9, true));
        Ship s15 = sRepo.save(new Ship("Carrier", gp2, shipLoc10, true));
        Ship s16 = sRepo.save(new Ship("Destroyer", gp3, shipLoc6, true));
        Ship s17 = sRepo.save(new Ship("Submarine", gp3, shipLoc7, true));
        Ship s18 = sRepo.save(new Ship("Cruiser", gp3, shipLoc8, true));
        Ship s19 = sRepo.save(new Ship("Battleship", gp3, shipLoc9, true));
        Ship s20 = sRepo.save(new Ship("Carrier", gp3, shipLoc10, true));
        // Ships for Gameplayer 5
        Ship s21 = sRepo.save(new Ship("Destroyer", gp5, shipLoc6, true));
        Ship s22 = sRepo.save(new Ship("Submarine", gp5, shipLoc7, true));
        Ship s23 = sRepo.save(new Ship("Cruiser", gp5, shipLoc8, true));
        Ship s24 = sRepo.save(new Ship("Battleship", gp5, shipLoc9, true));
        Ship s25 = sRepo.save(new Ship("Carrier", gp5, shipLoc10, true));
        // Game 1 Shots
        Salvo salvo1 = saRepo.save(new Salvo(gp1, 1, Arrays.asList("E8", "E9", "B1"), true));
        Salvo salvo2 = saRepo.save(new Salvo(gp2, 1, Arrays.asList("A1", "B6", "I8"), true));
        Salvo salvo3 = saRepo.save(new Salvo(gp1, 2, Arrays.asList("D4", "F2", "F8"), true));
        Salvo salvo4 = saRepo.save(new Salvo(gp2, 2, Arrays.asList("D4", "F2", "F8"), true));
        Salvo salvo5 = saRepo.save(new Salvo(gp1, 3, Arrays.asList("C9", "C10", "A2"), true));
        Salvo salvo6 = saRepo.save(new Salvo(gp2, 3, Arrays.asList("C9", "C10", "A2"), true));
        Salvo salvo7 = saRepo.save(new Salvo(gp1, 4, Arrays.asList("A3", "G3", "H3"), true));
        Salvo salvo8 = saRepo.save(new Salvo(gp2, 4, Arrays.asList("A3", "G3", "H3"), true));
        Salvo salvo9 = saRepo.save(new Salvo(gp1, 5, Arrays.asList("I3", "D3", "D5"), true));
        Salvo salvo10 = saRepo.save(new Salvo(gp2, 5, Arrays.asList("I3", "D3", "D5"), true));
        Salvo salvo11 = saRepo.save(new Salvo(gp1, 6, Arrays.asList("D6", "G8", "H8"), true));
        Salvo salvo12 = saRepo.save(new Salvo(gp2, 6, Arrays.asList("D6", "G8", "H8"), true));

        // Game 2 Shots
        Salvo salvo13 = saRepo.save(new Salvo(gp3, 1, Arrays.asList("A1", "B6", "I8"), true));
    }

    public static void loadFinishedGames(GameRepository gRepo, PlayerRepository pRepo, GamePlayerRepository gpRepo){
        Player[] players = new Player[5];
        players[0] = (pRepo.findByEmail("j.bauer@ctu.gov").get(0));
        players[1] = (pRepo.findByEmail("c.obrian@ctu.gov").get(0));
        players[2] = (pRepo.findByEmail("presPalmer@allstate.com").get(0));
        players[3] = (pRepo.findByEmail("kim@alwaysHostage.org").get(0));
        players[4] = (pRepo.findByEmail("m.dessler@ctu.gov").get(0));
        for (int i=0;i<10;i++){
            Game g = gRepo.save(new Game(true));
            Random rand = new Random();
            int  n = rand.nextInt(players.length);
            int n2 = rand.nextInt(players.length);
            while (n == n2){
                n2 = rand.nextInt(players.length);
            }
            GamePlayer gp1 = gpRepo.save(new GamePlayer(g, players[n], true, null));
            GamePlayer gp2 = gpRepo.save(new GamePlayer(g, players[n2]));
            g.setFinished(true);
            gp1.setWinner(true);
        }
    }

    public static void resetSampleData(PlayerRepository pRepo, GameRepository gRepo, GamePlayerRepository gpRepo, ShipRepository sRepo, SalvoRepository saRepo){
        Player[] players = new Player[5];
        players[0] = (pRepo.findByEmail("j.bauer@ctu.gov").get(0));
        players[1] = (pRepo.findByEmail("c.obrian@ctu.gov").get(0));
        players[2] = (pRepo.findByEmail("presPalmer@allstate.com").get(0));
        players[3] = (pRepo.findByEmail("kim@alwaysHostage.org").get(0));
        players[4] = (pRepo.findByEmail("m.dessler@ctu.gov").get(0));
        Set<Game> games = new HashSet<Game>();
        Set<GamePlayer> gplayers = new HashSet<GamePlayer>();
        Set<Ship> ships = new HashSet<Ship>();
        Set<Salvo> salvos = new HashSet<Salvo>();
        for (Player p : players) {
            gplayers.addAll(p.getGamePlayers());
        }
        for(GamePlayer gp : gplayers){
            games.add(gp.getGame());
            //gplayers.add(gp);
            for (Ship ship : gp.getShips()){
                ships.add(ship);
            }
            for (Salvo salvo : gp.getSalvos()){
                salvos.add(salvo);
            }
        }

        for (Salvo salvo: salvos){
            saRepo.delete(salvo);
        }
        for (Ship ship : ships){
            sRepo.delete(ship);
        }
        for (GamePlayer gameplayer : gplayers){
            gpRepo.delete(gameplayer);
        }
        for (Game game : games){
            gRepo.delete(game);
        }
        for (Player player: players){
            pRepo.delete(player);
        }
    }
}
