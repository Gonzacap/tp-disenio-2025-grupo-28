package tp.tp_disenio_2025_grupo_28;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TpDisenio2025Grupo28Application {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load(); // carga el .env
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		
		SpringApplication.run(TpDisenio2025Grupo28Application.class, args);
	}

}
