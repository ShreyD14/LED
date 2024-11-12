package com.example.led;

import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.io.OutputStream;

public class ControlActivity extends AppCompatActivity {

    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private ConstraintLayout rootLayout;
    private ImageView switchImage;
    private boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        switchImage = findViewById(R.id.switchImage);
        rootLayout = findViewById(R.id.rootLayout);

        bluetoothSocket = MainActivity.getBluetoothSocket();
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            try {
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to get output stream", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth connection not established", Toast.LENGTH_SHORT).show();
        }

        switchImage.setOnClickListener(v -> {
            if (isOn) {
                sendCommand("OFF");
                rootLayout.setBackgroundResource(R.drawable.background_off);
                switchImage.setImageResource(R.drawable.switch_off);
            } else {
                sendCommand("ON");
                rootLayout.setBackgroundResource(R.drawable.background_on);
                switchImage.setImageResource(R.drawable.switch_on);
            }
            isOn = !isOn; // Toggle the state
        });

        findViewById(R.id.disconnectButton).setOnClickListener(v -> disconnect());
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
                Toast.makeText(this, command + " command sent", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Output stream not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to disconnect", Toast.LENGTH_SHORT).show();
        }
        finish(); // Close activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }
}
