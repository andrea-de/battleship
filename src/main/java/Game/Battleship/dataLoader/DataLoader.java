package Game.Battleship.dataLoader;

import Game.Battleship.controller.DevController;
import Game.Battleship.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import Game.Battleship.entity.*;
import Game.Battleship.repository.*;


@Component
public class DataLoader {

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

    @Bean
    public CommandLineRunner someInitData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (String... args) -> {
            if (pRepo.findAll().isEmpty()) {
                DataLoaderService.loadPlayers(pRepo);
                DataLoaderService.loadGames(gRepo, gpRepo, pRepo, sRepo, saRepo);
                DataLoaderService.loadFinishedGames(gRepo, pRepo, gpRepo);
            }
        };
    }

    @Bean
    public CommandLineRunner moreInitData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {

        return (args) -> {
            Player p1 = new Player("James", "Bond", "00", "7");
            //playerRepository.save(p1);
        };
    }
}