package co.edu.unipiloto.scrumbacklog.activity.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.api.login.RegisterRequest;
import co.edu.unipiloto.scrumbacklog.model.Usuario;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre,
            etUsuario,
            etDireccion,
            etCorreo,
            etPassword,
            etPasswordConfirm,
            etFecha;

    private RadioGroup rgGenero;
    private Spinner spinnerEstacion;

    private Button btnRegistrar,
            btnVolver;

    private List<Ubicacion> listaUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etUsuario = findViewById(R.id.etUsuario);
        etDireccion = findViewById(R.id.etDireccion);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etFecha = findViewById(R.id.etFecha);

        rgGenero = findViewById(R.id.rgGenero);

        spinnerEstacion = findViewById(R.id.spinnerEstacion);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver);

        cargarEstaciones();

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        btnVolver.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginScreenActivity.class
                    );

            startActivity(intent);
        });
    }

    private void cargarEstaciones() {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            listaUbicaciones = response.body();

                            ArrayAdapter<Ubicacion> adapter =
                                    new ArrayAdapter<>(
                                            RegisterActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            listaUbicaciones
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spinnerEstacion.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Error cargando estaciones",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Toast.makeText(
                                RegisterActivity.this,
                                "No hay conexión con backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void registrarUsuario() {

        String nombre =
                etNombre.getText().toString().trim();

        String usuarioTxt =
                etUsuario.getText().toString().trim();

        String direccion =
                etDireccion.getText().toString().trim();

        String correo =
                etCorreo.getText().toString()
                        .trim()
                        .toLowerCase();

        String password =
                etPassword.getText().toString().trim();

        String confirm =
                etPasswordConfirm.getText().toString().trim();

        String fecha =
                etFecha.getText().toString().trim();

        if (nombre.isEmpty()
                || usuarioTxt.isEmpty()
                || correo.isEmpty()
                || password.isEmpty()
                || confirm.isEmpty()) {

            Toast.makeText(
                    this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!password.equals(confirm)) {

            Toast.makeText(
                    this,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (spinnerEstacion.getSelectedItem() == null) {

            Toast.makeText(
                    this,
                    "Seleccione estación",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int selectedId =
                rgGenero.getCheckedRadioButtonId();

        String genero = "";

        if (selectedId != -1) {

            RadioButton rb =
                    findViewById(selectedId);

            genero = rb.getText().toString();
        }

        Ubicacion ubicacionSeleccionada =
                (Ubicacion) spinnerEstacion.getSelectedItem();

        int idUbicacion =
                ubicacionSeleccionada.getIdUbicacion();

        String rol = "cliente";

        RegisterRequest request =
                new RegisterRequest(
                        nombre,
                        usuarioTxt,
                        correo,
                        password,
                        direccion,
                        rol,
                        idUbicacion,
                        fecha,
                        genero,
                        1
                );

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.registrarUsuario(request).enqueue(new Callback<Usuario>() {

                    @Override
                    public void onResponse(
                            Call<Usuario> call,
                            Response<Usuario> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Usuario registrado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Error al registrar usuario",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Usuario> call,
                            Throwable t) {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}