package threads;

public class ThreadUtility {
	
	  public static void logThreadDetails() {
		    Thread[] threads = new Thread[Thread.activeCount()];
		    Thread.enumerate(threads);
		    for (int i = 0; i < threads.length; i++) {
		      System.out.println("{}"+threads[i]);
		    }
		  }

		  public static void sleepQuietly(long millis) {
		    try {
		      Thread.sleep(millis);
		    } catch (Exception e) {
		      System.out.println("{}"+e);
		    }
		  }

}
