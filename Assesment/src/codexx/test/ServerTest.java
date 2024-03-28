package codexx.test;

import static org.junit.jupiter.api.Assertions.*;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

import codexx.Observer;
import codexx.Server;

class ServerTest {

	@Test
    public void testMain() throws Exception {
        ExecutorService serverThread = Executors.newSingleThreadExecutor();
        serverThread.submit(() -> {
            try {
                Server.main(null); // Start the server
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Wait for some time to allow the server to start
        Thread.sleep(1000);

        // Attempt to connect to the server
        try (Socket testSocket = new Socket("localhost", 3000)) {
            assertTrue(testSocket.isConnected(), "Server is running and accepting connections");
        } finally {
            serverThread.shutdownNow(); // Shutdown the server thread
        }
    }


	@Test
	void testRegister() {
		 Server testServer = new Server();
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        
        testServer.register(observer1);
        testServer.register(observer2);
        
        testServer.notifyObservers("Test message");
        
        assertTrue(observer1.isUpdated());
        assertTrue(observer2.isUpdated());
	}

	@Test
	void testUnregister() {
		Server testServer = new Server();
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        
        testServer.register(observer1);
        testServer.register(observer2);
        
        testServer.unregister(observer1);
        testServer.notifyObservers("Test message");
        
        assertFalse(observer1.isUpdated());
        assertTrue(observer2.isUpdated());
	}

	@Test
	void testNotifyObservers() {
	    // Create a server instance
	    Server testServer = new Server();

	    // Create test observers
	    TestObserver observer1 = new TestObserver();
	    TestObserver observer2 = new TestObserver();

	    // Register observers with the server
	    testServer.register(observer1);
	    testServer.register(observer2);

	    // Notify observers with a test message
	    testServer.notifyObservers("Test message");

	    // Check if both observers are updated
	    assertTrue(observer1.isUpdated());
	    assertTrue(observer2.isUpdated());

	    // Reset the flag for further tests
	    observer1.reset();
	    observer2.reset();

	    // Unregister one observer and notify again
	    testServer.unregister(observer2);
	    testServer.notifyObservers("A new test message");

	    // Check that only the remaining observer is updated
	    assertTrue(observer1.isUpdated());
	    assertFalse(observer2.isUpdated());
	}
	
	private class TestObserver implements Observer {
        private boolean updated = false;

        @Override
        public void update(String message) {
            updated = true;
        }

        public void reset() {
			updated = false;
		}

		public boolean isUpdated() {
            return updated;
        }
	}
}
