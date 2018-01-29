package Game.Battleship.dataLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import Game.Battleship.entity.*;
import Game.Battleship.repository.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

//@Component
@RestController
public class dataLoader {

    @RequestMapping("/load")
    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (args) -> {
            Player p1 = playerRepository.save(new Player("Jack", "Bauer", "j.bauer@ctu.gov", "password"));
            p1.setPassword("j");
            playerRepository.save(p1);
            Player p2 = playerRepository.save(new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", "c"));
            Player p3 = playerRepository.save(new Player("Kim", "Bauer", "k", "k"));
            p3.setPassword("k");
            playerRepository.save(p3);
            Player p4 = playerRepository.save(new Player("David", "Palmer", "dp@aol.com", "d"));
            Player p5 = playerRepository.save(new Player("Michelle", "Dessler", "md@.com", "m"));
            Game g1 = gameRepository.save(new Game());
            Game g2 = gameRepository.save(new Game());
            Game g3 = gameRepository.save(new Game());
            GamePlayer gp1 = gamePlayerRepository.save(new GamePlayer(g1, p1));
            GamePlayer gp2 = gamePlayerRepository.save(new GamePlayer(g1, p2));
            GamePlayer gp3 = gamePlayerRepository.save(new GamePlayer(g2, p4));
            GamePlayer gp4 = gamePlayerRepository.save(new GamePlayer(g2, p2));
            GamePlayer gp5 = gamePlayerRepository.save(new GamePlayer(g3, p3));
            List<String> shipLoc1 = Arrays.asList("A1", "A2", "A3");
            List<String> shipLoc2 = Arrays.asList("F8", "G8", "H8", "I8");
            List<String> shipLoc3 = Arrays.asList("C4", "D4", "E4", "F4");
            List<String> salvoLoc1 = Arrays.asList("A1", "B6", "I8");
            List<String> salvoLoc2 = Arrays.asList("A1", "E4", "F7");
            Ship s1 = shipRepository.save(new Ship("Long", gp1, shipLoc1));
            Ship s2 = shipRepository.save(new Ship("Short", gp1, shipLoc2));
            Ship s3 = shipRepository.save(new Ship("Long", gp2, shipLoc3));
            Ship s4 = shipRepository.save(new Ship("Long", gp3, shipLoc1));
            Salvo salvo1 = salvoRepository.save(new Salvo(gp1, 1, salvoLoc1));
            Salvo salvo2 = salvoRepository.save(new Salvo(gp1, 2, salvoLoc2));
            Salvo salvo3 = salvoRepository.save(new Salvo(gp2, 1, salvoLoc1));
            Score score2 = scoreRepository.save(new Score(g1, p1, "Lose", 0));
            Score score1 = scoreRepository.save(new Score(g1, p2, "Win", 3));
            Score score4 = scoreRepository.save(new Score(g2, p2, "Tie", 1));
            Score score3 = scoreRepository.save(new Score(g2, p4, "Tie", 1));
            Score score5 = scoreRepository.save(new Score(g3, p3, "Win", 3));
        };
    }

    //@Bean
    public CommandLineRunner initData2(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (args) -> {
            Player p1 = playerRepository.save(new Player("James", "Bond", "007@spy.gov", "7"));
            //Score score5 = scoreRepository.save(new Score(g3, p3, "Win", 3));
        };
    }
}