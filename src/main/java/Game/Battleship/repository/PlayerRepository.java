package Game.Battleship.repository;

import Game.Battleship.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //public void findByEmail();

    //List<Player> findByEmail(@Param("email") String email);
    public List<Player> findByEmail(String email);
}
