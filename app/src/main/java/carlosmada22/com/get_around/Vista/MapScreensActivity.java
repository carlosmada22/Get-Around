package carlosmada22.com.get_around.Vista;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import carlosmada22.com.get_around.R;

public class MapScreensActivity extends AppCompatActivity {

    private Menu menu;

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
            actionBar.setTitle("Mapas descargados");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tb_maps, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        if (id==R.id.alpha){
            MapScreensFragment.sortAlph(this);
            MapScreensFragment.setOrder(1);

        }
        if (id==R.id.creation){
            MapScreensFragment.sortDate(this);
            MapScreensFragment.setOrder(2);

        }
        if (id==R.id.prox){
            MapScreensFragment.sortProx(this);
            MapScreensFragment.setOrder(3);

        }
        return true;

    }
}
