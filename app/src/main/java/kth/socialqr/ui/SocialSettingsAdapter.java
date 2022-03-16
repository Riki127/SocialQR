package kth.socialqr.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kth.socialqr.R;
import kth.socialqr.models.Profile;
import kth.socialqr.models.SocialLink;
import kth.socialqr.models.SocialMediaEnums;

/**
 * Adapter for recyclerview in Settings activity
 */

public class SocialSettingsAdapter extends RecyclerView.Adapter<SocialSettingsAdapter.ViewHolder> {

    private Profile profile = Profile.getInstance();

    public SocialSettingsAdapter() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView socialName;
        ImageView iconView;
        ImageView removeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.remove_imageview).setOnClickListener(v -> {
                profile.getSocialLinks().remove(getLayoutPosition());
                notifyDataSetChanged();
            });
        }
    }

    @NonNull
    @Override
    public SocialSettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View customView = inflater.inflate(R.layout.social_item_settings, parent, false);

        ViewHolder viewHolder = new ViewHolder(customView);

        viewHolder.iconView = customView.findViewById(R.id.social_icon_settings);
        viewHolder.socialName = customView.findViewById(R.id.social_name_settings);
        viewHolder.removeIcon = customView.findViewById(R.id.remove_imageview);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SocialSettingsAdapter.ViewHolder viewHolder, int position) {

        SocialLink socialLink = profile.getSocialLinks().get(position);
        viewHolder.socialName.setText(socialLink.getSocialMediaName());

        int id = getImageId(socialLink.getSocialEnum());
        viewHolder.iconView.setImageResource(id);

        viewHolder.removeIcon.setImageResource(R.drawable.remove_icon);
    }

    private int getImageId(SocialMediaEnums enums){
        switch (enums){
            case TWITTER: return R.drawable.twitter_blue;
            case TIKTOK: return R.drawable.tiktok_black;
            case INSTAGRAM: return R.drawable.instagram_color;
            case TWITCH: return R.drawable.twitch_purple;
            default: return R.drawable.twitter_blue;
        }
    }

    @Override
    public int getItemCount() {
        return profile.getSocialLinks().size();
    }
}
