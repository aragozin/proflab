package info.ragozin.perflab.hazelagg;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.PredicateSupplier;
import com.hazelcast.query.Predicate;

@SuppressWarnings("serial")
public class PositionAggregation implements Aggregation<PositionKey, Position, Map<SliceKey, BigDecimal>>, Serializable {

    public static Supplier<PositionKey, Position, Position> all() {
        return Supplier.all();
    }

    public static Supplier<PositionKey, Position, Position> sliceSupplier(SliceKey key) {
        return new PredicateSupplier<>(new SlicePredicate(key));
    }

    public static Predicate<PositionKey, Position> slice(SliceKey key) {
        return new SlicePredicate(key);
    }
    
    private final long timestamp;
    
    public PositionAggregation(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collator<Entry, Map<SliceKey, BigDecimal>> getCollator() {
        return (Collator)new Collator<Map.Entry<SliceKey, Map<SliceKey,BigDecimal>>, Map<SliceKey,BigDecimal>>() {

            @Override
            public Map<SliceKey, BigDecimal> collate(Iterable<Entry<SliceKey, Map<SliceKey,BigDecimal>>> values) {
                Map<SliceKey, BigDecimal> result = new HashMap<SliceKey, BigDecimal>();
                for(Entry<SliceKey, Map<SliceKey,BigDecimal>> entry: values) {
                    for(Entry<SliceKey, BigDecimal> e: entry.getValue().entrySet()) {
                        if (e.getValue().signum() == 0) {
                            // ignore 0
                            continue;
                        }
                        BigDecimal v = result.get(e.getKey());
                        if (v == null) {
                            result.put(e.getKey(), e.getValue());
                        }
                        else {
                            v = v.add(e.getValue());
                            result.put(e.getKey(), v);
                        }                        
                    }                    
                }
                return result;
            }
        };
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Mapper getMapper(Supplier<PositionKey, ?, Position> supplier) {
        return new SliceMapper((Supplier)supplier);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public CombinerFactory getCombinerFactory() {
        return new CFactory();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ReducerFactory getReducerFactory() {
        return new RFactory(timestamp);
    }
    
    private static class SliceMapper implements Mapper<PositionKey, Position, SliceKey, Position>, Serializable {

        private final Supplier<PositionKey, Position, Position> supplier;
        
        public SliceMapper(Supplier<PositionKey, Position, Position> supplier) {
            this.supplier = supplier;
        }

        @Override
        public void map(final PositionKey key, final Position value, Context<SliceKey, Position> context) {
            SliceKey mkey = new SliceKey(value.getBook(), value.getUnderlying(), value.getContract());
            context.emit(mkey, supplier.apply(new Map.Entry<PositionKey, Position>() {

                @Override
                public PositionKey getKey() {
                    return key;
                }

                @Override
                public Position getValue() {
                    return value;
                }

                @Override
                public Position setValue(Position value) {
                    throw new UnsupportedOperationException();
                }
            }));            
        }
    }
    
    private static class RFactory implements ReducerFactory<SliceKey, List<Position>, Map<SliceKey, BigDecimal>>, Serializable {
        
        private final long timestamp;
        
        public RFactory(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public Reducer<List<Position>, Map<SliceKey, BigDecimal>> newReducer(final SliceKey slice) {
            return new Reducer<List<Position>, Map<SliceKey, BigDecimal>>() {

                Map<Long, Position> surface = new HashMap<Long, Position>();
                
                @Override
                public void reduce(List<Position> values) {
                    for(Position value: values) {                        
                        if (value.getTimestamp() <= timestamp) {
                            Long key = value.getPositionId();
                            Position p = surface.get(key);
                            if (p == null || value.getTimestamp() > p.getTimestamp()) {
                                surface.put(key, value);
                            }
                        }
                    }
                }

                @Override
                public Map<SliceKey, BigDecimal> finalizeReduce() {
                    BigDecimal qty = new BigDecimal(0);
                    for(Position pos: surface.values()) {
                        if (pos.isActive()) {
                            qty = qty.add(new BigDecimal(pos.getQty()));
                        }
                    }
                    
                    return Collections.singletonMap(slice, qty);
                }
            };
        }
    }
    
    private static class CFactory implements CombinerFactory<SliceKey, Position, List<Position>>, Serializable {

        @Override
        public Combiner<Position, List<Position>> newCombiner(final SliceKey key) {
            return new Combiner<Position, List<Position>>() {

                List<Position> positions = new ArrayList<Position>();
                
                @Override
                public void combine(Position value) {
                    if (value != null) {
                        positions.add(value);
                    }
                }

                @Override
                public List<Position> finalizeChunk() {
                    return positions;
                }
            };
        }
    }
    
    private static class SlicePredicate implements Predicate<PositionKey, Position>, Serializable {

        private final SliceKey slice;
        
        public SlicePredicate(SliceKey slice) {
            this.slice = slice;
        }

        @Override
        public boolean apply(Entry<PositionKey, Position> mapEntry) {
            Position p = mapEntry.getValue();
            if (slice.getBook() != null && !slice.getBook().equals(p.getBook())) {
                return false;
            }
            if (slice.getUnderlaying() != null && !slice.getUnderlaying().equals(p.getUnderlying())) {
                return false;
            }
            if (slice.getContract() != null && !slice.getUnderlaying().equals(p.getContract())) {
                return false;
            }
            return true;
        }
    }
}
