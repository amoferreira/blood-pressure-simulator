package org.feup_cmeb_group7.app1_blood_pressure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Hashtable;

public class QRActivity extends AppCompatActivity {
    private final String ISO_SET = "ISO-8859-1";
    private final static int IMAGE_SIZE=600;
    TextView tvResult;
    ImageView qrCode;
    int dpValue, spValue, hrValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);
        tvResult = findViewById(R.id.tv_result);
        qrCode = findViewById(R.id.im_qr);
        spValue = getIntent().getIntExtra("sp_value", 1);
        dpValue = getIntent().getIntExtra("dp_value", 1);
        hrValue = getIntent().getIntExtra("hr_value", 1);

        // create byte array with double input values
        byte[] values = createByteArray(spValue, dpValue, hrValue);
        String result = valuesToString(values);

        // generate QR code
        new Thread(new convertToQR(values)).start();
        tvResult.setText(result);
    }

    byte[] createByteArray(int sp, int dp, int hr) {
        ByteBuffer bf = ByteBuffer.allocate(8+3*4);     // long + nr_inputs * double

        long time = System.currentTimeMillis();
        bf.putLong(time);
        bf.putInt(sp);
        bf.putInt(dp);
        bf.putInt(hr);

        return bf.array();
    }

    String valuesToString(byte[] values) {
        StringBuilder sb = new StringBuilder();
        Timestamp ts;

        ByteBuffer bf = ByteBuffer.wrap(values);
        ts = new Timestamp(bf.getLong());
        sb.append("Time: ");
        sb.append(ts.toString());
        sb.append("\n Systolic Pressure: ");
        sb.append(bf.getInt());
        sb.append("\n Diastolic Pressure: ");
        sb.append(bf.getInt());
        sb.append("\n Heart Rate: ");
        sb.append(bf.getInt());

        return sb.toString();
    }

    Bitmap encodeAsBitmap(byte[] content) {
        BitMatrix result;
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, ISO_SET);
        String str = new String(content, StandardCharsets.ISO_8859_1);
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, IMAGE_SIZE, IMAGE_SIZE, hints);
        }
        catch (Exception exc) {
            runOnUiThread(()->tvResult.setText(exc.getMessage()));
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int line = 0; line < h; line++) {
            int offset = line * w;
            for (int col = 0; col < w; col++) {
                pixels[offset + col] = result.get(col, line) ? getResources().getColor(R.color.blue_1):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    // nested class to implement a thread to create a QR code as a Bitmap
    // it can take a while to do this processing

    class convertToQR implements Runnable {
        byte[] content;

        convertToQR(byte[] value) {
            content = value;
            tvResult.setText("");
        }

        @Override
        public void run() {
            final Bitmap bitmap;

            bitmap = encodeAsBitmap(content);
            runOnUiThread(()->qrCode.setImageBitmap(bitmap));
        }
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