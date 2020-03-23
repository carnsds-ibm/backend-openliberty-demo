package it.io.openliberty.guides.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.openliberty.guides.application.util.DBManager;

public class DBManagerTest {
    @Test
    public void testDatabase() {
        if (DBManager.DATABASE == null) {
            assertTrue(false, "database connection not created");
        } else {
            assertTrue(true, "database connection was successful");
        }
    }

    @Test
    public void testLoginUser() {
        if (DBManager.MONGO_ADMIN == null) {
            assertTrue(false, "failed to login");
        } else {
            assertTrue(true, "successfully logged in");
        }
    }
}