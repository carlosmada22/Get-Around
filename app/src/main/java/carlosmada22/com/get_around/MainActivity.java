package carlosmada22.com.get_around;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Menu menu;
    public static ArrayList<Ciudades> cities;
    private int[] tabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_list,
    };
    public static final String EXTRA_LIST_ID = "extra_list_id";
    DBAdapter mDBAdapter;

    List<String> nombres = new ArrayList<String>();
    List<Double> latitudes = new ArrayList<Double>();
    List<Double> longitudes = new ArrayList<Double>();
    List<String> descripciones = new ArrayList<String>();
    List<Integer> categorias = new ArrayList<Integer>();
    List<Integer> ids = new ArrayList<Integer>();

    boolean mostrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setElevation(0);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_icon_inside);
        actionBar.setTitle("GetAround");

        cities = new ArrayList<>();





        /*
        TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
        tabs.setup(mlam);

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(new Intent(this, MapFragment.class));
        spec.setIndicator("",
                res.getDrawable(R.drawable.ic_action_list));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",
                res.getDrawable(R.drawable.ic_action_map));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
        */

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setUpTabIcons();

        mostrar = true;


        /*
        JSONArray array = new JSONArray();
        try {
            for(int i =0 ; i<array.length(); i++) {
                JSONObject object = array.getJSONObject(1);
                cities.add(new Ciudades(object.getDouble("lat"),object.getDouble("lon"),
                        object.getString("wikipedia"),object.getString("city")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        try {
            JSONObject cit = new JSONObject(loadJSONFromAsset());
            Iterator x = cit.keys();
            JSONArray jsonArray = new JSONArray();
            while (x.hasNext()){
                String key = (String) x.next();
                jsonArray.put(cit.get(key));
            }
            try {
                for(int i =0 ; i<jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    cities.add(new Ciudades(object.getDouble("lat"),object.getDouble("lon"),
                            object.getString("wikipedia"),object.getString("city")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setUpTabIcons() {
        //tabLayout.getTabAt(0).setText("MAPA");
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        //tabLayout.getTabAt(1).setText("LISTAS");
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
        /*searchMenuItem = menu.findItem(R.id.buscar);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        //searchView = (SearchView) searchMenuItem.getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //ListFragment.listCursorAdapter.getFilter().filter(newText);

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
            }
            else
                MapFragment.gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(context,
                    "Tipo de mapa cambiado",
                    Toast.LENGTH_SHORT).show();
        }

        if(id==R.id.list){

            File file = new File("/storage/emulated/0/Maps/");
            if(file.isDirectory()&&file.listFiles().length!=0){
                Log.i("File", "" + getFilesDir().toString());
                Intent intent = new Intent(this, MapScreensActivity.class);
                startActivity(intent);
            }
            else Toast.makeText(context,
                    "Aún no has guardado ningún mapa. Hazlo pulsando sobre él y después en el botón naranja de descarga.",
                    Toast.LENGTH_LONG).show();

        }

        else if(id==R.id.show) {
            if(mostrar) {
                menu.findItem(R.id.show).setIcon(R.drawable.ic_action_hide);
                Log.i("mostrar", ""+mostrar);
                MapFragment.gMap.clear();
                mostrar = false;
            }
            else mostrar=true;
        }
        if (mostrar) {
            mDBAdapter = new DBAdapter((context));
            mDBAdapter.open();

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
                MapFragment.gMap.addMarker(new MarkerOptions().position(location).title(name).snippet(desc).icon(colour));
            }
            menu.findItem(R.id.show).setIcon(R.drawable.ic_action_show_all);
            mostrar = true;
        }
            /*Toast.makeText(context,
                    "Lista de mapas en construcción",
                    Toast.LENGTH_SHORT).show();*/
            //return true;


        return super.onOptionsItemSelected(item);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.i("MA",json);
        return json;
    }

    public static ArrayList<Ciudades> getCities() {
        return cities;
    }

    public static void setCities(ArrayList<Ciudades> cities) {
        MainActivity.cities = cities;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
