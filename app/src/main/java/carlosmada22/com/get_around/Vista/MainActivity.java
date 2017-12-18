package carlosmada22.com.get_around.Vista;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import java.io.File;

import java.util.ArrayList;

import java.util.List;

import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.R;

/**
 * Created by carlosmada22 on 2/10/17.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static Menu menu;
    private int[] tabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_list,
    };
    DBAdapter mDBAdapter;

    List<String> nombres = new ArrayList<String>();
    List<Double> latitudes = new ArrayList<Double>();
    List<Double> longitudes = new ArrayList<Double>();
    List<String> descripciones = new ArrayList<String>();
    List<Integer> categorias = new ArrayList<Integer>();
    List<Integer> ids = new ArrayList<Integer>();
    public static boolean mapType = false;
    public static boolean eye = true;

    public static boolean mostrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setElevation(0);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_icon_inside);
        actionBar.setTitle("GetAround");


        mDBAdapter = new DBAdapter((this));
        mDBAdapter.open();


        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcons();

        mostrar = true;


    }

    private void setUpTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MapFragment(), "");
        adapter.addFrag(new ListFragment(), "");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tb, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int v=0;

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        //noinspection SimplifiableIfStatement

        if(id==R.id.type){
            if(MapFragment.gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
            {
                MapFragment.gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mapType = true;
            }
            else {
                MapFragment.gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mapType = false;
            }
            Toast.makeText(context,
                    "Tipo de mapa cambiado",
                    Toast.LENGTH_SHORT).show();
        }
        Log.i("mapType", ""+mapType);

        if(id==R.id.list){

            File file = new File("/storage/emulated/0/Maps/");
            if(file.isDirectory()&&file.listFiles().length!=0){
                Log.i("File", "" + getFilesDir().toString());
                Intent intent = new Intent(this, MapScreensActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(context,
                        "Aún no has guardado ningún mapa. Hazlo pulsando sobre él y después en el botón naranja de descarga.",
                        Toast.LENGTH_LONG).show();
                mDBAdapter.clearMapas();
            }


        }

        else if(id==R.id.show) {
            if(mostrar) {
                menu.findItem(R.id.show).setIcon(R.drawable.ic_action_hide);
                eye = false;
                Log.i("mostrar", ""+mostrar);
                MapFragment.gMap.clear();
                MapFragment.positionMarkers.clear();
                mostrar = false;
            }
            else mostrar=true;
        }
        if (mostrar) {

            ids = new ArrayList<Integer>();
            nombres = new ArrayList<String>();
            latitudes = new ArrayList<Double>();
            longitudes = new ArrayList<Double>();
            descripciones = new ArrayList<String>();
            categorias = new ArrayList<Integer>();

            Cursor c = null;
            c = mDBAdapter.getMarkerLista();

            if (c.moveToFirst()) {
                do {
                    ids.add(c.getInt(0));
                    Log.i("id", "" + c.getInt(0));

                    nombres.add(c.getString(1));
                    Log.i("nombre", c.getString(1));

                    latitudes.add(c.getDouble(2));
                    Log.i("latitudes", "" + c.getDouble(2));

                    longitudes.add(c.getDouble(3));
                    Log.i("longitudes", "" + c.getDouble(3));

                    descripciones.add(c.getString(4));
                    Log.i("descripciones", c.getString(4));

                    categorias.add(c.getInt(5));
                    Log.i("categorias", "" + c.getInt(5));
                } while (c.moveToNext());

            }
            LatLng location;
            double latitude, longitude;
            String name, desc;
            BitmapDescriptor colour;
            for (int i = 0; i < ids.size(); i++) {
                latitude = latitudes.get(i);
                longitude = longitudes.get(i);
                location = new LatLng(latitude, longitude);
                name = nombres.get(i);
                desc = descripciones.get(i);
                colour = MapFragment.markerColor(categorias.get(i));
                MapFragment.positionMarkers.add(location);
                MapFragment.gMap.addMarker(new MarkerOptions().position(location).title(name).snippet(desc).icon(colour));
            }
            menu.findItem(R.id.show).setIcon(R.drawable.ic_action_show_all);
            eye = true;
            mostrar = true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("onResumeMain", "onResumeMain");
        super.onResume();
        Log.i("mapType", ""+mapType);

    }


}
