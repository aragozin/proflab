package info.ragozin.proflab;

public class ContentionResource<T> {

    private final String name;
    private final T lock;
    
    public ContentionResource(String name, T lock) {
        this.name = name;
        this.lock = lock;
    }
    
    public T lock() {
        return lock;
    }
    
    @Override
    public String toString() {
        return name;
    }    
}
