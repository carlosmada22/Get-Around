package carlosmada22.com.get_around;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MapScreensActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screens);

        MapScreensFragment fragment = (MapScreensFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_screen_container);

        if (fragment == null) {
            fragment = MapScreensFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map_screen_container, fragment)
                    .commit();
        }

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Lista de mapas descargados");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return true;

    }
}
