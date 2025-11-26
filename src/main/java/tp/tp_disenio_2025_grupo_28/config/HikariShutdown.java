package tp.tp_disenio_2025_grupo_28.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PreDestroy;

@Component
public class HikariShutdown {

    @Autowired
    private HikariDataSource dataSource;

    @PreDestroy
    public void close() {
        System.out.println("Cerrando HikariPool...");
        dataSource.close();
    }
}
