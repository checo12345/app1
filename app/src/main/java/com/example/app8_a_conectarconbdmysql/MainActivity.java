package com.example.app8_a_conectarconbdmysql;

//importaciones necesarias
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //declaraciones de objetos necesarios para manejar controles del .xml que viene en el layout
    Button btnconsultar, btnGuardar;
    EditText etId, etNombres, etTelefono;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instanciamos objetos
        btnconsultar = (Button)findViewById(R.id.btnConsultar);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        etId = (EditText)findViewById(R.id.etId);
        etNombres = (EditText)findViewById(R.id.etNombres);
        etTelefono = (EditText)findViewById(R.id.etTelefono);

        //eventos OnclicListener de los botones

        //evento que manda a llamar al archivo 'consulta.php' y busca el 'id' obtenido de etId (Consulta)
        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                            //10.0.2.2 -> localhost
                                            //CursoAndroid -> Es la carpeta que debemos tener en el servidor web(Yo lo tenía en la carpeta 'www' dentro de WampServer y ahí adentro vienen los scripts php para la conexión con la bd
                                            //consulta.php -> script para consultar info en la bd
                new ConsultarDatos().execute("http://10.0.2.2/CursoAndroid/consulta.php?id="+etId.getText().toString());

            }
        });


        //evento que manda a llamar al archivo 'registro.php' y le pasa los datos ingresados en la app para que éste las guarde en la BD
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                                            //10.0.2.2 -> localhost
                                                            //CursoAndroid -> Es la carpeta que debemos tener en el servidor web(Yo lo tenía en la carpeta 'www' dentro de WampServer y ahí adentro vienen los scripts php para la conexión con la bd
                                                            //registro.php -> script para agregar registro en la bd
                new CargarDatos().execute("http://10.0.2.2/CursoAndroid/registro.php?nombres="+etNombres.getText().toString()+"&tel="+etTelefono.getText().toString());

            }
        });



    }//fin OnCreate()

    /////////////////////////////////Clases Internas////////////////////////////////////////
    private class CargarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();

        }
    }//fin clase CargarDatos


    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        //ejecuta la consulta..
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        //pero después de obtener la consulta, realizará lo siguiente...
        // onPostExecute displays the results of the AsyncTask.
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONArray ja = null;//objeto que guardará la info obtenida de la consulta
            try {
                //resul = es lo que devuleve la consulta al buscar por id

                //guardamos ése resul en nuestro JSONArray y
                ja = new JSONArray(result);

                //mandamos a visualizar a las cajas de texto
                etNombres.setText(ja.getString(1));
                etTelefono.setText(ja.getString(2));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //////////////////////////////M é t o d o s///////////////////////////////////////////
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");/*es necesario hacer éste cambio(espacios por porcentaje 20) para que no nos genere error al acceder a una página del servidor*/
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();/*abre la conexión*/
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");/*método con el que recibiremos los datos*/
            conn.setDoInput(true);
            // Starts the query
            conn.connect();/*realizamos la conexión*/
            int response = conn.getResponseCode(); /*devolverá un valor entero depende si se conecta o no*/
            Log.d("respuesta", "The response is: " + response);/*mostramos dicho valor*/
            is = conn.getInputStream(); /*lo que responda la url lo va a guardar en la url*/

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
