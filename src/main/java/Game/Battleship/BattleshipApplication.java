package Game.Battleship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@SpringBootApplication
public class BattleshipApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleshipApplication.class, args);
		System.out.println("Website Ready");
	}

	@Configuration
	public class DatabaseConfig {
		@Bean
		@Primary
		@ConfigurationProperties(prefix = "spring.datasource")
		public DataSource dataSource() {
			return DataSourceBuilder.create().build();
		}
	}
}

