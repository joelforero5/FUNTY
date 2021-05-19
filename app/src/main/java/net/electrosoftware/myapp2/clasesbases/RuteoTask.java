package net.electrosoftware.myapp2.clasesbases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import net.electrosoftware.myapp2.activityes.Login;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RuteoTask extends AsyncTask<String, Void, String> {

    // Downloading data in non-ui thread
    static Context ctxMapa;
    static double latitud = 0.0;
    static double longitud = 0.0;
    static double latitudDestino = 0.0;
    static double longitudDestino = 0.0;
    static private Polyline Ruta;
    static GoogleMap mapa;
    static Context ctxActivity;
    ProgressDialog PDRuteo = null;

    public static void Parametros(Context contextMapa, LatLng LatLngOrigen, LatLng LatLngDestino, GoogleMap googlemap, Context contextActivity) {
        ctxMapa = contextMapa;
        latitud = LatLngOrigen.latitude;
        longitud = LatLngOrigen.longitude;
        latitudDestino = LatLngDestino.latitude;
        longitudDestino = LatLngDestino.longitude;
        mapa = googlemap;
        ctxActivity = contextActivity;
    }

    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl();
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executes in UI thread, after the execution of doInBackground()
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ParserTask parserTask = new ParserTask();
        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }

    private String downloadUrl() throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            String str_origin = "origin=" + latitud + "," + longitud;
            String str_dest = "destination=" + latitudDestino + "," + longitudDestino;
            // Sensor enabled
            String sensor = "sensor=false&mode=" + "driving";
            String parameters = str_origin + "&" + str_dest + "&" + sensor;
            String urlRuta = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
            URL url = new URL(urlRuta);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exc downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute() {
            PDRuteo = ProgressDialog.show(ctxActivity, "Creando Ruta", "Buscando una ruta, espera un momento...", true, true);
            PDRuteo.setCancelable(false);
            super.onPreExecute();
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            Activity act = (Activity) ctxActivity;

            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                if (Ruta != null) {
                    Ruta.remove();
                }
                // Traversing through all the routes
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<LatLng>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);


                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(4);
                        lineOptions.zIndex(99999);
                        lineOptions.color(Color.BLUE);
                        lineOptions.geodesic(true);
                    }
                    Ruta = mapa.addPolyline(lineOptions);
                    act.finish();
                } else {
                    PDRuteo.dismiss();
                    Toast.makeText(ctxMapa, "La función de ruteo requiere internet.", Toast.LENGTH_SHORT).show();
                }
                // Drawing polyline in the Google Map for the i-th route
            } catch (Exception e) {
                PDRuteo.dismiss();
                Toast.makeText(ctxMapa, "No hay una ruta disponible", Toast.LENGTH_LONG).show();
            }
        }
    }

}
