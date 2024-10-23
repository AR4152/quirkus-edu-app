package au.edu.anu.quirkus.searchTree;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import au.edu.anu.quirkus.UserModelFactory;
import au.edu.anu.quirkus.data.User;
import au.edu.anu.quirkus.profilePictures.Penguin;
import au.edu.anu.quirkus.profilePictures.ProfilePicture;
import au.edu.anu.quirkus.profilePictures.ProfilePictureFactory;
import au.edu.anu.quirkus.ui.forum.options.Enrollments;
import au.edu.anu.quirkus.data.Enrollment;

public class EnrolmentSearchTreeTest {
   User u1 = new User("1", "Dan", null, ProfilePictureFactory.createProfilePicture("Avatar"));
   User u2 = new User("2", "Daniel", null, ProfilePictureFactory.createProfilePicture("Koala"));
   User u3 = new User("3", "Mani", null, ProfilePictureFactory.createProfilePicture("Anonymous"));
   User u4 = new User("4", "Arjun", null, ProfilePictureFactory.createProfilePicture("Rhino"));
   User u5 = new User("5", "Daniel", null, ProfilePictureFactory.createProfilePicture("Anonymous"));

   @Test
   public void insertUserTest() {
      EnrolmentSearchTree tree = new EnrolmentSearchTree();
      // check root value after inserting user to an empty tree
      tree = tree.insert(u1);
      assertEquals("User " + u4.getName() + " was not properly inserted", u1.getName(), tree.value.getName());

      // check left insertion of user
      tree = tree.insert(u2).insert(u3);
      assertEquals("User " + u4.getName() + " was not properly inserted", u2.getName(), tree.value.getName());

      // check right insertion of user
      tree = tree.insert(u4);
      assertEquals("User " + u4.getName() + " was not properly inserted", u4.getName(), tree.leftNode.leftNode.value.getName());

      // check inserting a user that shares the same name as a node/leaf within the tree
      tree = tree.insert(u5);
      assertEquals("User " + u5.getName() + " was not properly inserted", u5.getName(), tree.rightNode.leftNode.value.getName());
   }

   @Test
   public void insertListOfUsersTest() {
      List<User> users = new ArrayList<>();
      users.add(u1);
      users.add(u2);
      users.add(u3);
      users.add(u4);
      users.add(u5);

      EnrolmentSearchTree tree = new EnrolmentSearchTree();
      tree = tree.insert(tree, users);

      // check root
      assertEquals("Users were not properly inserted", u2.getName(), tree.value.getName());
      // check left node
      assertEquals("Users were not properly inserted", u1.getName(), tree.leftNode.value.getName());
      // check left-left node
      assertEquals("Users were not properly inserted", u4.getName(), tree.leftNode.leftNode.value.getName());
      // check right node
      assertEquals("Users were not properly inserted", u3.getName(), tree.rightNode.value.getName());
      // check right-left node
      assertEquals("Users were not properly inserted", u5.getName(), tree.rightNode.leftNode.value.getName());
   }

   @Test
   public void searchTest() {
      EnrolmentSearchTree tree = new EnrolmentSearchTree();

      List<User> users = new ArrayList<>();
      users.add(u1);
      users.add(u2);
      users.add(u3);
      users.add(u4);
      users.add(u5);

      // search when tree is empty
      List<User> usersFound = tree.search2("Dan");
      assertEquals(0, usersFound.size());

      // search when tree is not empty
      tree = tree.insert(tree, users);
      usersFound = tree.search2("Dan");
      assertEquals(3, usersFound.size());

      // check whether users found start with "Dan"
      List<User> expected = new ArrayList<>();
      expected.add(u1);
      expected.add(u2);
      expected.add(u5);

      Collections.sort(expected);
      Collections.sort(usersFound);
      assertEquals(expected, usersFound);
   }
}
