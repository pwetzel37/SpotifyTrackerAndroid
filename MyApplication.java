package edu.bloomu.plw59761;

import android.app.Application;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

/**
 * Class that extends Application, allowing to store a 'global variable'
 * Used to store track list that is passed from main activity to tracker activity.
 * Also stores spotify app remote once connected
 *
 * @author Patrick Wetzel
 */
public class MyApplication extends Application {

    private ArrayList<Track> tracks = new ArrayList<>();
    private SpotifyAppRemote spotifyAppRemote;

    public ArrayList<Track> getTracks() { return tracks; }
    public void addTrack(Track t) { tracks.add(t); }
    public void resetTracks() { tracks = new ArrayList<>(); }

    public SpotifyAppRemote getAppRemote() { return spotifyAppRemote; }
    public void setAppRemote(SpotifyAppRemote sar) { spotifyAppRemote = sar; }

}