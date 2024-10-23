package au.edu.anu.quirkus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.data.Post;
import au.edu.anu.quirkus.data.PostRoot;

public class AuthorPostModelFactory {
    // Left joins a list of Posts with a list of UserModels by the UserID
    public static List<AuthorPostModel> createPostModels(List<Post> posts, List<UserModel> user, boolean isStaff) {
        List<UserModel> userList;
        if (posts == null) return new ArrayList<>();
        if (user == null) userList = new ArrayList<>();
        else userList = user;

        return posts.stream().map(p -> {
            // Staff can see anonymous names
            if (p.isAnonymousPost() && !isStaff) {
                return new AuthorPostModel(p, UserModelFactory.createAnonymousUserModel());
            }

            Optional<UserModel> actualUser = userList.stream().filter(u -> u.getUser().getId().equals(p.getAuthorId())).findFirst();

            // Create default with user loading
            UserModel um;
            if (actualUser.isPresent()) {
                um = actualUser.get();
            } else {
                um = UserModelFactory.createLoadingUserModel(p.getAuthorId());
            }

            return new AuthorPostModel(p, um);
        }).collect(Collectors.toList());
    }
}
