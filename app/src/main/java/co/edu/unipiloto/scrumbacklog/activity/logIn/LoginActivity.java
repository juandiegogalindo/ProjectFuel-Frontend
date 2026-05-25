package co.edu.unipiloto.scrumbacklog.activity.logIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.LoginRequest;
import co.edu.unipiloto.scrumbacklog.api.LoginResponse;
import co.edu.unipiloto.scrumbacklog.api.UsuarioResponse;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreoLogin, etPasswordLogin;
    private Button btnLogin, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);

        btnLogin = findViewById(R.id.btnLogin);
        btnVolver = findViewById(R.id.btnVolver);

        btnLogin.setOnClickListener(v -> login());

        btnVolver.setOnClickListener(v -> {

            Intent intent =
                    new Intent(this, LoginScreenActivity.class);

            startActivity(intent);
        });
    }

    private void login() {

        String correo =
                etCorreoLogin.getText().toString().trim().toLowerCase();

        String password =
                etPasswordLogin.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Todos los campos son obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        LoginRequest request =
                new LoginRequest(correo, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response){

                if (response.isSuccessful() &&
                        response.body() != null) {

                    Usuario usuario = response.body().getUsuario();

                    SharedPreferences prefs =
                            getSharedPreferences("sesion", MODE_PRIVATE);

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putInt(
                            "id_usuario",
                            usuario.getId()
                    );

                    editor.putString(
                            "rol",
                            usuario.getRol()
                    );

                    editor.putInt(
                            "id_ubicacion",
                            usuario.getIdUbicacion()
                    );

                    editor.apply();

                    Toast.makeText(
                            LoginActivity.this,
                            "Login exitoso",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );

                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(
                            LoginActivity.this,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                t.printStackTrace();

                Toast.makeText(
                        LoginActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}