package com.uas.facite.adoptaunbache;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoingActivity extends AppCompatActivity {
    //creamos las variables para los controles que usaremos
    EditText txt_usuario;
    EditText txt_contraseña;
    Button boton_entrar;
    ProgressDialog progressDialog;
    String URL_WEB_SERVICE = "http://facite.uas.mx/adoptaunbache/api/get_usuarios.php";

    @Override
    protected  void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loing);
        //identificar los controles del layout
        txt_usuario = (EditText) findViewById(R.id.edithTextUsuario);
        txt_contraseña = (EditText) findViewById(R.id.editTextContraseña);
        boton_entrar = (Button) findViewById(R.id.btnentrar);
        //capturamos el evento click del boton login
        boton_entrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //mandamos llamar el metodo hacer login
                hacerlogin();
            }

        });
        //hacer la notification bar transparente
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    private void hacerlogin(){
        //obtener los valores de los edit text
        String usuario = txt_usuario.getText().toString();
        String contraseña = txt_contraseña.getText().toString();
        UsuarioLogin ul = new UsuarioLogin(usuario, contraseña);
        ul.execute();
    }

    public  void AbrirRegistro(View v){
        //Intent intent = new Intent( LoingActivity.this, RegistroActivity.class);
       // startActivity(intent);
    }


    //CLASE PARA HACER LA PETICION AL WEB SERVICE EN SEGUNDO PLANO
    class UsuarioLogin extends AsyncTask<Void, Void, String>{
        String usuario, contraseña;
        //Constructor de la clase Usuario Login
        UsuarioLogin(String usuario, String contraseña){
            this.usuario = usuario;
            this.contraseña = contraseña;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try {
                //convertir la respuesta del web service a un objeto JSON
                JSONObject obj = new JSONObject(s);
                //VERIFICAMOS QUE ESTADO NOS REGRESO
                if(obj.getInt("status") == 0){
                    //si el status del web service fue 0 entonces es un login correcto
                    //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    //Aqui pondremos el intent para que nos lleve al activity del mapa
                    Intent intent = new Intent(LoingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            //CREAMOS UN OBJETO DE LA CLASE RequestHandler
            RequestHandler requestHandler = new RequestHandler();
            //Crear los parametros que se enviaran al web service
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put("usuario", usuario);
            parametros.put("contraseña", contraseña);
            //retornamos la respuesta del web service
            return requestHandler.sendPostRequest(URL_WEB_SERVICE, parametros);
        }
    }
}
