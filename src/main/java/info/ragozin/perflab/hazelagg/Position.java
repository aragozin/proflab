package info.ragozin.perflab.hazelagg;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Position implements Serializable {
    
    public static Position update(long id, long timestamp, String book, String underlying, String contract, double qty) {
        Position p = new Position(id, timestamp);
        p.setBook(book);
        p.setUnderlying(underlying);
        p.setContract(contract);
        p.setQty(qty);
        p.setActive(true);
        return p;
    }

    public static Position remove(long id, long timestamp, String book, String underlying, String contract) {
        Position p = new Position(id, timestamp);
        p.setActive(false);
        p.setBook(book);
        p.setUnderlying(underlying);
        p.setContract(contract);
        return p;
    }
    
    protected long positionId;
    protected long timestamp;
    
    protected String book = "";
    protected String underlying = "";
    protected String contract = "";

    protected double qty = 0d;
    protected boolean active = false;
    
    Position() {
        // for serialization
    }
    
    public Position(long positionId, long timestamp) {
        this.positionId = positionId;
        this.timestamp = timestamp;
    }

    public long getPositionId() {
        return positionId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public PositionKey getKey() {
        return new PositionKey(positionId, timestamp);
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
