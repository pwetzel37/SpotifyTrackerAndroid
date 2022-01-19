package edu.bloomu.plw59761;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class that maintains the Tracker Activity, that contains a list of all saved songs.
 * Songs can be tapped to start playing, and saved song list reset button at the bottom.
 *
 * @author Patrick Wetzel
 */
public class TrackerActivity extends AppCompatActivity {

    LinearLayout songsList;
    SharedPreferences sharedPref;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        songsList =  findViewById(R.id.songsList);
        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        title = findViewById(R.id.listTitle);
        int numSaved = sharedPref.getInt("numSaved", 0);
        title.setText("SAVED TRACKS: " + numSaved);

        // for each track in sharedPref, create textview and add to linear layout
        for (int i = 1; i <= numSaved; i++) {
            // get strings for text and uri
            String tNum = "track" + i;
            String uriNum = "uri" + i;
            String song = sharedPref.getString(tNum, "");
            String uri = sharedPref.getString(uriNum, "");
            // set textview attributes
            TextView t = new TextView(this);
            t.setText(song);
            t.setTextSize(20);
            t.setGravity(1);
            t.setBackgroundResource(R.drawable.saved_track_bg);
            // set clickable
            t.setOnClickListener(v -> {
                ((MyApplication) this.getApplication()).getAppRemote().getPlayerApi().play(uri);
            });
            // add to linear layout
            songsList.addView(t);
        }
    }

    /**
     * Resets saved tracks list stored in MyApplication
     */
    public void resetList(View view) {
        ((MyApplication) this.getApplication()).resetTracks();
        songsList.removeAllViews();
        sharedPref.edit().clear().apply();
        title.setText("SAVED TRACKS: 0");
    }
}