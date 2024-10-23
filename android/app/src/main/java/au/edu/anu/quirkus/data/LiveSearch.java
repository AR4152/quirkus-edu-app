package au.edu.anu.quirkus.data;

import java.util.concurrent.Callable;
import java.util.List;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.searchTree.EnrolmentSearchTree;

class LiveSearch implements Callable<List<User>> {

    EnrolmentSearchTree tree;
    String name;

    public LiveSearch(EnrolmentSearchTree tree, String name) {
        this.tree = tree;
        this.name = name;
    }

    @Override
    public List<User> call() {
        return tree.search2(name);
    }
}
