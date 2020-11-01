package com.example.obdiidiagnostyka;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class erro extends AppCompatActivity {
    bluetoothDeviceHandler bluetoothDeviceHandler;
    ListView lvErrorCodes;
    ArrayAdapter<String> errorcodes;
    List<String> errorcodespure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erro);
        this.bluetoothDeviceHandler = com.example.obdiidiagnostyka.bluetoothDeviceHandler.getInstance();
        lvErrorCodes = findViewById(R.id.errorCodes);
        Button back = findViewById(R.id.restore);
        errorcodespure = new ArrayList<String>();
        errorcodes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, errorcodespure);
        Button errorCodesCheck = findViewById(R.id.errorcodescheck);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        errorCodesCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorCodesCheck();
            }
        });
    }

    private void errorCodesCheck() {
        String response = null;
        try {
            String message = new String("GetErrors");
            this.bluetoothDeviceHandler.sendData(message);
            response = this.bluetoothDeviceHandler.readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String[] data = response.split(";");
            for (String e :
                    data) {
                errorcodespure.add(e);
            }
        } catch (NullPointerException e) {
            finish();
        }

    }
}