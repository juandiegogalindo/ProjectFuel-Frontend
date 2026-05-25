package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.PrecioResponse;
import co.edu.unipiloto.scrumbacklog.model.Combustible;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultaActivity
        extends AppCompatActivity {

    private Spinner spTipoCombustible,
            spCiudad,
            spZona;

    private Button btnCalcular,
            btnCalcularGalones;

    private TextView txtResultado,
            txtResultadoGalones;

    private EditText etGalones;

    private ApiService apiService;

    private String rol;

    private int idUbicacion;

    private List<Ubicacion> listaUbicaciones =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_consulta
        );

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    "Consulta"
            );

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // =====================================================
        // SESION
        // =====================================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        rol = prefs.getString("rol", "");

        idUbicacion =
                prefs.getInt(
                        "id_ubicacion",
                        -1
                );

        // =====================================================
        // API
        // =====================================================

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // =====================================================
        // COMPONENTES
        // =====================================================

        spTipoCombustible =
                findViewById(
                        R.id.spTipoCombustible
                );

        spCiudad =
                findViewById(
                        R.id.spCiudad
                );

        spZona =
                findViewById(
                        R.id.spZona
                );

        btnCalcular =
                findViewById(
                        R.id.btnCalcular
                );

        btnCalcularGalones =
                findViewById(
                        R.id.calcularGalones
                );

        etGalones =
                findViewById(
                        R.id.etGalones
                );

        txtResultado =
                findViewById(
                        R.id.txtResultado
                );

        txtResultadoGalones =
                findViewById(
                        R.id.txtResultadoGalones
                );

        // =====================================================
        // CARGAR DATOS
        // =====================================================

        cargarCombustibles();

        cargarUbicaciones();

        // =====================================================
        // EVENTOS
        // =====================================================

        btnCalcular.setOnClickListener(
                view -> consultarPrecio()
        );

        btnCalcularGalones.setOnClickListener(
                view -> calcularTotal()
        );
    }

    // =====================================================
    // CARGAR COMBUSTIBLES
    // =====================================================

    private void cargarCombustibles() {

        apiService.obtenerCombustibles()
                .enqueue(new Callback<List<Combustible>>() {

                    @Override
                    public void onResponse(
                            Call<List<Combustible>> call,
                            Response<List<Combustible>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ArrayList<String> nombres =
                                    new ArrayList<>();

                            for (Combustible c
                                    : response.body()) {

                                nombres.add(
                                        c.getNombre()
                                );
                            }

                            spTipoCombustible.setAdapter(
                                    new ArrayAdapter<>(
                                            ConsultaActivity.this,
                                            android.R.layout
                                                    .simple_spinner_dropdown_item,
                                            nombres
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Combustible>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ConsultaActivity.this,
                                "Error cargando combustibles",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // CARGAR UBICACIONES
    // =====================================================

    private void cargarUbicaciones() {

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            listaUbicaciones =
                                    response.body();

                            configurarPorRol();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ConsultaActivity.this,
                                "Error cargando ubicaciones",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // CONFIGURAR POR ROL
    // =====================================================

    private void configurarPorRol() {

        // =====================================================
        // OPERADOR
        // =====================================================

        if (rol.equalsIgnoreCase("OPERADOR")) {

            for (Ubicacion u : listaUbicaciones) {

                if (u.getIdUbicacion() == idUbicacion) {

                    // CIUDAD
                    spCiudad.setAdapter(
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout
                                            .simple_spinner_dropdown_item,
                                    Collections.singletonList(
                                            u.getCiudad()
                                    )
                            )
                    );

                    // ESTACION
                    spZona.setAdapter(
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout
                                            .simple_spinner_dropdown_item,
                                    Collections.singletonList(
                                            u.getNombre()
                                    )
                            )
                    );

                    spCiudad.setEnabled(false);

                    spZona.setEnabled(false);

                    break;
                }
            }

            return;
        }

        // =====================================================
        // ADMIN / CLIENTE / DISTRIBUIDOR
        // =====================================================

        ArrayList<String> ciudades =
                new ArrayList<>();

        // EVITAR CIUDADES REPETIDAS
        for (Ubicacion u : listaUbicaciones) {

            if (!ciudades.contains(u.getCiudad())) {

                ciudades.add(u.getCiudad());
            }
        }

        // CARGAR CIUDADES
        spCiudad.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        ciudades
                )
        );

        // =====================================================
        // CUANDO CAMBIA LA CIUDAD
        // =====================================================

        spCiudad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        String ciudadSeleccionada =
                                spCiudad
                                        .getSelectedItem()
                                        .toString();

                        ArrayList<String> estaciones =
                                new ArrayList<>();

                        // FILTRAR ESTACIONES POR CIUDAD
                        for (Ubicacion u : listaUbicaciones) {

                            if (u.getCiudad()
                                    .equalsIgnoreCase(
                                            ciudadSeleccionada
                                    )) {

                                estaciones.add(
                                        u.getNombre()
                                );
                            }
                        }

                        spZona.setAdapter(
                                new ArrayAdapter<>(
                                        ConsultaActivity.this,
                                        android.R.layout
                                                .simple_spinner_dropdown_item,
                                        estaciones
                                )
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                });
    }

    // =====================================================
    // CONSULTAR PRECIO
    // =====================================================

    private void consultarPrecio() {

        String combustible =
                spTipoCombustible
                        .getSelectedItem()
                        .toString();

        if (rol.equalsIgnoreCase("ADMIN")
                || rol.equalsIgnoreCase("CLIENTE")) {

            String ciudad =
                    spCiudad
                            .getSelectedItem()
                            .toString();

            String zona =
                    spZona
                            .getSelectedItem()
                            .toString();

            Toast.makeText(
                    this,
                    "Consultando: "
                            + combustible
                            + " - "
                            + ciudad
                            + " - "
                            + zona,
                    Toast.LENGTH_LONG
            ).show();

            apiService.obtenerPrecioZona(
                    combustible,
                    ciudad,
                    zona
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response
                ) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        txtResultado.setText(
                                "Precio: $"
                                        + response.body().getPrecio()
                        );

                    } else {

                        txtResultado.setText(
                                "No se encontró precio"
                        );

                        Toast.makeText(
                                ConsultaActivity.this,
                                "Respuesta backend: "
                                        + response.code(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t
                ) {

                    txtResultado.setText(
                            "Error conexión"
                    );

                    Toast.makeText(
                            ConsultaActivity.this,
                            t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        } else {

            apiService.obtenerPrecioPorUbicacion(
                    combustible,
                    idUbicacion
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response
                ) {

                    Toast.makeText(
                            ConsultaActivity.this,
                            "CODE: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();

                    if (response.body() != null) {

                        Toast.makeText(
                                ConsultaActivity.this,
                                "PRECIO: " + response.body().getPrecio(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t
                ) {

                    txtResultado.setText(
                            "Error conexión"
                    );

                    Toast.makeText(
                            ConsultaActivity.this,
                            t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }

    // =====================================================
    // CALCULAR TOTAL
    // =====================================================

    private void calcularTotal() {

        String galonesTexto =
                etGalones.getText()
                        .toString()
                        .trim();

        if (galonesTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese galones",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double galones =
                Double.parseDouble(
                        galonesTexto
                );

        String combustible =
                spTipoCombustible
                        .getSelectedItem()
                        .toString();

        if (rol.equalsIgnoreCase("ADMIN")|| rol.equalsIgnoreCase("CLIENTE")) {

            String ciudad =
                    spCiudad
                            .getSelectedItem()
                            .toString();

            String zona =
                    spZona
                            .getSelectedItem()
                            .toString();

            apiService.obtenerPrecioZona(
                    combustible,
                    ciudad,
                    zona
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response
                ) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        double total =
                                galones
                                        * response.body()
                                        .getPrecio();

                        txtResultadoGalones.setText(
                                "Total: $"
                                        + total
                        );
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t
                ) {

                    Toast.makeText(
                            ConsultaActivity.this,
                            "Error calculando total",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });

        } else {

            apiService.obtenerPrecioPorUbicacion(
                    combustible,
                    idUbicacion
            ).enqueue(new Callback<PrecioResponse>() {

                @Override
                public void onResponse(
                        Call<PrecioResponse> call,
                        Response<PrecioResponse> response
                ) {

                    if (response.isSuccessful()
                            && response.body() != null) {

                        double total =
                                galones
                                        * response.body()
                                        .getPrecio();

                        txtResultadoGalones.setText(
                                "Total: $"
                                        + total
                        );
                    }
                }

                @Override
                public void onFailure(
                        Call<PrecioResponse> call,
                        Throwable t
                ) {

                    Toast.makeText(
                            ConsultaActivity.this,
                            "Error calculando total",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }

    // =====================================================
    // TOOLBAR BACK
    // =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    // =====================================================
    // MENU
    // =====================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_consulta,
                menu
        );

        return true;
    }

    // =====================================================
    // MENU ACTIONS
    // =====================================================

    @Override
    public boolean onOptionsItemSelected(
            MenuItem item
    ) {

        if (item.getItemId()
                == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Consulta de precios",
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

    // =====================================================
    // CERRAR SESION
    // =====================================================

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