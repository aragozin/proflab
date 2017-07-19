package info.ragozin.proflab;

import java.io.IOException;

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
        
        thread.start();
        
        try {
            StringBuilder sb = new StringBuilder();
            while(true) {
                int n = System.in.read();
                sb.append((char)n);
                if (n < 0 || sb.toString().toLowerCase().endsWith("end")) {
                    System.err.println("Aborted by console");
                    System.exit(1);
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }    
    }    
}
