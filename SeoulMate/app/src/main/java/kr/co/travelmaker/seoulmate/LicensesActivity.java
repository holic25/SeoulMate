package kr.co.travelmaker.seoulmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LicensesActivity extends AppCompatActivity {
    @BindView(R.id.txt_icon_license) TextView txt_icon_license;
    @BindView(R.id.txt_library_license) TextView txt_library_license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        ButterKnife.bind(this);

        txt_icon_license.setText(ReadIconLisence());
        txt_library_license.setText(ReadLibraryLisence());
    }
    private String ReadLibraryLisence() {
        String data = null;
        InputStream inputStream = getResources().openRawResource(R.raw.library_lisence);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = new String(byteArrayOutputStream.toByteArray(),"MS949");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String ReadIconLisence() {
        String data = null;
        InputStream inputStream = getResources().openRawResource(R.raw.icon_lisence);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = new String(byteArrayOutputStream.toByteArray(),"MS949");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}