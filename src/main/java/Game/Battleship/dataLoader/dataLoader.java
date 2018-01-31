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
public class dataLoader {

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (args) -> {
            Player p1 = playerRepository.save(new Player("Jack", "Bauer", "j.bauer@ctu.gov", "password"));
            p1.setPassword("j");
        };
    }

    @Bean
    public CommandLineRunner initData2(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (args) -> {
            Player p1 = playerRepository.save(new Player("James", "Bond", "007@spy.gov", "7"));
            //Score score5 = scoreRepository.save(new Score(g3, p3, "Win", 3));
        };
    }
}