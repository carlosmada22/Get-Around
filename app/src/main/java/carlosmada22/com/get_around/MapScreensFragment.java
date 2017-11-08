package carlosmada22.com.get_around;


import android.*;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapScreensFragment extends Fragment {

    ListView list;
    MapAdapter adapter;

    int posicion;

    File file;
    private String[] mFileStrings;
    private File[] listFile;
    public ImageLoader imageLoader;
    MapView gMapView;
    GoogleMap gMap;
    Bitmap myBitmap;


    public static MapScreensFragment newInstance() {
        return new MapScreensFragment();
    }

    public MapScreensFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ...

        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_screens, container, false);
        //File file = new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/");
        file = new File("/storage/emulated/0/Maps/");
        Log.i("directorio", ""+file.toString());

        if (file.isDirectory())
        {
            Log.i("directorio?", "si");
            listFile = file.listFiles();
            mFileStrings = new String[listFile.length];

            Log.i("vacio?", "" + listFile.length);
            for (int i = 0; i < listFile.length; i++)
            {
                mFileStrings[i] = listFile[i].getAbsolutePath();
                Log.i("archivos", ""+ mFileStrings[i]);
            }
        }
        else {
            file.mkdirs();
            Log.i("directorio?", "no");
        }

        list = (ListView) v.findViewById(R.id.list);
        adapter = new MapAdapter(getActivity(), mFileStrings);
        list.setAdapter(adapter);


        imageLoader=new ImageLoader(getActivity().getApplicationContext());

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                View crear = getActivity().getLayoutInflater().inflate(R.layout.image_display, null);
                ImageView imageView= (ImageView) crear.findViewById(R.id.imageView);
                String path_name = mFileStrings[i];
                String file_name=path_name.substring(path_name.indexOf("Maps/") + 5,path_name.indexOf(".jpg"));
                build.setTitle(file_name);
                File imgFile = new  File(path_name);
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                build.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    } });
                build.setView(crear);
                AlertDialog ver_mapa = build.create();
                ver_mapa.show();
            }
        });

        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int position, long l) {
                posicion = position;
                CharSequence[] options = {"Cambiar nombre", "Eliminar Mapa", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Elige una opción");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                break;

                            case 1:
                                File archivo = new File(mFileStrings[posicion]);
                                boolean deleted = archivo.delete();
                                if(deleted) Toast.makeText(getContext(),
                                        "Mapa eliminado",
                                        Toast.LENGTH_SHORT).show();
                                listFile = file.listFiles();
                                mFileStrings = new String[listFile.length];
                                for (int j = 0; j < listFile.length; j++)
                                {
                                    mFileStrings[j] = listFile[j].getAbsolutePath();
                                }
                                adapter = new MapAdapter(getActivity(), mFileStrings);
                                list.setAdapter(adapter);
                                break;

                            case 2:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        //Button b = (Button) v.findViewById(R.id.button1);
        //b.setOnClickListener(listener);

        return v;
    }

    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }

    /*public View.OnClickListener listener=new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };*/


}
