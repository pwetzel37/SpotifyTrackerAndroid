package edu.bloomu.plw59761;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

/**
 * Main activity that displays currently playing song, with player buttons.
 * Also displays sample categories with buttons, and 2 buttons that switch activities.
 *
 * @author Patrick Wetzel
 */
public class MainActivity extends AppCompatActivity {

    // Spotify credentials
    private static final String CLIENT_ID = "15bfbacbbe8840aaba25fd2319690bbf";
    private static final String REDIRECT_URI = "edu.bloomu.plw59761://callback";

    // Spotify App Remote variables
    private SpotifyAppRemote spotifyAppRemote;
    private PlayerApi playerApi;
    // variables for different views
    private TextView shuffleButton;
    private TextView togglePlayer;
    private TextView skipButton;
    private TextView songText;
    private TextView timeText;
    // variable for storing current playlist (maybe not needed)
    private String currentPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views variables
        shuffleButton = findViewById(R.id.shuffleButton);
        togglePlayer = findViewById(R.id.togglePlayer);
        skipButton = findViewById(R.id.skipButton);
        songText = findViewById(R.id.songText);
        timeText = findViewById(R.id.timeText);
    }

    /**
     * Event handler for about button
     */
    public void startAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Event handler for tracker button
     */
    public void startSavedPage(View view) {
        Intent intent = new Intent(this, TrackerActivity.class);
        startActivity(intent);
    }

    /**
     * Starts Spotify application, authenticates and connects with SDK
     *
     * Learned how to set up authentication with Spotify Android SDK Quick Start guide:
     * https://developer.spotify.com/documentation/android/quick-start/
     */
    @Override
    protected void onStart() {
        super.onStart();

        // create connection parameters object for authentication
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI).showAuthView(true).build();

        // connect to Spotify app remote for listening to Spotify for data
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote sar) {
                        // set variables for using Spotify tools
                        spotifyAppRemote = sar;
                        playerApi = spotifyAppRemote.getPlayerApi();
                        // set globals for other views to use remote and api
                        ((MyApplication) getApplication()).setAppRemote(spotifyAppRemote);
                        // start using Spotify App Remote
                        connected();
                    }
                    @Override
                    public void onFailure(Throwable throwable) { }
        });

    }

    /**
     * Used when Spotify app disconnects or stops
     */
    @Override
    protected void onStop() { super.onStop(); }

    /**
     * Method that is called when Spotify App Remote connects successfully,
     * basically just sets up main activity page with player buttons and text
     */
    private void connected() {
        // set toggle and shuffle buttons
        togglePlayer.setText("PAUSE");
        playerApi.setShuffle(true);
        shuffleButton.setTextColor(Color.parseColor("#00FF00"));
        // use API to get current state of player and make display
        playerApi.subscribeToPlayerState().setEventCallback(playerState -> {
            // get current track
            final Track track = playerState.track;
            if (track != null) {
                // set song text
                songText.setText("\"" + track.name + "\" by " + track.artist.name);
                // set current time and length of track
                int totalMinutes = (int) track.duration / 1000 / 60;
                float totalSeconds = (float) (track.duration / 1000) - (totalMinutes * 60);
                String duration = String.format("%d:%02.0f", totalMinutes, totalSeconds);
                int currentMinutes = (int) playerState.playbackPosition / 1000 / 60;
                float currentSeconds = (float) (playerState.playbackPosition / 1000) - (currentMinutes * 60);
                String currentTime = String.format("%d:%02.0f", currentMinutes, currentSeconds);
                timeText.setText(currentTime + " / " + duration);
            }
        });
    }

    /**
     * Toggles the play/pause button when textview is clicked
     */
    public void togglePlayer(View view) {
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.isPaused) {
                playerApi.resume();
                togglePlayer.setText("PAUSE");
            }
            else {
                playerApi.pause();
                togglePlayer.setText("PLAY");
            }
        });
    }

    /**
     * Skips to next song in current playlist
     */
    public void skipSong(View view) { playerApi.skipNext(); togglePlayer.setText("PAUSE"); }

    /**
     * Toggles the shuffle button when textview is clicked
     */
    public void shuffle(View view) {
        playerApi.getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackOptions.isShuffling) {
                playerApi.setShuffle(false);
                shuffleButton.setTextColor(Color.parseColor("#FF0000"));
            }
            else {
                playerApi.setShuffle(true);
                shuffleButton.setTextColor(Color.parseColor("#00FF00"));
            }
        });
    }

    /**
     * Changes playlist from the sample playlists buttons
     */
    public void changePlaylist(View view) {
        String playlist = (String) view.getTag();
        playerApi.play(playlist);
        togglePlayer.setText("PAUSE");
    }

    /**
     * Adds current playing song to the tracking list, displayed in Tracker activity
     */
    public void saveSong(View view) {
        // add current track to 'global' arraylist, and store strings in shared prefs
        playerApi.getPlayerState().setResultCallback(playerState -> {
            Track currentTrack = playerState.track;
            ArrayList<Track> savedTracks = ((MyApplication) this.getApplication()).getTracks();
            if (currentTrack != null && !savedTracks.contains(currentTrack)) {
                ((MyApplication) this.getApplication()).addTrack(currentTrack);
                addToShared(currentTrack);
            }
        });
    }

    /**
     * Adds given (current) track to shared preferences to be displayed in saved songs
     */
    public void addToShared(Track t) {
        // used for creating textview button in saved songs page
        String tString = "\"" + t.name + "\" by " + t.artist.name;
        String uri = t.uri;
        // create shared preferences
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // update # of saved songs
        int numSaved = sharedPref.getInt("numSaved", 0);
        numSaved++;
        editor.putInt("numSaved", numSaved);
        // save track strings
        String tNum = "track" + numSaved;
        String uriNum = "uri" + numSaved;
        editor.putString(tNum, tString);
        editor.putString(uriNum, uri);
        editor.apply();
    }
}