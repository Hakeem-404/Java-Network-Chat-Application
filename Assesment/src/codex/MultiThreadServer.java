package codex;

public class MultiThreadServer extends Thread {
  public static void main(String[] args) {
    MultiThreadServer thread = new MultiThreadServer();
    thread.start();
    System.out.println("This code is outside of the thread");
  }
  public void run() {
    System.out.println("This code is running in a thread");
  }
}