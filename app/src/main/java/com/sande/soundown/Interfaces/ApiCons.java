package com.sande.soundown.Interfaces;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public interface ApiCons{
    String BASE_URL="https://soundcloud.com/connect?";
    String CLIENT_ID_URI="client_id=";
    String CLIENT_ID="c9b0b6f01bb3987448c07c2744c5f117";
    String RESPONSE_TYPE="&response_type=";
    String RESPONSE_TOKEN="token";
    String DISPLAY_URI="&display=";
    String LINKED_PARTITION="&linked_partitioning=1";
    String DISPLAY="popup";
    String REDIRECT_URI="&redirect_uri=";
    String REDIRECT="http://redirectsoundown.com";
    String USER_DETAILS_ID="https://api.soundcloud.com/me?";
    String OAUTH_TOKEN_URI="oauth_token=";
    String USERS_PAGE="https://api.soundcloud.com/users/";
    String FAVORITES="/favorites?";
    String REDIRECTED_URL="http://redirectsoundown.com/?#access_token=";
    String SET_LIMIT="&limit=35";
    String PLAYLISTS="/playlists?";
    String PLAYLISTS_USER="https://api.soundcloud.com/playlists/";

    String NEXT_HREF="next_href";
    String COLLECTION="collection";
    int PAGINATION_LIMIT=35;
}
