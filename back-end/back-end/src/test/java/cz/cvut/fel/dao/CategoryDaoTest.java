package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Category;
import generator.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ComponentScan(basePackageClasses = MoneyExpertApplication.class)
@Sql("classpath:test-data.sql")
public class CategoryDaoTest {
    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void find() {
        Category category = categoryDao.find(-1);
        assertNotNull(category);
        assertEquals("Auto", category.getName());
    }

    @Test
    public void getUsersCategory() {
        List<Category> categories = categoryDao.getUsersCategory(10);
        assertFalse(categories.isEmpty());
        assertEquals(2, categories.size());

        int[] ids = {2, 1};
        for (int i = 0; i < categories.size(); i++) {
            assertEquals(ids[i], categories.get(i).getId());
        }
    }

    @Test
    public void getUsersCategoryEmpty() {
        List<Category> categories = categoryDao.getUsersCategory(12);
        assertTrue(categories.isEmpty());
    }

    @Test
    public void getUsersCategoryById() throws Exception {
        Category category = categoryDao.getUsersCategoryById(10, 2);
        assertNotNull(category);
        assertEquals("Jidlo", category.getName());
    }

    @Test
    public void getUsersCategoryByIdEmpty() throws Exception {
        Category category = categoryDao.getUsersCategoryById(10, 7);
        assertNull(category);
    }

    @Test
    public void persist() {
        Category generatedCategory = Generator.generateDefaultCategory();
        Category persistedCategory = categoryDao.persist(generatedCategory);
        assertNotNull(persistedCategory);
        assertEquals(generatedCategory.getName(), persistedCategory.getName());
    }

    @Test
    public void remove() {
        Category category = categoryDao.find(1);
        assertNotNull(category);
        categoryDao.remove(category);
        assertNull(categoryDao.find(1));
    }

    @Test
    public void update() {
        Category category = categoryDao.find(-3);
        assertNotNull(category);
        assertEquals("Skola", category.getName());
        category.setName("test update name");
        categoryDao.update(category);
        Category updatedCategory = categoryDao.find(-3);
        assertEquals("test update name", updatedCategory.getName());
    }
}
