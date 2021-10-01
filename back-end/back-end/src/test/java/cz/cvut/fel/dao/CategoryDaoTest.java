package cz.cvut.fel.dao;

import cz.cvut.fel.MoneyExpertApplication;
import cz.cvut.fel.model.Budget;
import cz.cvut.fel.model.Category;
import generator.Generator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
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
    public void persist() {
        Category generatedCategory = Generator.generateDefaultCategory();
        Category persistedCategory = categoryDao.persist(generatedCategory);
        assertNotNull(persistedCategory);
        assertEquals(generatedCategory.getName(), persistedCategory.getName());
    }

    @Test
    public void remove() {
        Category category = categoryDao.find(-2);
        assertNotNull(category);
        categoryDao.remove(category);
        assertNull(categoryDao.find(-2));
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
