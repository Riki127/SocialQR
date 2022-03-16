package kth.socialqr.utils;

import kth.socialqr.models.SocialMediaEnums;

/**
 * Utility class for linking correct URL to the specified social media
 */

public class PlatformURL {
    public static String TWITTER_LINK = "https://twitter.com/";
    public static String TWITCH_LINK = "https://www.twitch.tv/";
    public static String TIKTOK_LINK = "https://www.tiktok.com/@";
    public static String INSTAGRAM_LINK = "https://www.instagram.com/";

    public static String PlatformString(SocialMediaEnums e){
        switch (e){
            case TWITCH: return TWITCH_LINK;
            case TWITTER: return  TWITTER_LINK;
            case TIKTOK: return TIKTOK_LINK;
            case INSTAGRAM: return  INSTAGRAM_LINK;
        }
        return null;
    }
}
