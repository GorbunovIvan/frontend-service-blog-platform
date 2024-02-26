package org.example.frontendservice.model;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class EntityWithIdTest {

    @Test
    void createEntityWithId_User() {

        var id = 1L;

        var result = EntityWithId.createEntityWithId(id, User.class);
        assertNotNull(result);
        assertEquals(result.getClass(), User.class);
        assertNotNull(result.getId());
        assertEquals(result.getId(), id);
    }

    @Test
    void createEntityWithId_Post() {

        var id = 1L;

        var result = EntityWithId.createEntityWithId(id, Post.class);
        assertNotNull(result);
        assertEquals(result.getClass(), Post.class);
        assertNotNull(result.getId());
        assertEquals(result.getId(), id);
    }

    @Test
    void createEntityWithId_Comment() {

        var id = "234234";

        var result = EntityWithId.createEntityWithId(id, Comment.class);
        assertNotNull(result);
        assertEquals(result.getClass(), Comment.class);
        assertNotNull(result.getId());
        assertEquals(result.getId(), id);
    }

    @Test
    void createEntityWithId_ClassCastException() {

        var id = 1;

        var newClass = new EntityWithId<Integer>() {
            @Override
            public Integer getId() {
                return null;
            }
            @Override
            public void setId(Integer id) {
            }
        };

        assertThrows(ClassCastException.class, () -> EntityWithId.createEntityWithId(id, newClass.getClass()));
    }
}