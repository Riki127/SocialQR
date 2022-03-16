package kth.socialqr.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Class for the user's profile.
 */

public class Profile implements Serializable {
    private String profileName;
    private String description;
    private List<SocialLink> socialLinks;
    private static Profile profile = null;

    private Profile() {
        socialLinks = new ArrayList<>();
    }

    public static Profile getInstance(){
        if(profile == null){
            profile = new Profile();
        }
        return profile;
    }

    public void resetProfile(){
        profile = new Profile();
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<SocialLink> socialLinks) {
        this.socialLinks = socialLinks;
    }

}
