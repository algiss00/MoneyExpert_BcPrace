package cz.cvut.fel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.jdbc.Sql;

@SpringBootApplication
public class MoneyExpertApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyExpertApplication.class, args);
    }
}
