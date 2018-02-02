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
        Player p3 = pRepo.save(new Player("Kim", "Bauer", "k", "kb"));
        p3.setPassword("k");
        pRepo.save(p3);
        Player p4 = pRepo.save(new Player("David", "Palmer", "dp@aol.com", "d"));
        Player p5 = pRepo.save(new Player("Michelle", "Dessler", "md", "m"));
    }

    public static void loadGames(GameRepository gRepo, GamePlayerRepository gpRepo, PlayerRepository pRepo, ShipRepository sRepo, SalvoRepository saRepo){
        // Games
        Game g1 = gRepo.save(new Game());
        Game g2 = gRepo.save(new Game());
        Game g3 = gRepo.save(new Game());
        GamePlayer gp1 = gpRepo.save(new GamePlayer(g1, pRepo.findByEmail("j.bauer@ctu.gov").get(0)));
        GamePlayer gp2 = gpRepo.save(new GamePlayer(g1, pRepo.findByEmail("c.obrian@ctu.gov").get(0)));
        GamePlayer gp3 = gpRepo.save(new GamePlayer(g2, pRepo.findByEmail("c.obrian@ctu.gov").get(0)));
        GamePlayer gp4 = gpRepo.save(new GamePlayer(g2, pRepo.findByEmail("dp@aol.com").get(0)));
        GamePlayer gp5 = gpRepo.save(new GamePlayer(g3, pRepo.findByEmail("k").get(0)));
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
        Ship s1 = sRepo.save(new Ship("Destroyer", gp1, shipLoc1));
        Ship s2 = sRepo.save(new Ship("Submarine", gp1, shipLoc2));
        Ship s3 = sRepo.save(new Ship("Cruiser", gp1, shipLoc3));
        Ship s4 = sRepo.save(new Ship("Battleship", gp1, shipLoc4));
        Ship s5 = sRepo.save(new Ship("Carrier", gp1, shipLoc5));
        Ship s6 = sRepo.save(new Ship("Destroyer", gp4, shipLoc1));
        Ship s7 = sRepo.save(new Ship("Submarine", gp4, shipLoc2));
        Ship s8 = sRepo.save(new Ship("Cruiser", gp4, shipLoc3));
        Ship s9 = sRepo.save(new Ship("Battleship", gp4, shipLoc4));
        Ship s10 = sRepo.save(new Ship("Carrier", gp4, shipLoc5));
        // Ships for Gameplayer 2 & 3
        Ship s11 = sRepo.save(new Ship("Destroyer", gp2, shipLoc6));
        Ship s12 = sRepo.save(new Ship("Submarine", gp2, shipLoc7));
        Ship s13 = sRepo.save(new Ship("Cruiser", gp2, shipLoc8));
        Ship s14 = sRepo.save(new Ship("Battleship", gp2, shipLoc9));
        Ship s15 = sRepo.save(new Ship("Carrier", gp2, shipLoc10));
        Ship s16 = sRepo.save(new Ship("Destroyer", gp3, shipLoc6));
        Ship s17 = sRepo.save(new Ship("Submarine", gp3, shipLoc7));
        Ship s18 = sRepo.save(new Ship("Cruiser", gp3, shipLoc8));
        Ship s19 = sRepo.save(new Ship("Battleship", gp3, shipLoc9));
        Ship s20 = sRepo.save(new Ship("Carrier", gp3, shipLoc10));
        // Ships for Gameplayer 5
        Ship s21 = sRepo.save(new Ship("Destroyer", gp5, shipLoc6));
        Ship s22 = sRepo.save(new Ship("Submarine", gp5, shipLoc7));
        Ship s23 = sRepo.save(new Ship("Cruiser", gp5, shipLoc8));
        Ship s24 = sRepo.save(new Ship("Battleship", gp5, shipLoc9));
        Ship s25 = sRepo.save(new Ship("Carrier", gp5, shipLoc10));
        // Game 1 Shots
        Salvo salvo1 = saRepo.save(new Salvo(gp1, 1, Arrays.asList("A1", "C6", "I8"), true));
        Salvo salvo2 = saRepo.save(new Salvo(gp2, 1, Arrays.asList("A8", "B6", "I9"), true));
        Salvo salvo3 = saRepo.save(new Salvo(gp1, 1, Arrays.asList("D4", "F2", "E8"), true));
        Salvo salvo4 = saRepo.save(new Salvo(gp2, 1, Arrays.asList("D8", "I3", "E8"), true));
        // Game 2 Shots
        Salvo salvo5 = saRepo.save(new Salvo(gp3, 1, Arrays.asList("A1", "B6", "I8"), true));
    }

    // Reconcile remaining coordinates for each GamePlayer
    public static void reconcile(GameRepository gRepo, GamePlayerRepository gpRepo, PlayerRepository pRepo, ShipRepository sRepo, SalvoRepository saRepo){
        for (GamePlayer gp : gpRepo.findAll()){
            for (Ship ship: gp.getShips()){
                //System.out.println(ship.getGamePlayer());
                //System.out.println(ship.getShipType());
                //gp.remainingShipCoordinates.addAll(ship.getLocation());
            }

        }
    }

    public static void loadFinishedGames(GameRepository gRepo, PlayerRepository pRepo, GamePlayerRepository gpRepo){
        Player[] playerset = new Player[5];
        playerset[0] = (pRepo.findByEmail("j.bauer@ctu.gov").get(0));
        playerset[1] = (pRepo.findByEmail("c.obrian@ctu.gov").get(0));
        playerset[2] = (pRepo.findByEmail("dp@aol.com").get(0));
        playerset[3] = (pRepo.findByEmail("k").get(0));
        playerset[4] = (pRepo.findByEmail("md").get(0));
        for (int i=0;i<10;i++){
            Game g = gRepo.save(new Game(true));
            Random rand = new Random();
            int  n = rand.nextInt(playerset.length);
            int n2 = rand.nextInt(playerset.length);
            while (n == n2){
                n2 = rand.nextInt(playerset.length);
            }
            GamePlayer gp1 = gpRepo.save(new GamePlayer(g, playerset[n], true));
            GamePlayer gp2 = gpRepo.save(new GamePlayer(g, playerset[n2]));
            g.setFinished(true);
            gp1.winner = true;
            //System.out.println("Game: " + g + "is finished: " + g.finished);
            //System.out.println("And the winner is: " + gp1);
        }
    }
}
