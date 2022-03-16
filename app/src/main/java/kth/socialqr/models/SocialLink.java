package kth.socialqr.models;

import java.io.Serializable;

/**
 * Class for identifying the social media used for the specific social media name.
 */

public class SocialLink implements Serializable {
    private SocialMediaEnums socialEnum;
    private String socialMediaName;

    public SocialLink(SocialMediaEnums socialEnum, String socialMediaName) {
        this.socialEnum = socialEnum;
        this.socialMediaName = socialMediaName;
    }

    public SocialMediaEnums getSocialEnum() {
        return socialEnum;
    }

    public void setSocialEnum(SocialMediaEnums socialEnum) {
        this.socialEnum = socialEnum;
    }

    public String getSocialMediaName() {
        return socialMediaName;
    }

    public void setSocialMediaName(String socialMediaName) {
        this.socialMediaName = socialMediaName;
    }
}
