package carlosmada22.com.get_around;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MarkerListActivity extends AppCompatActivity {

    String nameLista;
    private DBAdapter mDBAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        Intent intent = getIntent();
        String name = getIntent().getExtras().getString("nameLista");

        MarkerListFragment fragment = (MarkerListFragment)
                getSupportFragmentManager().findFragmentById(R.id.markers_container);

        if (fragment == null) {
            fragment = MarkerListFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.markers_container, fragment)
                    .commit();
        }
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name);
        }

        mDBAdapter = new DBAdapter(getApplicationContext());
        mDBAdapter.open();

        /*Intent intent = getIntent();
        nameLista = getIntent().getExtras().getString("nameLista");
        actionBar.setTitle(nameLista);*/
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return true;

    }
}
