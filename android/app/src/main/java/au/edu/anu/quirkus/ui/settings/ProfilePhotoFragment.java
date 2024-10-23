package au.edu.anu.quirkus.ui.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;
import java.util.stream.Collectors;

import au.edu.anu.quirkus.R;
import au.edu.anu.quirkus.profilePictures.ProfilePicture;
import au.edu.anu.quirkus.profilePictures.ProfilePictureFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilePhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePhotoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "user";
    private static final String ARG_PARAM2 = "param2";

    private static List<ProfilePicture> icons = ProfilePictureFactory.getAllProfilePictures();


    // TODO: Rename and change types of parameters
    private String user;
    private String mParam2;

    private ImageView profileImageView;

    SettingsModel settings;

    public ProfilePhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilePhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilePhotoFragment newInstance(String param1, String param2) {
        ProfilePhotoFragment fragment = new ProfilePhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        settings = new ViewModelProvider(getActivity()).get(SettingsModel.class);
    }

    // Method for inflating the fragment's layout and setting up its views
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and assign it to the view variable
        View view = inflater.inflate(R.layout.fragment_profile_photo, container, false);

        // Get the profile picture's ImageView and Button from the layout
        profileImageView = view.findViewById(R.id.imageView4);
        Button photoButton = view.findViewById(R.id.photo_button);
        // Set up the change profile button's click listener
        photoButton.setOnClickListener(new View.OnClickListener() {
            // Notify the icon picker dialog if the button is clicked
            @Override
            public void onClick(View v) {
                showIconPickerDialog();
            }
        });

        // Set up the ImageView's image resource based on the current user's profile picture icon
        settings.getActiveUser().observe(getViewLifecycleOwner(), u -> {
            profileImageView.setImageResource(u.getProfilePicture().getDrawable());
        });

        return view;
    }

    // Method for showing the icon picker dialog
    public void showIconPickerDialog() {
        // Create a new dialog builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an icon");

        // Obtain the drawable resource IDs of the profile image icons
        List<Integer> iconResources = icons.stream().map(pic -> pic.getDrawable()).collect(Collectors.toList());;
        // Create a new IconAdapter with the profile image icon resources
        IconAdapter adapter = new IconAdapter(requireContext(), iconResources);

        // Set up the adapter of the dialog and its corresponding click listener
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Set the current user's profile picture to the profile picture icon that was chosen and clicked
                settings.setUserProfilePicture(icons.get(which));
            }
        });

        // Create the dialog and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}