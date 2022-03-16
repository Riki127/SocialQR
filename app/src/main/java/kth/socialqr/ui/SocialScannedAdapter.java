package kth.socialqr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kth.socialqr.R;
import kth.socialqr.models.ScannedProfile;
import kth.socialqr.models.SocialLink;
import kth.socialqr.models.SocialMediaEnums;

public class SocialScannedAdapter extends RecyclerView.Adapter<SocialScannedAdapter.ViewHolder> {
    private ScannedProfile profile = ScannedProfile.getInstance();
    private SocialScannedAdapter.IOnItemSelectedCallBack mOnItemSelectedCallback;

    public SocialScannedAdapter(SocialScannedAdapter.IOnItemSelectedCallBack onItemSelectedCallBack) {
        mOnItemSelectedCallback = onItemSelectedCallBack;
    }

    public interface IOnItemSelectedCallBack {
        void onItemClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView socialName;
        ImageView socialIcon;
        private SocialScannedAdapter.IOnItemSelectedCallBack mOnItemSelectedCallback;

        public ViewHolder(@NonNull View itemView, SocialScannedAdapter.IOnItemSelectedCallBack onItemSelectedCallback) {
            super(itemView);
            itemView.setOnClickListener(this);
            mOnItemSelectedCallback = onItemSelectedCallback;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnItemSelectedCallback.onItemClicked(position);
        }
    }

    @NonNull
    @Override
    public SocialScannedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_item, parent, false);
        final SocialScannedAdapter.ViewHolder vh = new SocialScannedAdapter.ViewHolder(itemView, mOnItemSelectedCallback);
        vh.socialIcon = itemView.findViewById(R.id.social_icon);
        vh.socialName = itemView.findViewById(R.id.social_name);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SocialScannedAdapter.ViewHolder holder, int position) {
        SocialLink socialLink = profile.getSocialLinks().get(position);
        holder.socialName.setText(socialLink.getSocialMediaName());
        int id = getImageId(socialLink.getSocialEnum());
        holder.socialIcon.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return profile.getSocialLinks().size();
    }

    private int getImageId(SocialMediaEnums enums) {
        switch (enums) {
            case TWITTER:
                return R.drawable.twitter_blue;
            case TIKTOK:
                return R.drawable.tiktok_black;
            case INSTAGRAM:
                return R.drawable.instagram_color;
            case TWITCH:
                return R.drawable.twitch_purple;
            default:
                return R.drawable.twitter_blue;
        }
    }
}
