package edu.bloomu.plw59761;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that displays textviews that tell how the application is used.
 *
 * @author Patrick Wetzel
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}