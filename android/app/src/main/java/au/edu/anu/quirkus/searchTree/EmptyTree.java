package au.edu.anu.quirkus.searchTree;

/**
 * @author Oanh Pham - u7281948
 * <p>
 * Abstract class for empty tree.
 * @param <T> Generic type
 */
public abstract class EmptyTree<T extends Comparable<T>> extends Tree<T> {
    public abstract Tree<T> insert(T element);

    @Override
    public T find(T element) {
        return null;
    }

    @Override
    public Tree<T> delete(T element) {
        return null;
    }

    @Override
    public int getHeight() {
        return -1;
    }

    @Override
    public String toString() {
        return "{}";
    }

}
