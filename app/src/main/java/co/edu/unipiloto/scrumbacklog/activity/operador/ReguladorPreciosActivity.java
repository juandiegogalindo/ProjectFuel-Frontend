package co.edu.unipiloto.scrumbacklog.activity.operador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.PrecioResponse;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Combustible;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReguladorPreciosActivity
        extends AppCompatActivity {

    private Spinner spCiudad,
            spLocalidad,
            spCombustible;

    private TextView txtPrecioActual;

    private EditText etNuevoPrecio;

    private Button btnActualizarPrecio;

    private String rol;

    private int idUbicacion;

    private ApiService apiService;

    private boolean inicializado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_regulador_precios
        );

        // ===================================
        // TOOLBAR
        // ===================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    "Regulador de Precios"
            );

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // ===================================
        // API
        // ===================================

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // ===================================
        // SESION
        // ===================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        rol =
                prefs.getString("rol", "");

        idUbicacion =
                prefs.getInt(
                        "id_ubicacion",
                        -1
                );

        // ===================================
        // REFERENCIAS
        // ===================================

        spCiudad =
                findViewById(R.id.spCiudad);

        spLocalidad =
                findViewById(R.id.spZona);

        spCombustible =
                findViewById(R.id.spCombustible);

        txtPrecioActual =
                findViewById(R.id.txtPrecioActual);

        etNuevoPrecio =
                findViewById(R.id.etNuevoPrecio);

        btnActualizarPrecio =
                findViewById(R.id.btnActualizarPrecio);

        cargarCombustibles();

        configurarSegunRol();

        configurarListeners();

        inicializado = true;

        btnActualizarPrecio
                .setOnClickListener(v ->
                        actualizarPrecio());
    }

    // ===================================
    // CONFIGURAR SEGUN ROL
    // ===================================

    private void configurarSegunRol() {

        if (rol.equalsIgnoreCase("OPERADOR")) {

            cargarUbicacionOperador();

        } else {

            cargarCiudades();
        }
    }

    // ===================================
    // LISTENERS
    // ===================================

    private void configurarListeners() {

        // ===================================
        // CIUDAD
        // ===================================

        spCiudad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        // SOLO ADMIN
                        if (!rol.equalsIgnoreCase("OPERADOR")) {

                            if (spCiudad.getSelectedItem() != null) {

                                String ciudad =
                                        spCiudad
                                                .getSelectedItem()
                                                .toString();

                                cargarZonas(ciudad);
                            }
                        }

                        if (inicializado) {

                            mostrarPrecioActual();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        // ===================================
        // ZONA
        // ===================================

        spLocalidad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (inicializado) {

                            mostrarPrecioActual();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        // ===================================
        // COMBUSTIBLE
        // ===================================

        spCombustible.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (inicializado) {

                            mostrarPrecioActual();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });
    }

    // ===================================
    // CARGAR COMBUSTIBLES
    // ===================================

    private void cargarCombustibles() {

        apiService.obtenerCombustibles()
                .enqueue(new Callback<List<Combustible>>() {

                    @Override
                    public void onResponse(
                            Call<List<Combustible>> call,
                            Response<List<Combustible>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ArrayAdapter<Combustible> adapter =
                                    new ArrayAdapter<>(
                                            ReguladorPreciosActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            response.body()
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spCombustible.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Combustible>> call,
                            Throwable t) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Error cargando combustibles",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ===================================
    // CIUDADES
    // ===================================

    private void cargarCiudades() {

        apiService.obtenerCiudades()
                .enqueue(new Callback<List<String>>() {

                    @Override
                    public void onResponse(
                            Call<List<String>> call,
                            Response<List<String>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            ReguladorPreciosActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            response.body()
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spCiudad.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<String>> call,
                            Throwable t) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Error cargando ciudades",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ===================================
    // ZONAS
    // ===================================

    private void cargarZonas(String ciudad) {

        apiService.obtenerZonas(ciudad)
                .enqueue(new Callback<List<String>>() {

                    @Override
                    public void onResponse(
                            Call<List<String>> call,
                            Response<List<String>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            ReguladorPreciosActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            response.body()
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spLocalidad.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<String>> call,
                            Throwable t) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Error cargando zonas",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ===================================
    // UBICACION OPERADOR
    // ===================================

    private void cargarUbicacionOperador() {

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            for (Ubicacion u
                                    : response.body()) {

                                if (u.getIdUbicacion()
                                        == idUbicacion) {

                                    // ===================================
                                    // CIUDAD
                                    // ===================================

                                    ArrayAdapter<String> ciudadAdapter =
                                            new ArrayAdapter<>(
                                                    ReguladorPreciosActivity.this,
                                                    android.R.layout.simple_spinner_item,
                                                    Collections.singletonList(
                                                            u.getCiudad()
                                                    )
                                            );

                                    ciudadAdapter.setDropDownViewResource(
                                            android.R.layout.simple_spinner_dropdown_item
                                    );

                                    spCiudad.setAdapter(ciudadAdapter);

                                    // ===================================
                                    // LOCALIDAD
                                    // ===================================

                                    ArrayAdapter<String> zonaAdapter =
                                            new ArrayAdapter<>(
                                                    ReguladorPreciosActivity.this,
                                                    android.R.layout.simple_spinner_item,
                                                    Collections.singletonList(
                                                            u.getLocalidad()
                                                    )
                                            );

                                    zonaAdapter.setDropDownViewResource(
                                            android.R.layout.simple_spinner_dropdown_item
                                    );

                                    spLocalidad.setAdapter(zonaAdapter);

                                    // ===================================
                                    // DESHABILITAR Y PONER EN GRIS
                                    // ===================================

                                    spCiudad.setEnabled(false);
                                    spCiudad.setClickable(false);
                                    spCiudad.setAlpha(0.5f);

                                    spLocalidad.setEnabled(false);
                                    spLocalidad.setClickable(false);
                                    spLocalidad.setAlpha(0.5f);

                                    // ===================================
                                    // MOSTRAR PRECIO
                                    // ===================================

                                    if (spCombustible.getSelectedItem() != null) {

                                        mostrarPrecioActual();
                                    }

                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Error cargando ubicación",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ===================================
    // MOSTRAR PRECIO
    // ===================================

    private void mostrarPrecioActual() {

        if (spCombustible.getSelectedItem() == null) {
            return;
        }

        Combustible combustible =
                (Combustible)
                        spCombustible.getSelectedItem();

        // ===================================
        // OPERADOR
        // ===================================

        if (rol.equalsIgnoreCase("OPERADOR")) {

            apiService.obtenerPrecioPorUbicacion(
                    combustible.getNombre(),
                    idUbicacion
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        txtPrecioActual.setText(
                                "Precio actual: $"
                                        + response.body().getPrecio()
                        );
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t) {

                    Toast.makeText(
                            ReguladorPreciosActivity.this,
                            "Error obteniendo precio",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        }

        // ===================================
        // ADMIN
        // ===================================

        else {

            if (spCiudad.getSelectedItem() == null
                    || spLocalidad.getSelectedItem() == null) {
                return;
            }

            apiService.obtenerPrecioZona(
                    combustible.getNombre(),
                    spCiudad.getSelectedItem().toString(),
                    spLocalidad.getSelectedItem().toString()
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        txtPrecioActual.setText(
                                "Precio actual: $"
                                        + response.body().getPrecio()
                        );
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t) {

                    Toast.makeText(
                            ReguladorPreciosActivity.this,
                            "Error obteniendo precio",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }

    // ===================================
    // ACTUALIZAR PRECIO
    // ===================================

    private void actualizarPrecio() {

        if (spCombustible.getSelectedItem() == null) {
            return;
        }

        double nuevoPrecio;

        try {

            nuevoPrecio =
                    Double.parseDouble(
                            etNuevoPrecio
                                    .getText()
                                    .toString()
                    );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Precio inválido",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Combustible combustible =
                (Combustible)
                        spCombustible.getSelectedItem();

        // ===================================
        // OPERADOR
        // ===================================

        if (rol.equalsIgnoreCase("OPERADOR")) {

            apiService.actualizarPrecioUbicacion(
                    combustible.getNombre(),
                    idUbicacion,
                    nuevoPrecio
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Precio actualizado",
                                Toast.LENGTH_SHORT
                        ).show();

                        mostrarPrecioActual();
                    }
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t) {

                    Toast.makeText(
                            ReguladorPreciosActivity.this,
                            "Error conexión backend",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        // ===================================
        // ADMIN
        // ===================================

        else {

            if (spCiudad.getSelectedItem() == null
                    || spLocalidad.getSelectedItem() == null) {
                return;
            }

            apiService.actualizarPrecioZona(
                    combustible.getNombre(),
                    spCiudad.getSelectedItem().toString(),
                    spLocalidad.getSelectedItem().toString(),
                    nuevoPrecio
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                ReguladorPreciosActivity.this,
                                "Precio actualizado",
                                Toast.LENGTH_SHORT
                        ).show();

                        mostrarPrecioActual();
                    }
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t) {

                    Toast.makeText(
                            ReguladorPreciosActivity.this,
                            "Error conexión backend",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }

    // ===================================
    // TOOLBAR BACK
    // ===================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    // ===================================
    // MENU
    // ===================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_toolbar_principal,
                menu
        );

        return true;
    }

    // ===================================
    // MENU ACTIONS
    // ===================================

    @Override
    public boolean onOptionsItemSelected(
            MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Gestión de precios",
                    Toast.LENGTH_SHORT
            ).show();

            return true;

        } else if (item.getItemId()
                == R.id.action_logout) {

            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ===================================
    // LOGOUT
    // ===================================

    private void cerrarSesion() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        SharedPreferences.Editor editor =
                prefs.edit();

        editor.clear();
        editor.apply();

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}