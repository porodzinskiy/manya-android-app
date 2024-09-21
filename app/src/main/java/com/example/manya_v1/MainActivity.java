package com.example.manya_v1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Xml;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.manya_v1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager;


import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {



    private Set<BluetoothDevice> pairedDevices;
    public String bt_address = null;
    private ArrayList<String> arrayList = new ArrayList();
    public ArrayAdapter<String> arrayAdapter;
    public Boolean bt_auto_connect_flag;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ActivityMainBinding binding;
    public SharedPreferences sharedPreferences;
    Handler handler = new Handler();
    public String bt_name;
    public int neon_brightness, neon_smooth, neon_red, neon_green, neon_blue;
    public int trunk_brightness, trunk_smooth, trunk_red, trunk_green, trunk_blue;
    public int angel_brightness, angel_smooth, angel_red, angel_green, angel_blue;
    public int demon_brightness, demon_smooth, demon_red, demon_green, demon_blue;
    public int car_colour = 0xFFDDDDDD;
    public Boolean bt_connected_flag = false;
    private BluetoothSocket bluetoothSocket;
    private String old_title_name = "";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String[] myPermissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_led, R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (isNotificationPermission() == false) {
            Toast.makeText(this, "Необходимо разрешение", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String new_title_name = playerTitle();
                if (new_title_name != null && bt_connected_flag == true) {
                    if (old_title_name.equals(new_title_name) == false) {
                        old_title_name = new_title_name;
                        String info = convertCyrilic(playerArtist()) + " - " + convertCyrilic(playerTitle());
                        btSendInfoString(10, info);
                    }
                }
                handler.postDelayed(this, 200);
            }
        };
        handler.postDelayed(runnable, 200);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, myPermissions, 1);
            Toast.makeText(getApplicationContext(), "Необходимо разрешение", Toast.LENGTH_SHORT).show();
        } else if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth недоступен", Toast.LENGTH_SHORT).show();
        } else if (bluetoothAdapter.isEnabled() == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        } else {
            btPairedDevicesList();
        }
        bt_auto_connect_flag = sharedPreferences.getBoolean("APP_SETTINGS_BTCONNECTSTART", false);
        bt_address = sharedPreferences.getString("APP_SETTINGS_BTADDRESS", null);
    }

    public void btPairedDevicesList() {
        if (isBluetoothPermission() == true) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                arrayList.add(sharedPreferences.getString("APP_SETTINGS_BTINFO", "Устройство не выбрано").toString());
                for (BluetoothDevice bluetoothDevice : pairedDevices) {
                    arrayList.add(bluetoothDevice.getName().toString() + "\n" + bluetoothDevice.getAddress().toString());
                }
            } else {
                arrayList.add("Устройства отсутствуют");
            }
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bt_auto_connect_flag == true){
            btConnectMethod();
        }
    }

    public Boolean isBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Необходимо разрешение", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth недоступен", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bluetoothAdapter.isEnabled() == false) {
            Toast.makeText(getApplicationContext(), "Bluetooth отключен", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public Boolean isNotificationPermission() {
        String theList = android.provider.Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        String[] theListList = theList.split(":");
        String me = (new ComponentName(this, NotificationListener.class)).flattenToString();
        for (String next : theListList) {
            if (me.equals(next)) return true;
        }
        return false;
    }

    public void btChangeAddress(String address){
        bt_address = address;
    }

    void playerPrev() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        audioManager.dispatchMediaKeyEvent(keyEvent);
    }

    void playerPlay() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMusicActive() == true) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
            audioManager.dispatchMediaKeyEvent(keyEvent);
        } else {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
            audioManager.dispatchMediaKeyEvent(keyEvent);
        }
    }

    void playerNext() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
        audioManager.dispatchMediaKeyEvent(keyEvent);
    }

    String playerArtist() {
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        if (isNotificationPermission() == true) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
                for (MediaController mediaController1 : controllers) {
                    MediaController mediaController = mediaController1;
                    MediaMetadata meta = mediaController.getMetadata();
                    return meta.getString(MediaMetadata.METADATA_KEY_ARTIST);
                }
            }
        }
        return null;
    }

    String playerTitle() {
        if (isNotificationPermission() == true) {
            ComponentName componentName = new ComponentName(this, MainActivity.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
                for (MediaController mediaController1 : controllers) {
                    MediaController mediaController = mediaController1;
                    MediaMetadata meta = mediaController.getMetadata();
                    return meta.getString(MediaMetadata.METADATA_KEY_TITLE);
                }
            }
        }
        return null;
    }

    public void btConnectMethod (){
        if (bt_connected_flag == false){
            if (bt_address == null){
                Toast.makeText(getApplicationContext(), "Выберите bluetooth утсройство", Toast.LENGTH_SHORT).show();
                return;
            }
            new btConnect().execute();
        }
    }


    private class btConnect extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
        private ProgressDialog progressDialog;
        private BluetoothAdapter bluetoothAdapter;
        private int bt_in_errors_counter = 0;
        private int bt_msg_red;
        private int bt_msg_green;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Подключение к bluetooth устройству", "Подождите");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (bluetoothSocket == null || bt_connected_flag == false) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(bt_address);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
                bt_name = "Не подключено к устройству";
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (bluetoothSocket.getInputStream() != null && bt_connected_flag == true){
                            InputStreamReader inputStreamReader = new InputStreamReader(bluetoothSocket.getInputStream(), StandardCharsets.ISO_8859_1);
                            while (inputStreamReader.ready()){
                                int bt_msg_address = inputStreamReader.read();
                                int bt_msg_value = inputStreamReader.read();
                                switch (bt_msg_address){
                                    case 1:
                                        bt_msg_red = bt_msg_value;
                                        break;
                                    case 2:
                                        bt_msg_green = bt_msg_value;
                                        break;
                                    case 3:
                                        car_colour = Color.rgb(bt_msg_red, bt_msg_green, bt_msg_value);
                                        break;

                                    case 10:
                                        playerPrev();
                                        break;
                                    case 11:
                                        playerPlay();
                                        break;
                                    case 12:
                                        playerNext();
                                        break;

                                    case 20:
                                        neon_brightness = bt_msg_value;
                                        break;
                                    case 21:
                                        neon_red = bt_msg_value;
                                        break;
                                    case 22:
                                        neon_green = bt_msg_value;
                                        break;
                                    case 23:
                                        neon_blue = bt_msg_value;
                                        break;
                                    case 24:
                                        neon_smooth = bt_msg_value;
                                        changerColorPicker("Неон", neon_brightness, neon_red, neon_green, neon_blue, neon_smooth);
                                        break;

                                    case 30:
                                        trunk_brightness = bt_msg_value;
                                        break;
                                    case 31:
                                        trunk_red = bt_msg_value;
                                        break;
                                    case 32:
                                        trunk_green = bt_msg_value;
                                        break;
                                    case 33:
                                        trunk_blue = bt_msg_value;
                                        break;
                                    case 34:
                                        trunk_smooth = bt_msg_value;
                                        changerColorPicker("Багажник", trunk_brightness, trunk_red, trunk_green, trunk_blue, trunk_smooth);
                                        break;

                                    case 40:
                                        angel_brightness = bt_msg_value;
                                        break;
                                    case 41:
                                        angel_red = bt_msg_value;
                                        break;
                                    case 42:
                                        angel_green = bt_msg_value;
                                        break;
                                    case 43:
                                        angel_blue = bt_msg_value;
                                        break;
                                    case 44:
                                        angel_smooth = bt_msg_value;
                                        changerColorPicker("Ангельские глазки", angel_brightness, angel_red, angel_green, angel_blue, angel_smooth);
                                        break;

                                    case 50:
                                        demon_brightness = bt_msg_value;
                                        break;
                                    case 51:
                                        demon_red = bt_msg_value;
                                        break;
                                    case 52:
                                        demon_green = bt_msg_value;
                                        break;
                                    case 53:
                                        demon_blue = bt_msg_value;
                                        break;
                                    case 54:
                                        demon_smooth = bt_msg_value;
                                        changerColorPicker("Дьявольские глазки", demon_brightness, demon_red, demon_green, demon_blue, demon_smooth);
                                        break;

                                    default:
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Ошибка чтения сообщения с автомобиля", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        bt_in_errors_counter = bt_in_errors_counter + 1;
                                        if (bt_in_errors_counter > 10){
                                            bt_in_errors_counter = 0;
                                            btDisconnect();
                                        }
                                        break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        btDisconnect();

                    }
                }
            }, 0, 100);
            return null;
        }



        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (ConnectSuccess == false){
                Toast.makeText(getApplicationContext(), "Не удалось подключиться", Toast.LENGTH_SHORT).show();
                bt_connected_flag = false;
            } else {
                Toast.makeText(getApplicationContext(), "Подключено к bluetooth устройству", Toast.LENGTH_SHORT).show();
                bt_connected_flag = true;
                btSendInfo( 0,  100);
            }
            progressDialog.dismiss();
        }
    }

    public void btDisconnect(){
        if (bluetoothSocket != null){
            try {
                bt_connected_flag = false;
                car_colour = Color.rgb(221, 221, 221);
                bluetoothSocket.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Отключено от bluetooth устройства", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e){
                Toast.makeText(getApplicationContext(), "Не удалось отключиться от устройства", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btDisconnect();
    }
    public void btSendInfo (int address, int value){
        if (bluetoothSocket != null && isBluetoothPermission() == true && bt_connected_flag == true){
            try {
                bluetoothSocket.getOutputStream().write(address);
                bluetoothSocket.getOutputStream().write(value);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ошибка передачи сообщения", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Не подключено к устройству", Toast.LENGTH_SHORT).show();
        }
    }
    public void btSendInfoWithoutAddress (int value){
        if (bluetoothSocket != null && isBluetoothPermission() == true && bt_connected_flag == true){
            try {
                bluetoothSocket.getOutputStream().write(value);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ошибка передачи сообщения", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Не подключено к устройству", Toast.LENGTH_SHORT).show();
        }
    }
    public void btSendInfoString (int address, String value){
        if (bluetoothSocket != null && isBluetoothPermission() == true && bt_connected_flag == true){
            try {
                bluetoothSocket.getOutputStream().write(address);
                bluetoothSocket.getOutputStream().write(value.length());
                bluetoothSocket.getOutputStream().write(value.toString().getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Ошибка передачи сообщения", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Не подключено к устройству", Toast.LENGTH_SHORT).show();
        }
    }


    public void colorPicker(String title, int startAdr) {

        new ColorPickerDialog.Builder(this)
                .setTitle(title)
                .setPreferenceName(title)
                .setPositiveButton(getString(R.string.confirm),
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                int[] colours = envelope.getArgb();
                                int smooth = colours[0];
                                int red = colours[1];
                                int green = colours[2];
                                int blue = colours[3];
                                int brightness = Math.max(Math.max(red, green), blue);
                                btSendInfo(startAdr, brightness);
                                btSendInfoWithoutAddress(red);
                                btSendInfoWithoutAddress(green);
                                btSendInfoWithoutAddress(blue);
                                btSendInfoWithoutAddress(smooth);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();

    }

    public void changerColorPicker(String title, int brightness, int red, int green, int blue, int smooth){
        ColorPickerPreferenceManager manager = ColorPickerPreferenceManager.getInstance(this);
        red = red * brightness/255;
        green = green * brightness/255;
        blue = blue * brightness/255;
        int color = Color.argb(smooth, red, green, blue);
        manager.setColor(title, color);
        manager.setBrightnessSliderPosition(title, brightness*3);
    }
    public static String convertCyrilic(String message){
        char[] abcCyr =   {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч', 'ш', 'щ',   'ъ','ы','ь','э', 'ю', 'я' ,'А','Б','В','Г','Д', 'Е','Ё',  'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У', 'Ф', 'Х','Ц', 'Ч',  'Ш', 'Щ', 'Ъ','Ы','Ь','Э','Ю', 'Я' ,'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6','7','8','9','0','/','-', '$'};
        String[] abcLat = {" ","a","b","v","g","d","e","e","zh","z","i","j","k","l","m","n","o","p","r","s","t","u","f","h","c","ch","ch","ch'", "", "i","'","a", "yu","ya","A","B","V","G","D","Ye","Ye","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F",  "H","C", "Ch", "Sh","Sh","", "I","'","E","Yu","Ya","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","0","/","-", "S"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++ ) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }
    public static String convertTextToString(String message){
        char[] abcLat1 =   {   ' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0','/', '-', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h','i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y','Z'};
        String[] latNum =    {"10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72","73","74"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcLat1.length; x++ ) {
                if (message.charAt(i) == abcLat1[x]) {
                    builder.append(latNum[x]);
                }
            }
        }
        return builder.toString();
    }
}



