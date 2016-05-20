package com.sande.soundown.Interfaces;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public interface ApiCons{
    String BASE_URL="https://soundcloud.com/connect?";
    String CLIENT_ID_URI="client_id=";
    String CLIENT_ID="262b166a6ebe9e3fc269f23204bc602b";
    String RESPONSE_TYPE="&response_type=";
    String RESPONSE_TOKEN="token";
    String DISPLAY_URI="&display=";
    String LINKED_PARTITION="&linked_partitioning=1";
    String DISPLAY="popup";
    String REDIRECT_URI="&redirect_uri=";
    String REDIRECT="sociallogin://redirect";
    String USER_DETAILS_ID="https://api.soundcloud.com/me?";
    String OAUTH_TOKEN_URI="oauth_token=";
    String USERS_PAGE="https://api.soundcloud.com/users/";
    String FAVORITES="/favorites?";
    String REDIRECTED_URL="http://redirectsoundown.com/?#access_token=";
    String SET_LIMIT="&limit=35";
    String PLAYLISTS="/playlists?";
    String PLAYLISTS_USER="https://api.soundcloud.com/playlists/";
    String TYPE_TRACK="&type=track";
    String GET_FEED="https://api.soundcloud.com/me/activities?type=track";
    String PLAYLISTS_TRACKS="/tracks?";

    String AVATAR="avatar_url";
    String USERNAME="username";
    String FAV_COUNT="public_favorites_count";
    String PLAYLISTCNT="playlist_count";
    String FOLLOWERCNT="followers_count";
    String FOLLOWINGCNT="followings_count";

    String NEXT_HREF="next_href";
    String COLLECTION="collection";
    int PAGINATION_LIMIT=35;
}
