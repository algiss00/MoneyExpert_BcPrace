package cz.cvut.fel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableSpringHttpSession
public class MoneyExpertApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyExpertApplication.class, args);
    }
}
