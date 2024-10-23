package au.edu.anu.quirkus.searchTree;

import java.util.ArrayList;
import java.util.List;
import au.edu.anu.quirkus.data.User;

/**
 * @author Arjun Raj (u7526852) and Oanh Pham (u7281948)
 * <p>
 * AVL for storing Users enrolled in course and for
 * searching users
 */

public class EnrolmentSearchTree extends Tree<User> {
    public EnrolmentSearchTree() {
        super();
        this.leftNode = new EmptyEnrolmentTree();
        this.rightNode = new EmptyEnrolmentTree();
    }

    public EnrolmentSearchTree(User value) {
        super(value);
        this.leftNode = new EmptyEnrolmentTree();
        this.rightNode = new EmptyEnrolmentTree();
    }

    public EnrolmentSearchTree(User value, Tree<User> leftNode, Tree<User> rightNode) {
        super(value, leftNode, rightNode);
    }

    public int getBalanceFactor() {
        return leftNode.getHeight() - rightNode.getHeight();
    }

    /**
     * Search and return a list of Users whose name start with @param name
     * Implementation 1
     *
     * @param name: start string of user's name
     * @return: list of Users
     */
    public List<User> search(String name){
        return searchHelper(this, name, new ArrayList<>());
    }

    /**
     * Helper method for Search Implementation 1
     *
     * @param currNode: current search node in tree
     * @param name: string to check
     * @param userList: user with string name match
     * @return: list of Users
     */
    public List<User> searchHelper(Tree currNode, String name, List<User> userList){

        if (currNode.value == null)
            return userList;

        if (((User)currNode.value).getName().toLowerCase().startsWith(name.toLowerCase()))
            userList.add((User) currNode.value);

        userList.addAll(searchHelper(currNode.leftNode, name, new ArrayList<>()));
        userList.addAll(searchHelper(currNode.rightNode, name, new ArrayList<>()));

        return userList;
    }

    /**
     * Search and return a list of Users whose name start with @param name
     * Implementation 2
     *
     * @param name: start string of user's name
     * @return: list of Users
     */
    public List<User> search2(String name){
        return searchHelper2(this, name, new ArrayList<>());
    }

    /**
     * Helper method for Search Implementation 2
     *
     * @param currNode: current search node in tree
     * @param name: string to check
     * @param userList: user with string name match
     * @return: list of Users
     */
    public List<User> searchHelper2(Tree currNode, String name, List<User> userList){

        if (currNode.value == null)
            return userList;

        if (((User)currNode.value).getName().toLowerCase().startsWith(name.toLowerCase()))
            userList.add((User) currNode.value);

        if (((User)currNode.leftNode.value) != null)
            if (name.toLowerCase().compareTo(((User)currNode.leftNode.value).getName().toLowerCase()) <= 0)
                userList.addAll(searchHelper(currNode.leftNode, name, new ArrayList<>()));

        if (((User)currNode.rightNode.value) != null)
                if(name.toLowerCase().compareTo(((User)currNode.rightNode.value).getName().toLowerCase()) <= 0)
                    userList.addAll(searchHelper(currNode.rightNode, name, new ArrayList<>()));

        return userList;
    }

    /**
     * Sample text representation of tree
     *
     * @return String representation of tree
     */
    @Override
    public String toString() {
        return "{" + value.getName() + " - " + value.getId() + "}\n" + leftNode.toString() + "\n" + rightNode.toString();
    }

    @Override
    public EnrolmentSearchTree insert(User element) {

        if (value == null) {
            return new EnrolmentSearchTree(element);
        }

        EnrolmentSearchTree tree = new EnrolmentSearchTree(this.value, this.leftNode, this.rightNode);

        // Ensure input is not null.
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        if (element.compareTo(value) < 0) {
            tree.leftNode = tree.leftNode.insert(element);
        }
        if (element.compareTo(value) > 0) {
            tree.rightNode = tree.rightNode.insert(element);
        }
        else {
            return tree;
        }

        if (tree.getBalanceFactor() > 1) {
            if (((EnrolmentSearchTree) tree.leftNode).getBalanceFactor() < 0) {
                tree.leftNode = ((EnrolmentSearchTree) tree.leftNode).leftRotate();
            }
            tree = tree.rightRotate();
        }
        else if (tree.getBalanceFactor() < -1) {
            if (((EnrolmentSearchTree) tree.rightNode).getBalanceFactor() > 0) {
                tree.rightNode = ((EnrolmentSearchTree) tree.rightNode).rightRotate();
            }
            tree = tree.leftRotate();
        }

        return tree;
    }

    @Override
    public User find(User element) {
        if (value == null || element.compareTo(value) == 0) {
            return value;
        }
        else if (element.compareTo(value) < 0) {
            return leftNode.find(element);
        }
        else {
            return rightNode.find(element);
        }
    }

    public static EnrolmentSearchTree insert(EnrolmentSearchTree tree, List<User> elements) {
        for (User i : elements) {
            tree = tree.insert(i);
        }

        return tree;
    }

    @Override
    public Tree<User> delete(User element) {
        if (this.value == null) {
            return this;
        }

        EnrolmentSearchTree tree = new EnrolmentSearchTree(this.value, this.leftNode, this.rightNode);

        // Ensure input is not null.
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        if (element.compareTo(value) == 0) {
            if (tree.leftNode.value == null && tree.rightNode.value == null) {
                tree = new EnrolmentSearchTree();
            }
            else if (tree.leftNode.value == null) {
                tree.value = tree.rightNode.value;
                tree.leftNode = tree.rightNode.leftNode;
                tree.rightNode = tree.rightNode.rightNode;
            }
            else if (tree.rightNode.value == null) {
                tree.value = tree.leftNode.value;
                tree.leftNode = tree.leftNode.leftNode;
                tree.rightNode = tree.leftNode.rightNode;
            }
            else {
                tree.value = tree.leftNode.value;
                tree.leftNode = tree.leftNode.leftNode;
            }
        }
        else if (element.compareTo(value) < 0) {
            tree.leftNode = tree.leftNode.delete(element);
        }
        else {
            tree.rightNode = tree.rightNode.delete(element);
        }

        if (tree.getBalanceFactor() > 1) {
            if (((EnrolmentSearchTree) tree.leftNode).getBalanceFactor() < 0) {
                tree.leftNode = ((EnrolmentSearchTree) tree.leftNode).leftRotate();
            }
            tree = tree.rightRotate();
        }
        else if (tree.getBalanceFactor() < -1) {
            if (((EnrolmentSearchTree) tree.rightNode).getBalanceFactor() > 0) {
                tree.rightNode = ((EnrolmentSearchTree) tree.rightNode).rightRotate();
            }
            tree = tree.leftRotate();
        }

        return tree;
    }

    public EnrolmentSearchTree leftRotate() {

        Tree<User> newParent = this.rightNode;
        Tree<User> newRightOfCurrent = newParent.leftNode;

        newParent.leftNode = this;
        this.rightNode = newRightOfCurrent;

        return (EnrolmentSearchTree) newParent;
    }

    /**
     * Conducts a right rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public EnrolmentSearchTree rightRotate() {

        Tree<User> newParent = this.leftNode;
        Tree<User> newLeftOfCurrent = newParent.rightNode;

        newParent.rightNode = this;
        this.leftNode = newLeftOfCurrent;

        return (EnrolmentSearchTree) newParent;
    }

    public static class EmptyEnrolmentTree extends EmptyTree<User> {
        @Override
        public Tree<User> insert(User element) {
            return new EnrolmentSearchTree(element);
        }

        @Override
        public Tree<User> delete(User element) {
            return new EnrolmentSearchTree();
        }
    }
}
