package info.ragozin.perflab.hazelagg;

import java.io.Serializable;


@SuppressWarnings("serial")
public class PositionKey implements Serializable {

    private long positionId;
    private long timestamp;
    
    PositionKey() {
        // for serialization
    }
    
    public PositionKey(long positionId, long timestamp) {
        this.positionId = positionId;
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (positionId ^ (positionId >>> 32));
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PositionKey other = (PositionKey) obj;
        if (positionId != other.positionId)
            return false;
        if (timestamp != other.timestamp)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return positionId + "@" + timestamp;
    }        
}
