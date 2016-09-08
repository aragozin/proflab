package info.ragozin.perflab.hazelagg;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

@SuppressWarnings("serial")
public class DataPosition extends Position implements DataSerializable {

    public DataPosition() {
        super(0, 0);
    }
    
    public DataPosition(long positionId, long timestamp) {
        super(positionId, timestamp);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(positionId);
        out.writeLong(timestamp);
        out.writeUTF(book);
        out.writeUTF(underlying);
        out.writeUTF(contract);
        out.writeDouble(qty);
        out.writeBoolean(active);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        positionId = in.readLong();
        timestamp = in.readLong();
        book = in.readUTF();
        underlying = in.readUTF();
        contract = in.readUTF();
        qty = in.readDouble();
        active = in.readBoolean();
    }
}
