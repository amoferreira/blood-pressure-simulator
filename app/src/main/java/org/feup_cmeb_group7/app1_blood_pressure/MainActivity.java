package org.feup_cmeb_group7.app1_blood_pressure;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText edt_d_pressure, edt_s_pressure, edt_heart_rate;
    TextView tvError_dp, tvError_sp, tvError_hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_s_pressure = findViewById(R.id.edt_s_pressure);
        edt_d_pressure = findViewById(R.id.edt_d_pressure);
        edt_heart_rate = findViewById(R.id.edt_heart_rate);
        tvError_sp = findViewById(R.id.tv_error_sp);
        tvError_dp = findViewById(R.id.tv_error_dp);
        tvError_hr = findViewById(R.id.tv_error_hr);
        findViewById(R.id.bt_generate).setOnClickListener((vw)->generateQR());
    }

    void generateQR() {
        int dp, sp, hr;
        Intent qrActivity;

        tvError_sp.setText(R.string.tv_empty);
        String stSp = edt_s_pressure.getText().toString();
        tvError_dp.setText(R.string.tv_empty);
        String stDp = edt_d_pressure.getText().toString();
        tvError_hr.setText(R.string.tv_empty);
        String stHr = edt_heart_rate.getText().toString();

        try {
            sp = Integer.parseInt(stSp);
        }
        catch (Exception e) {
            tvError_sp.setText(R.string.tv_error);
            return;
        }
        finally {
            try {
                dp = Integer.parseInt(stDp);
            }
            catch (Exception e) {
                tvError_dp.setText(R.string.tv_error);
                return;
            }
            finally {
                try {
                    hr = Integer.parseInt(stHr);
                }
                catch (Exception e) {
                    tvError_hr.setText(R.string.tv_error);
                    return;
                }
            }
        }

        qrActivity = new Intent(this, QRActivity.class);
        qrActivity.putExtra("sp_value", sp);
        qrActivity.putExtra("dp_value", dp);
        qrActivity.putExtra("hr_value", hr);
        startActivity(qrActivity);
    }

    // Three dots menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater(); //from activity
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.help:{
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.version_info:{
                Toast.makeText(this,R.string.app_version,Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return true;
    }
}