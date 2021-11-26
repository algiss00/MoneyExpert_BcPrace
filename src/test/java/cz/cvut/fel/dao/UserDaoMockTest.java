package cz.cvut.fel.dao;

import cz.cvut.fel.model.User;
import generator.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;

public class UserDaoMockTest {
    private UserDao userDao;
    private EntityManager entityManagerMock;

    @BeforeEach
    public void setUp() {
        entityManagerMock = mock(EntityManager.class);
        userDao = new UserDao(entityManagerMock);
    }

    @Test
    public void persist_mockTestEm_success() {
        User user = Generator.generateDefaultUser();
        userDao.persist(user);
        verify(entityManagerMock, times(1)).persist(user);
    }

    @Test
    public void remove_MockTestEm_success() {
        User user = Generator.generateDefaultUser();
        userDao.persist(user);
        verify(entityManagerMock, times(1)).persist(user);
        userDao.remove(user);
        verify(entityManagerMock, times(1)).remove(any());
    }

    @Test
    public void update_mockTestEm_success() {
        User user = Generator.generateDefaultUser();
        userDao.persist(user);
        user.setUsername("mock-test");
        when(entityManagerMock.merge(any())).thenReturn(user);
        assertEquals(user, userDao.update(user));
        verify(entityManagerMock, times(1)).merge(user);
    }

    @Test
    public void find_mockTestEm_success() {
        User user = Generator.generateDefaultUser();
        userDao.persist(user);
        verify(entityManagerMock, times(1)).persist(user);
        userDao.find(user.getId());
        verify(entityManagerMock, times(1)).find(user.getClass(), user.getId());
    }

}
