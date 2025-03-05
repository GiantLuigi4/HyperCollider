package tfc.hypercollider.util.collection;

import java.util.List;

public class ReadOnlyListView<T> extends ReadOnlyList<T> {
    public ReadOnlyListView(T[] arr, int min, int max) {
        super(arr);
        this.len = max - min;
    }
}
