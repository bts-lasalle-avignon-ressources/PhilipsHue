package com.example.myapplicationhttp;

// OkHttp : client HTTP
// https://square.github.io/okhttp/
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

// JSON (JavaScript Object Notation) est un format d'échange de données textuelles
// https://developer.android.com/reference/org/json/package-summary
import org.json.JSONObject;
import org.json.JSONException;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    private final String TAG               = "ClientHTTP";
    private OkHttpClient clientOkHttp      = null;
    private String       url               = "https://192.168.52.182/clip/v2/resource/light";
    private String       hueApplicationKey = "8FcG-JAXZa47KLLtzjmYlDp73nOBrUJ1ktbrmtvf";
    private Button       boutonEnvoyer;
    private TextView     urlRequete;
    private TextView     reponseEtat;
    private TextView     reponseJson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            clientOkHttp = TrustAllCertsClient.getTrustAllCertsClient();
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        catch(KeyManagementException e)
        {
            throw new RuntimeException(e);
        }

        urlRequete = (TextView)findViewById(R.id.urlRequete);
        urlRequete.setText(url);
        reponseEtat   = (TextView)findViewById(R.id.reponseEtat);
        reponseJson   = (TextView)findViewById(R.id.reponseJson);
        boutonEnvoyer = (Button)findViewById(R.id.boutonEnvoyer);
        boutonEnvoyer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    emettreRequeteOkHttp(url, hueApplicationKey);
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
    }

    private void emettreRequeteOkHttp(String url, String hueApplicationKey)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "emettreRequeteOkHttp()");
        Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("hue-application-key", hueApplicationKey)
                            .build();

        clientOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                Log.d("OkHttp", "onFailure");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        reponseEtat.setText("Erreur requête OkHttp !");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Log.d("OkHttp", "onResponse - message = " + response.message());
                Log.d("OkHttp", "onResponse - code    = " + response.code());

                if(!response.isSuccessful())
                {
                    throw new IOException(response.toString());
                }

                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(!response.message().isEmpty())
                            reponseEtat.setText(response.message());
                        else
                            reponseEtat.setText(String.valueOf(response.code()));
                        reponseJson.setText(body);

                        /*
                            {"power":0,"Ws":0,"relay":true,"temperature":21.5}
                        */
                        /*JSONObject json = null;

                        try
                        {
                            json = new JSONObject(body);
                            //JSONObject payloadFields = null;
                            //payloadFields = json.getJSONObject("main");
                            double power = json.getDouble("power");
                            double Ws = json.getDouble("Ws");
                            Boolean relay = json.getBoolean("relay");
                            double temperature = json.getDouble("temperature");
                            Log.d("OkHttp", "Power       = " + power);
                            Log.d("OkHttp", "Ws          = " + Ws);
                            Log.d("OkHttp", "Etat        = " + relay);
                            Log.d("OkHttp", "Température = " + temperature + " °C");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }*/
                    }
                });
            }
        });
    }

    private boolean estConnecteReseau()
    {
        ConnectivityManager connectivityManager =
          (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected() ||
           (networkInfo.getType() != ConnectivityManager.TYPE_WIFI &&
            networkInfo.getType() != ConnectivityManager.TYPE_MOBILE))
        {
            return false;
        }
        return true;
    }
}