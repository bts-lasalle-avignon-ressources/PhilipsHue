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
import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private final String TAG               = "ClientHTTPHue";
    private OkHttpClient clientOkHttp      = null;
    private String       adresseIPPontHue  = "";
    private String       url               = null;
    private String       hueApplicationKey = "";
    private Button       boutonDecouvrir;
    private Button       boutonAuthentifier;
    private Button       boutonEnvoyer;
    private Button       boutonEteindre;
    private Button       boutonAllumer;
    private TextView     urlRequete;
    private TextView     reponseEtat;
    private TextView     reponseJson;
    private List<String> idEclairages = null;

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

        urlRequete      = (TextView)findViewById(R.id.urlRequete);
        reponseEtat     = (TextView)findViewById(R.id.reponseEtat);
        reponseJson     = (TextView)findViewById(R.id.reponseJson);
        idEclairages    = new ArrayList<String>();
        boutonDecouvrir = (Button)findViewById(R.id.boutonDecouvrir);
        boutonDecouvrir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    url = "https://discovery.meethue.com/";
                    decouvrirPontHue(url);
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
        boutonAuthentifier = (Button)findViewById(R.id.boutonAuthentifier);
        boutonAuthentifier.setEnabled(false);
        boutonAuthentifier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    url = "https://" + adresseIPPontHue + "/api";
                    authentifierHue(url);
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
        boutonEnvoyer = (Button)findViewById(R.id.boutonEnvoyer);
        boutonEnvoyer.setEnabled(false);
        boutonEnvoyer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    if(!adresseIPPontHue.isEmpty())
                    {
                        url = "https://" + adresseIPPontHue + "/clip/v2/resource/light";
                        listerEclairages(url);
                    }
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
        boutonEteindre = (Button)findViewById(R.id.boutonEteindre);
        boutonEteindre.setEnabled(false);
        boutonEteindre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    if(!adresseIPPontHue.isEmpty())
                    {
                        for(int i = 0; i < idEclairages.size(); i++)
                        {
                            String id = idEclairages.get(i);
                            url = "https://" + adresseIPPontHue + "/clip/v2/resource/light/" + id;
                            eteindreEclairage(url);
                        }
                    }
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
        boutonAllumer = (Button)findViewById(R.id.boutonAllumer);
        boutonAllumer.setEnabled(false);
        boutonAllumer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(estConnecteReseau())
                {
                    if(!adresseIPPontHue.isEmpty())
                    {
                        for(int i = 0; i < idEclairages.size(); i++)
                        {
                            String id = idEclairages.get(i);
                            url = "https://" + adresseIPPontHue + "/clip/v2/resource/light/" + id;
                            allumerEclairage(url);
                        }
                    }
                }
                else
                {
                    reponseEtat.setText("Aucune connexion réseau !");
                }
            }
        });
    }

    private void decouvrirPontHue(String url)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "decouvrirPontHue() url = " + url);
        urlRequete.setText(url);

        Request request =
          new Request.Builder().url(url).addHeader("Content-Type", "application/json").build();

        clientOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "decouvrirPontHue() onFailure");
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
                Log.d(TAG, "decouvrirPontHue() onResponse - message = " + response.message());
                Log.d(TAG, "decouvrirPontHue() onResponse - code    = " + response.code());

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
                        [
                            {
                                "id":"ecb5fafffe01c1b5",
                                "internalipaddress":"192.168.52.16",
                                "port":443
                            }
                        ]
                        */
                        JSONArray json = null;

                        try
                        {
                            json                     = new JSONArray(body);
                            JSONObject payloadFields = null;
                            payloadFields            = json.getJSONObject(0);
                            adresseIPPontHue         = payloadFields.getString("internalipaddress");
                            Log.d(TAG, "decouvrirPontHue() adresseIPPontHue = " + adresseIPPontHue);
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if(!adresseIPPontHue.isEmpty())
                        {
                            boutonAuthentifier.setEnabled(true);
                        }
                        else
                        {
                            boutonAuthentifier.setEnabled(false);
                            boutonEnvoyer.setEnabled(false);
                            boutonEteindre.setEnabled(false);
                            boutonAllumer.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    private void authentifierHue(String url)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "authentifierHue() url = " + url);
        urlRequete.setText(url);

        MediaType   JSON     = MediaType.parse("application/json; charset=utf-8");
        String      jsonBody = "{\"devicetype\": \"" + TAG + "\",\"generateclientkey\": true}";
        RequestBody body     = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

        clientOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "authentifierHue() onFailure");
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
                Log.d(TAG, "authentifierHue() onResponse - message = " + response.message());
                Log.d(TAG, "authentifierHue() onResponse - code    = " + response.code());

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
                        Log.d(TAG, "authentifierHue() body = " + body);
                        reponseJson.setText(body);

                        /*
                            [{"error":{"type":101,"address":"","description":"link button not pressed"}}]
                            [{"success":{"username":"XXXXXXXX","clientkey":"YYYYYYYY"}}]
                        */

                        JSONArray json = null;

                        try
                        {
                            json                     = new JSONArray(body);
                            JSONObject payloadFields = null;
                            payloadFields            = json.getJSONObject(0);
                            hueApplicationKey = "";
                            if(payloadFields.has("success"))
                            {
                                Log.d(TAG, "authentifierHue() username = " + payloadFields.getJSONObject("success").getString("username"));
                                hueApplicationKey = payloadFields.getJSONObject("success").getString("username");
                            }
                            else if (payloadFields.has("error"))
                            {
                                Log.d(TAG, "authentifierHue() description = " + payloadFields.getJSONObject("error").getString("description"));
                                if(payloadFields.getJSONObject("error").getInt("type") == 101)
                                    reponseEtat.setText(payloadFields.getJSONObject("error").getString("description"));
                            }
                            else
                            {
                                Log.d(TAG, "authentifierHue() payloadFields = " + payloadFields.toString());
                            }

                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if(!hueApplicationKey.isEmpty())
                        {
                            boutonEnvoyer.setEnabled(true);
                        }
                        else
                        {
                            boutonEnvoyer.setEnabled(false);
                            boutonEteindre.setEnabled(false);
                            boutonAllumer.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    private void listerEclairages(String url)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "listerEclairages() url = " + url);
        urlRequete.setText(url);
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
                Log.d(TAG, "listerEclairages() onFailure");
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
                Log.d(TAG, "listerEclairages() onResponse - message = " + response.message());
                Log.d(TAG, "listerEclairages() onResponse - code    = " + response.code());

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
                            {
                                "errors": [],
                                "data": [
                                    {
                                        "id": "5d4ba384-f49c-45a1-a68f-874a5008b967",
                                        "id_v1": "/lights/3",
                                        ...
                                        "type": "light"
                                    },
                                    {
                                        "id": "a40100ed-eedf-4f8b-b5f9-271b790edb2f",
                                        "id_v1": "/lights/2",
                                        ...
                                        "type": "light"
                                    },
                                    {
                                        "id": "f4d936da-441d-46d5-bf58-0089ad4a9aa9",
                                        "id_v1": "/lights/1",
                                        ...
                                        "type": "light"
                                    }
                                ]
                            }
                        */
                        JSONObject json = null;

                        try
                        {
                            json           = new JSONObject(body);
                            JSONArray data = json.getJSONArray("data");

                            idEclairages.clear();
                            for(int i = 0; i < data.length(); i++)
                            {
                                JSONObject eclairage = data.getJSONObject(i);
                                String     id        = eclairage.getString("id");
                                Log.d(TAG,
                                      "listerEclairages() id = " + id +
                                        " - type = " + eclairage.getString("type"));
                                if(eclairage.getString("type").equals("light"))
                                    idEclairages.add(id);
                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if(idEclairages.size() > 0)
                        {
                            boutonEteindre.setEnabled(true);
                            boutonAllumer.setEnabled(true);
                        }
                        else
                        {
                            boutonEteindre.setEnabled(false);
                            boutonAllumer.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    private void eteindreEclairage(String url)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "eteindreEclairage() url = " + url);
        urlRequete.setText(url);

        MediaType   JSON     = MediaType.parse("application/json; charset=utf-8");
        String      jsonBody = "{\"on\":{\"on\": false}}";
        RequestBody body     = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                            .url(url)
                            .put(body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("hue-application-key", hueApplicationKey)
                            .build();

        clientOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "eteindreEclairage() onFailure");
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
                Log.d(TAG, "eteindreEclairage() onResponse - message = " + response.message());
                Log.d(TAG, "eteindreEclairage() onResponse - code    = " + response.code());

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
                            {"data":[{"rid":"f4d936da-441d-46d5-bf58-0089ad4a9aa9","rtype":"light"}],"errors":[]}
                        */
                    }
                });
            }
        });
    }

    private void allumerEclairage(String url)
    {
        if(clientOkHttp == null)
            return;
        Log.d(TAG, "allumerEclairage() url = " + url);
        urlRequete.setText(url);

        MediaType   JSON     = MediaType.parse("application/json; charset=utf-8");
        String      jsonBody = "{\"on\":{\"on\": true}}";
        RequestBody body     = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                            .url(url)
                            .put(body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("hue-application-key", hueApplicationKey)
                            .build();

        clientOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "eteindreEclairage() onFailure");
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
                Log.d(TAG, "eteindreEclairage() onResponse - message = " + response.message());
                Log.d(TAG, "eteindreEclairage() onResponse - code    = " + response.code());

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
                            {"data":[{"rid":"f4d936da-441d-46d5-bf58-0089ad4a9aa9","rtype":"light"}],"errors":[]}
                        */
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