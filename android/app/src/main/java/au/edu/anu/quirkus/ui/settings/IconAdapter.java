package au.edu.anu.quirkus.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import au.edu.anu.quirkus.R;

// A custom ArrayAdapter implementation that displays the icons in a ListView
public class IconAdapter extends ArrayAdapter<Integer> {
    // Constructor
    public IconAdapter(Context context, List<Integer> objects) {
        super(context, 0, objects);
    }


    // Method for setting up the view of each icon in the ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the icon layout (null implies it is not inflated)
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.icon_list_item, parent, false);
        }

        // Obtain the icon image and name views
        ImageView iconImage = convertView.findViewById(R.id.icon_image);
        TextView iconName = convertView.findViewById(R.id.icon_name);

        // Obtain the resource ID of the icon at it's current position
        int iconResId = getItem(position);
        // Set the icon image resource of the ImageView along with the text of the TextView
        iconImage.setImageResource(iconResId);
        iconName.setText(getContext().getResources().getResourceEntryName(iconResId));

        return convertView;
    }
}
