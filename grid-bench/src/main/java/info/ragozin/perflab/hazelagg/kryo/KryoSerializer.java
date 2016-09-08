package info.ragozin.perflab.hazelagg.kryo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

public class KryoSerializer<T> implements StreamSerializer<T> {

    private static final List<Class<?>> KNOWN_CLASSES = new ArrayList<Class<?>>();

    private static final ThreadLocal<byte[]> buffer = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[64 << 10];
        }
    };
    
    private static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {

        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            for (Class<?> c : KNOWN_CLASSES) {
                kryo.register(c);
            }
            return kryo;
        }
    };

    private final Class<T> type;
    private final int typeId;

    public KryoSerializer(Class<T> c) {
        if (!KNOWN_CLASSES.contains(c)) {
            KNOWN_CLASSES.add(c);
        }
        type = c;
        typeId = 10 + KNOWN_CLASSES.indexOf(c);
    }

    public int getTypeId() {
        return typeId;
    }

    public void write(ObjectDataOutput objectDataOutput, T value) throws IOException {
        Kryo kryo = KRYO.get();
        byte[] buf = buffer.get();
        Output output = new Output(buf);
        kryo.writeObject(output, value);
        int len = output.position();
        objectDataOutput.writeInt(len);
        objectDataOutput.write(buf, 0, output.position());
    }

    public T read(ObjectDataInput objectDataInput) throws IOException {
        Kryo kryo = KRYO.get();
        byte[] buf = buffer.get();
        int len = objectDataInput.readInt();
        objectDataInput.readFully(buf, 0, len);
        Input input = new Input(buf, 0, len);
        T result = kryo.readObject(input, type);
        return result;
    }

    public void destroy() {
    }
}