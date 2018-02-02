package Game.Battleship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BattleshipApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleshipApplication.class, args);
		System.out.println("Website Ready");
	}

//	@Configuration
//	public class DatabaseConfig {
//		@Bean
//		@Primary
//		@ConfigurationProperties(prefix = "spring.datasource")
//		public DataSource dataSource() {
//			return DataSourceBuilder.create().build();
//		}
//	}
}

