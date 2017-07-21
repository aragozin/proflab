package info.ragozin.proflab;

public class DemoRunner {

    public DemoRunner(final Runnable task) {
    
        System.out.println("Running: " + task.getClass().getSimpleName());
        
        Thread thread = new Thread() {

            int c = 0;
            long[] history = new long[100];
            
            @Override
            public void run() {
                
                while(true) {
                    long n = System.currentTimeMillis();
                    task.run();
                    long time = System.currentTimeMillis() - n;
                    push(time);
                    System.out.println("Iteration " + c + " completed in " + time + "ms, sliding average: " + average() + "ms");
                }        
            }
            
            private String average() {
                long total = 0;
                long count = 0;
                if (c < history.length) {
                    for(int i = 0; i != c; ++i) {
                        total += history[i];
                        count += 1;
                    }
                }
                else {
                    for(int i = 0; i != history.length; ++i) {
                        total += history[i];
                        count += 1;
                    }                    
                }
                return String.valueOf(total / count);
            }

            private void push(long time) {
                history[c % history.length] = time;
                ++c;                
            }
        };
        
        thread.setName(task.getClass().getSimpleName());
        thread.setDaemon(true);
        thread.start();
        
        try {
        	while(true) {
        		Thread.sleep(1000);
        	}
        }
        catch(Exception e) {
            e.printStackTrace();
        }    
    }    
}
