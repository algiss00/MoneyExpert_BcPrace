package generator;

import cz.cvut.fel.model.Category;
import cz.cvut.fel.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    private static final Random RAND = new Random();

    public static User generateUser(String username, String password, String name, String lastname, String email) {
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        return user;
    }

    public static User generateDefaultUser() {
        final User user = new User();
        user.setUsername("username" + randomInt());
        user.setPassword("password");
        user.setName("name");
        user.setLastname("lastname");
        user.setEmail("email" + randomInt());
        return user;
    }

    public static List<Category> generateDefaultCategories() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Category category = new Category();
            category.setName("DefaultCategory" + i);
            categories.add(category);
        }
        return categories;
    }

    public static int randomInt() {
        return RAND.nextInt();
    }
}
