package com.uas.facite.adoptaunbache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapBoxActivity extends AppCompatActivity {

    private MapView mapa;
    private MapboxMap mapboxMap;
    private FloatingActionButton BotonAgregarBache;
    private String WEB_SERVICE = "http://facite.uas.edu.mx/adoptaunbache/api/insertar_bache.php";


    BottomSheetBehavior botomSheet;
    LinearLayout layout_capturarBache;
    TextView txt_direccion, txt_latitud, txt_longitud;
    ImageButton botonCamara;
    Button botonAdoptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = "pk.eyJ1Ijoiam9yZ2VjYW5lZG8iLCJhIjoiY2syM3U3NGNkMDFybjNtbW54azdmaHZ2cyJ9.6gqxALyiqgkmsSJ5F7YutQ";
        //creamos una instancia de mapbox
        Mapbox.getInstance(this, key);
        setContentView(R.layout.activity_map_box);
        //identificamos el visor de nuestro diseño
        mapa = findViewById(R.id.mapViewMapBox);
        layout_capturarBache = (LinearLayout) findViewById(R.id.capturar_bache);
        botomSheet = BottomSheetBehavior.from(layout_capturarBache);
        txt_direccion = (TextView) findViewById(R.id.txtDireccion);
        txt_latitud = (TextView) findViewById(R.id.txtLatitud);
        txt_longitud = (TextView) findViewById(R.id.txtLatitud);
        botonCamara = (ImageButton) findViewById(R.id.botonCamara);
        botonAdoptar = (Button) findViewById(R.id.botonAdoptar);

        botonAdoptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Funcionalidad del Boton Camara
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //desplegar una alerta con posibles opciones a hacer
                final CharSequence[] opciones = {"Tomar fotografia", "Desde galeria", "Cancelar"};
                AlertDialog.Builder alerta = new AlertDialog.Builder(MapBoxActivity.this);
                alerta.setTitle("Agregar una Fotografia para el bache");
                alerta.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //PROGRAMAR LA FUNCIONALIDAD DE LAS OPCIONES
                        if (opciones[which].equals("Tomar fotografia")) {
                            //SOLICITAR PERMISOS A LA CAMARA EN CASO DE QUE NO LO TENGA
                            //VERIFICAR EL SDK DEL TELEFONO DONDE SE EST EJECUTANDO NUESTRA APP
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                //verificar permisos para la camara
                                if (ContextCompat.checkSelfPermission(MapBoxActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(camara, 1);
                                } else {
                                    //solicitamos los permisos a la camara
                                    ActivityCompat.requestPermissions(MapBoxActivity.this, new String[]{Manifest.permission.CAMERA}, 507);
                                    return;
                                }
                            } else {
                                Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(camara, 1);
                            }
                        } else if (opciones[which].equals("Desde Galeria")) {
                            if (ContextCompat.checkSelfPermission(MapBoxActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galeria, 2);
                            } else {
                                ActivityCompat.requestPermissions(MapBoxActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 507);
                                return;
                            }
                        } else {
                            Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galeria, 2);
                        }
                    }
                });
                alerta.show();
            }


        });


        mapa.onCreate(savedInstanceState);
        mapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                //referencia hacia el mapa
                MapBoxActivity.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        //CARGAR LOS PUNTOS DEL GeoJson de nuestra Api
                        try {
                            style.addSource(new GeoJsonSource("GEOJSON_PUNTOS",
                                    new URI("http://facite.uas.edu.mx/adoptaunbache/api/getlugares.php")));
                        } catch (URISyntaxException e) {
                           Log.i("ERROR GEOJSON:", e.toString());
                        }
                        //Creamos el icono personalizado para nuestros marcadores (puntos)
                        Bitmap icono = BitmapFactory.decodeResource(getResources(), R.drawable.alarm);
                        //agregar el icono al estilo del mapa
                        style.addImage("BACHE_ICONO", icono);
                        //Crear una capa layer con los datos cargados desde geojson
                        SymbolLayer BachesCapa = new SymbolLayer("BACHES", "GEOJSON_PUNTOS");
                        //Asignamos el icono personalizado a la capa de baches
                        BachesCapa.setProperties(PropertyFactory.iconImage("BACHE_ICONO"));
                        //Asignamos la capa de baches al mapa
                        style.addLayer(BachesCapa);
                        //POSICIONAR EL MARCADOR ESTATICO EN EL CENTRO DEL MAPA
                        ImageView MarcadorPin;
                        MarcadorPin = new ImageView(MapBoxActivity.this);
                        MarcadorPin.setImageResource(R.drawable.ic_pinwarning);
                        //Posicionar el MarcadorPin en el centro del mapa
                        FrameLayout.LayoutParams parametros = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                        //aplicamos esos parametros al marcador
                        MarcadorPin.setLayoutParams(parametros);
                        //Agregamos el marcador al mapa cargado
                        mapa.addView(MarcadorPin);
                        //Identificamos el Boton flotante del Layout
                        BotonAgregarBache = (FloatingActionButton)findViewById(R.id.btnAgregarBache);
                        //capturar el click del boton agregar bache
                        BotonAgregarBache.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Obtener las coordenadas x y del centro del mapa
                                final LatLng coordenadas = mapboxMap.getCameraPosition().target;
                                //Mandamos una alerta bonita con las coordenadas
                               /* new SweetAlertDialog(MapBoxActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Coordenadas del Marcador Pin")
                                        .setContentText(" Latitud: "+ coordenadas.getLatitude() +
                                                        " Longitud: " + coordenadas.getLongitude())
                                        .show();
                                //Obtener la direccion con el metodo le enviamos el view del boton
                                ObtenerDireccion(v);*/
                                //mostrar el layout capturar bache
                                botomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                                txt_latitud.setText("Latitud: "+coordenadas.getLatitude());
                                txt_longitud.setText("Longitud: "+coordenadas.getLongitude());
                            }
                        });
                    }
                });

                //llevar a la posicion de culiacan
                CameraPosition posicion = new CameraPosition.Builder()
                        .target(new LatLng(24.8087148, -107.3941223)) //estalece la posicion
                        .zoom(10) //establecer el zoom
                        //.bearing(180) //rota la camara
                        .tilt(80) //angulo de inclinacion
                        .build();

                //mover la posicion del mapa
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(posicion), 5000);


            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        MapBoxActivity.super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            Bitmap foto = (Bitmap) intent.getExtras().get("data");
            Drawable fotodrawable = new BitmapDrawable(foto);
            botonCamara.setImageDrawable(fotodrawable);
        } else if (requestCode == 2) {
            Uri fotoseleccionada = intent.getData();
            String[] rutaImagen = {MediaStore.Images.Media.DATA};
            Cursor cursor = MapBoxActivity.this.getApplicationContext().getContentResolver().query(fotoseleccionada, rutaImagen, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(rutaImagen[0]);
            String archivoFoto = cursor.getString(columnIndex);
            cursor.close();
            Bitmap foto = (BitmapFactory.decodeFile(archivoFoto));
            Drawable fotodrawable = new BitmapDrawable(foto);
            botonCamara.setImageDrawable(fotodrawable);

        }
    }

    //MEtodo para obtener la direccion en base a la latitud y la longitud
    public void ObtenerDireccion(View view){
        try{
        //Obtener las coordenadas del Marcador Pin en el mapa
        final LatLng coordenadas = mapboxMap.getCameraPosition().target;
        final Point punto = Point.fromLngLat(coordenadas.getLongitude(), coordenadas.getLatitude());
        //Utilizar los servicios de Mapbox para Geodecodificar la direccion en base al punto
        MapboxGeocoding servicio = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoiYWJlbGRleCIsImEiOiJjazFsYjJuODgwMTZzM21waW94MXdkb3VpIn0.CRUvSFmmzZN_UK0IYLt3fA")
                .query(Point.fromLngLat(punto.longitude(), punto.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();

        //Ejecutar el servicio con los parametros que estalecimos
        servicio.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                //si el resultado no fue no nulo osea que si encontro una direccion
                if(response.body() != null){
                    List<CarmenFeature> resultados = response.body().features();
                    //obtenemos la direccion
                    CarmenFeature direccion = resultados.get(0);
                    //mostrar la direccion obtenida
                    txt_direccion.setText(direccion.placeName());


                }
            }
            @Override
             public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                    //Mostramos que no existieeron direcciones que mostrar

             }
        });
    }
        catch(ServicesException servicesException){
            Log.i("Error del servicio", servicesException.toString());
        }
    }

    class RegistrarBache extends AsyncTask<Void, Void, String>{

        String direccion, latitud, longitud, foto;
        //creamos el conductor de esta clase
        RegistrarBache(String direccion, String latitud, String longitud, String foto)
        {
            this.direccion = direccion;
            this.latitud = latitud;
            this.longitud = longitud;
            this.foto = foto;

        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            HashMap<String,String> parametros = new HashMap<>();
            parametros.put("nombre", direccion);
            parametros.put("lat", latitud);
            parametros.put("lon", longitud);
            parametros.put("img", foto);
            return  requestHandler.sendPostRequest(WEB_SERVICE, parametros);

        }
        @Override
        protected  void onPostExecute(String respuesta){
            super.onPostExecute(respuesta);
            try {
                JSONObject object =new JSONObject(respuesta);
                int status = object.getInt("status");
                if (status ==1) {
                    new SweetAlertDialog(MapBoxActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Excelente")
                            .setContentText(object.getString("message"))
                            .show();
                }
                else {
                    new SweetAlertDialog(MapBoxActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("UPS")
                            .setContentText(object.getString("message"))
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
