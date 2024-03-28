package codexx.test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import codexx.Client;

class ClientTest {
    private Client client;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        client = new Client("localhost", "3000");
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    void testClient() {
        assertNotNull(client);
    }

    @Test
    public void testGetName() {
        // Mock JOptionPane to simulate user input
        String expectedName = "TestUser";
        String inputName = expectedName + "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inputName.getBytes());
        System.setIn(in);

        // Call the method to be tested
        String name = client.getName();

        // Assertions
        assertEquals(expectedName, name);
        assertNotNull(name);
    }
}