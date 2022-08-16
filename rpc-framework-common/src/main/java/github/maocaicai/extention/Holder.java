package github.maocaicai.extention;

public class Holder<T> {

    //volatile修饰
    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
