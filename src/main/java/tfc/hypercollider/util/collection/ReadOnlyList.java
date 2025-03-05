package tfc.hypercollider.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ReadOnlyList<T> implements List<T> {
    T[] arr;
    int len;

    public ReadOnlyList(T[] arr) {
        this.arr = arr;
        this.len = arr.length;
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public boolean isEmpty() {
        return len == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (T t : arr) {
            if (o.equals(arr))
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(arr, len);
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        if (a.length < len) a = Arrays.copyOf(a, len);
        ;
        System.arraycopy(arr, 0, a, 0, arr.length);
        return a;
    }

    @Override
    public boolean add(T t) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public T get(int index) {
        return arr[index];
    }

    @Override
    public T set(int index, T element) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void add(int index, T element) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public T remove(int index) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < arr.length; i++) {
            if (o.equals(arr[i]))
                return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = 0; i < arr.length; i++) {
            if (o.equals(arr[i]))
                return i;
        }
        return -1;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListIter(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        // TODO: list view
        throw new RuntimeException("TODO");
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        for (int i = 0; i < arr.length; i++) {
            action.accept(arr[i]);
        }
    }

    class ListIter implements ListIterator<T> {
        int index;

        public ListIter(int index) {
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < len;
        }

        @Override
        public T next() {
            return arr[index++];
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public T previous() {
            return arr[--index];
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            throw new RuntimeException("Unsupported");
        }

        @Override
        public void set(T t) {
            throw new RuntimeException("Unsupported");
        }

        @Override
        public void add(T t) {
            throw new RuntimeException("Unsupported");
        }
    }
}
