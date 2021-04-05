package cz.cvut.fel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

//implements CommandLineRunner
@SpringBootApplication
public class MoneyExpertApplication {

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(MoneyExpertApplication.class, args);
    }

//    @Override
//    public void run(String... args) {
//        String sql = "INSERT INTO users(username, password, email, name, lastname) " +
//                "VALUES (?, ?, ?, ?, ?)";
//        int result = jdbcTemplate.update(sql, "test", "testpass", "asd@asd", "name", "lastname");
//
//        if (result > 0) {
//            System.out.println("success");
//        }
//    }

}
