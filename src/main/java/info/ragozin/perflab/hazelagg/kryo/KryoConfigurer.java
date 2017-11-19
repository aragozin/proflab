package info.ragozin.perflab.hazelagg.kryo;

import java.util.Collection;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;

import info.ragozin.perflab.hazelagg.Position;
import info.ragozin.perflab.hazelagg.PositionKey;
import info.ragozin.perflab.hazelagg.SliceKey;

public class KryoConfigurer {

    public static void configure(Config config) {
        Collection<SerializerConfig> section = config.getSerializationConfig().getSerializerConfigs();
        
        section.add(new SerializerConfig()
                    .setTypeClass(Position.class)
                    .setImplementation(new KryoSerializer<Position>(Position.class)));

        section.add(new SerializerConfig()
                .setTypeClass(PositionKey.class)
                .setImplementation(new KryoSerializer<PositionKey>(PositionKey.class)));

        section.add(new SerializerConfig()
                .setTypeClass(SliceKey.class)
                .setImplementation(new KryoSerializer<SliceKey>(SliceKey.class)));
    }
    
}
