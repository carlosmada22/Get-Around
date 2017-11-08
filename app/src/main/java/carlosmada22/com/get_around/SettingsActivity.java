package carlosmada22.com.get_around;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView kms;
    int valorMax = 15;
    double valorReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_settings);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Opciones de descarga");
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        kms = (TextView) findViewById(R.id.kms);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valorMax = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                kms.setText("");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                valorReal = (double)valorMax / 2;
                kms.setText((valorReal) + "km");


            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }
        return true;

    }
}
