package info.ragozin.perflab.hazelagg;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import info.ragozin.perflab.hazelagg.kryo.KryoConfigurer;

public class HazelcastDemo {

    @Test
    public void testPositionAggregation() throws FileNotFoundException {

        Config cfg = new FileSystemXmlConfig("node-conf.xml");
        
        HazelcastInstance node = Hazelcast.newHazelcastInstance(cfg);
        
        IMap<PositionKey, Position> cache = node.getMap("positions");
        
        update(cache, Position.update(1, 100, "A", "X", "SPOT", 10));
        update(cache, Position.update(2, 100, "A", "X", "SPOT", 11));
        update(cache, Position.update(3, 100, "B", "X", "SPOT", 12));
        update(cache, Position.update(1, 200, "A", "X", "SPOT", 13));
        update(cache, Position.update(4, 400, "C", "X", "SPOT", 14));
        update(cache, Position.remove(1, 300, "A", "X", "SPOT"));
        
        
        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(cfg);
        
        IMap<PositionKey, Position> cache2 = node2.getMap("positions");

        System.out.println("110 -> " + cache2.aggregate(PositionAggregation.all(), new PositionAggregation(110)));
        System.out.println("210 -> " + cache2.aggregate(PositionAggregation.all(), new PositionAggregation(210)));
        System.out.println("310 -> " + cache2.aggregate(PositionAggregation.all(), new PositionAggregation(310)));
        System.out.println("410 -> " + cache2.aggregate(PositionAggregation.all(), new PositionAggregation(410)));
        System.out.println("510 -> " + cache2.aggregate(PositionAggregation.all(), new PositionAggregation(510)));
        
        System.out.println("210 [A] -> " + cache2.aggregate(PositionAggregation.sliceSupplier(SliceKey.book("A")), new PositionAggregation(510)));
    }

    
    @Test
    public void tesStresstPositionAggregation() throws FileNotFoundException, InterruptedException {

        Config cfg = new FileSystemXmlConfig("node-conf.xml");
        Config cfgL = new FileSystemXmlConfig("node-conf-lite.xml");
        
        KryoConfigurer.configure(cfg);
        KryoConfigurer.configure(cfgL);
        
        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(cfg);
        Thread.sleep(10);
        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(cfg);
        Thread.sleep(10);
        HazelcastInstance node3 = Hazelcast.newHazelcastInstance(cfg);
        Thread.sleep(10);
        
        HazelcastInstance nodeL = Hazelcast.newHazelcastInstance(cfgL);
        
        node1.getMap("positions");
        node2.getMap("positions");
        node3.getMap("positions");
        
        IMap<PositionKey, Position> cache = nodeL.getMap("positions");
        
        Random rnd = new Random();
        
        System.out.println("Generating data");

        for(int i = 0; i != 10000; ++i) {
            String book = "B" + rnd.nextInt(100);
            String under = "U" + rnd.nextInt(100);
            String cornt = "C" + rnd.nextInt(10);
            
            long id = rnd.nextInt(11000) % 10000;
            long ts = rnd.nextInt(100);
            
            update(cache, Position.update(id, ts, book, under, cornt, 0.1d * rnd.nextInt(1000)));
        }
        
        
        IMap<PositionKey, Position> cacheL = nodeL.getMap("positions");

        System.out.println("Data generated");
        System.out.println("Data -> " + cacheL.aggregate(PositionAggregation.all(), new PositionAggregation(110)));

        for(int i = 0; i != 100000; ++i) {

            String book = "B" + rnd.nextInt(100);
            long ts = rnd.nextInt(100);

            long time = System.currentTimeMillis();
            
            Map<SliceKey, Double> data = cacheL.aggregate(PositionAggregation.sliceSupplier(SliceKey.book(book)), new PositionAggregation(ts));
            
            time = System.currentTimeMillis() - time;
            System.out.println("Book [" + book + "] at " + time + "ms -> " + data.size() + " slices");
        }
        
    }
    
    private void update(IMap<PositionKey, Position> cache, Position event) {
        cache.put(event.getKey(), event);        
    }
}
