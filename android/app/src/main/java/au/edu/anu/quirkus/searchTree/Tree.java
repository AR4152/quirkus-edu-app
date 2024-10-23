package au.edu.anu.quirkus.searchTree;

/**
 * @author Oanh Pham (u7281948)
 * <p>
 * Generic tree class
 */
public abstract class Tree<T extends Comparable<T>> {

    public T value;       // element stored in this node of the tree.
    public Tree<T> leftNode;    // less than the node.
    public Tree<T> rightNode;   // greater than the node.

    /**
     * Constructor to allow for empty trees
     */
    public Tree() {
        value = null;
    }

    /**
     * Constructor for creating a new child node.
     *
     * @param value to set as this node's value.
     */
    public Tree(T value) {
        // Ensure input is not null.
        if (value == null)
            throw new IllegalArgumentException("Input cannot be null");

        this.value = value;
    }

    /**
     * Constructor for creating a new node.
     *
     * @param value     to set as this node's value.
     * @param leftNode  left child of current node.
     * @param rightNode right child of current node.
     */
    public Tree(T value, Tree<T> leftNode, Tree<T> rightNode) {
        // Ensure inputs are not null.
        if (value == null || leftNode == null || rightNode == null)
            throw new IllegalArgumentException("Inputs cannot be null");

        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public abstract T find(T element);     // Finds the element and returns the node.

    public abstract Tree<T> insert(T element);   // Inserts the element and returns a new instance of itself with the new node.

    public abstract Tree<T> delete(T element);   // Deletes the element and returns a new instance of itself with the new node.

    public int getHeight() {
        int leftNodeHeight = leftNode instanceof EmptyTree ? 0 : 1 + leftNode.getHeight();
        int rightNodeHeight = rightNode instanceof EmptyTree ? 0 : 1 + rightNode.getHeight();
        return Math.max(leftNodeHeight, rightNodeHeight);
    }

    @Override
    public String toString() {
        if (value == null) {
            return "{}";
        }
        else {
            return "{" +
                    "value=" + value +
                    ", leftNode=" + leftNode +
                    ", rightNode=" + rightNode +
                    '}';
        }
    }
}