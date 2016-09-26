package pe.edu.ulima.conexionesremotas;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button butIr;
    TextView tviContenido;
    private String mContenido="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tviContenido = (TextView) findViewById(R.id.tviContenido);
        butIr = (Button) findViewById(R.id.butIr);
        butIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler = new Handler();
                Thread hilo = new Thread(){
                    @Override
                    public void run() {
                        try {
                            String resp = obtenerContenido("http://www.pornhub.com");
                            mContenido = resp;
                            handler.post(pintarEnTextView);
                            Log.i("MainActivity","Resp: " + resp);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("MainActivity", e.getMessage());
                        }
                    }
                };
                hilo.start();

            }
        });

    }
    private Runnable pintarEnTextView = new Runnable() {
        @Override
        public void run() {
            tviContenido.setText(mContenido);
        }
    };

    private String obtenerContenido(String ruta) throws IOException {
        InputStream is=null;

        try {
            URL url = new URL(ruta);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(20000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            int code = conn.getResponseCode();

            if(code == 200){
                //Todo esta correcto
                is = conn.getInputStream();
                return convertInputStreamToString(is);
            }else{
                return "Error :" + conn.getResponseMessage();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return e.getMessage();
        }finally {
            if(is!=null){
                is.close();
            }
        }
    }
    private String convertInputStreamToString(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }
}
