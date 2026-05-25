package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.InventarioResponse;
import co.edu.unipiloto.scrumbacklog.api.MovimientoRequest;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;
import co.edu.unipiloto.scrumbacklog.api.PrecioResponse;
import co.edu.unipiloto.scrumbacklog.model.Combustible;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalidasActivity extends AppCompatActivity {

    TextView txtInventarioDisponible;

    Spinner spTipoCombustible,
            spCiudad,
            spZona;

    EditText etSalida;

    Button btnRetirar;

    ListView listHistorial;

    ArrayList<String> historial =
            new ArrayList<>();

    ArrayAdapter<String> adapterHistorial;

    String rol;

    int idUbicacion;

    ApiService apiService;

    List<Combustible> listaCombustibles =
            new ArrayList<>();

    List<Ubicacion> listaUbicaciones =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_salidas);

        // =====================================
        // TOOLBAR
        // =====================================

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    "Salidas de Combustible");

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // =====================================
        // SESION
        // =====================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        rol = prefs.getString("rol", "");

        idUbicacion =
                prefs.getInt("id_ubicacion", -1);

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // =====================================
        // REFERENCIAS
        // =====================================

        txtInventarioDisponible =
                findViewById(R.id.txtInventarioDisponible);

        spTipoCombustible =
                findViewById(R.id.spTipoCombustible);

        spCiudad =
                findViewById(R.id.spCiudad);

        spZona =
                findViewById(R.id.spZona);

        etSalida =
                findViewById(R.id.etSalida);

        btnRetirar =
                findViewById(R.id.btnRetirar);

        listHistorial =
                findViewById(R.id.listHistorial);

        // =====================================
        // HISTORIAL
        // =====================================

        adapterHistorial = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                historial
        );

        listHistorial.setAdapter(adapterHistorial);

        // =====================================
        // CARGAR DATOS
        // =====================================

        cargarCombustibles();

        cargarUbicaciones();

        configurarPorRol();

        configurarListeners();

        // =====================================
        // BOTON
        // =====================================

        btnRetirar.setOnClickListener(v -> {

            registrarSalida();
        });
    }

    // =====================================
    // COMBUSTIBLES
    // =====================================

    private void cargarCombustibles() {

        apiService.obtenerCombustibles()
                .enqueue(new Callback<List<Combustible>>() {

                    @Override
                    public void onResponse(
                            Call<List<Combustible>> call,
                            Response<List<Combustible>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            listaCombustibles =
                                    response.body();

                            ArrayAdapter<Combustible> adapter =
                                    new ArrayAdapter<>(
                                            SalidasActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            listaCombustibles
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spTipoCombustible.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Combustible>> call,
                            Throwable t) {

                        Toast.makeText(
                                SalidasActivity.this,
                                "Error cargando combustibles",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================
// UBICACIONES
// =====================================

    private void cargarUbicaciones() {

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            listaUbicaciones =
                                    response.body();

                            // =====================================
                            // OPERADOR
                            // =====================================

                            if (rol.equalsIgnoreCase("OPERADOR")) {

                                for (Ubicacion u
                                        : listaUbicaciones) {

                                    if (u.getIdUbicacion()
                                            == idUbicacion) {

                                        // CIUDAD

                                        ArrayAdapter<String> adapterCiudad =
                                                new ArrayAdapter<>(
                                                        SalidasActivity.this,
                                                        android.R.layout.simple_spinner_item,
                                                        java.util.Collections.singletonList(
                                                                u.getCiudad()
                                                        )
                                                );

                                        adapterCiudad.setDropDownViewResource(
                                                android.R.layout.simple_spinner_dropdown_item
                                        );

                                        spCiudad.setAdapter(adapterCiudad);

                                        // ZONA

                                        ArrayAdapter<String> adapterZona =
                                                new ArrayAdapter<>(
                                                        SalidasActivity.this,
                                                        android.R.layout.simple_spinner_item,
                                                        java.util.Collections.singletonList(
                                                                u.getLocalidad()
                                                        )
                                                );

                                        adapterZona.setDropDownViewResource(
                                                android.R.layout.simple_spinner_dropdown_item
                                        );

                                        spZona.setAdapter(adapterZona);

                                        // BLOQUEAR SPINNERS

                                        spCiudad.setEnabled(false);

                                        spZona.setEnabled(false);

                                        break;
                                    }
                                }

                                actualizarInventarioUI();

                                return;
                            }

                            // =====================================
                            // ADMIN / DISTRIBUIDOR
                            // =====================================

                            ArrayList<String> ciudades =
                                    new ArrayList<>();

                            for (Ubicacion u
                                    : listaUbicaciones) {

                                if (!ciudades.contains(
                                        u.getCiudad())) {

                                    ciudades.add(
                                            u.getCiudad());
                                }
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            SalidasActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            ciudades
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spCiudad.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Toast.makeText(
                                SalidasActivity.this,
                                "Error cargando ubicaciones",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================
    // CONFIGURAR ROL
    // =====================================

    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("DISTRIBUIDOR")) {

            btnRetirar.setEnabled(false);

            btnRetirar.setAlpha(0.4f);

            etSalida.setEnabled(false);

            Toast.makeText(
                    this,
                    "Modo consulta",
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (rol.equalsIgnoreCase("CLIENTE")) {

            finish();
        }
    }

    // =====================================
    // LISTENERS
    // =====================================

    private void configurarListeners() {

        spCiudad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        cargarZonas();

                        actualizarInventarioUI();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        spZona.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        actualizarInventarioUI();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });

        spTipoCombustible.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        actualizarInventarioUI();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });
    }

    // =====================================
    // ZONAS
    // =====================================

    private void cargarZonas() {

        if (spCiudad.getSelectedItem() == null)
            return;

        String ciudad =
                spCiudad.getSelectedItem().toString();

        ArrayList<String> zonas =
                new ArrayList<>();

        for (Ubicacion u : listaUbicaciones) {

            if (u.getCiudad().equalsIgnoreCase(
                    ciudad)) {

                zonas.add(u.getLocalidad());
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        zonas
                );

        spZona.setAdapter(adapter);
    }

    // =====================================
    // INVENTARIO
    // =====================================

    private void actualizarInventarioUI() {

        Integer idUbicacionSeleccionada =
                obtenerIdUbicacionSeleccionada();

        if (idUbicacionSeleccionada == null)
            return;

        apiService.obtenerInventarioPorUbicacion(
                idUbicacionSeleccionada
        ).enqueue(new Callback<List<InventarioResponse>>() {

            @Override
            public void onResponse(
                    Call<List<InventarioResponse>> call,
                    Response<List<InventarioResponse>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    String combustibleSeleccionado =
                            spTipoCombustible
                                    .getSelectedItem()
                                    .toString();

                    double cantidad = 0;

                    for (InventarioResponse inventario
                            : response.body()) {

                        if (inventario.getCombustible()
                                .equalsIgnoreCase(
                                        combustibleSeleccionado)) {

                            cantidad =
                                    inventario.getCantidad();
                        }
                    }

                    txtInventarioDisponible.setText(
                            cantidad
                                    + " galones disponibles"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<List<InventarioResponse>> call,
                    Throwable t) {

                Toast.makeText(
                        SalidasActivity.this,
                        "Error inventario",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // =====================================
    // REGISTRAR SALIDA
    // =====================================

    private void registrarSalida() {

        if (spTipoCombustible.getSelectedItem() == null)
            return;

        String cantidadTexto =
                etSalida.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese cantidad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double galones =
                Double.parseDouble(cantidadTexto);

        Combustible combustible =
                (Combustible)
                        spTipoCombustible.getSelectedItem();

        Integer idUbicacionSeleccionada =
                obtenerIdUbicacionSeleccionada();

        if (idUbicacionSeleccionada == null)
            return;

        apiService.obtenerPrecioPorUbicacion(
                combustible.getNombre(),
                idUbicacionSeleccionada
        ).enqueue(new Callback<PrecioResponse>() {

            @Override
            public void onResponse(
                    Call<PrecioResponse> call,
                    Response<PrecioResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    MovimientoRequest request =
                            new MovimientoRequest();

                    request.setIdCombustible(
                            combustible.getIdCombustible());

                    request.setIdUbicacion(
                            idUbicacionSeleccionada);

                    request.setTipoMovimiento(
                            "SALIDA");

                    request.setGalones(galones);

                    request.setPrecioUnitario(
                            response.body().getPrecio());

                    request.setFecha(
                            new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm",
                                    Locale.getDefault()
                            ).format(new Date())
                    );

                    apiService.registrarSalida(request)
                            .enqueue(
                                    new Callback<MovimientoResponse>() {

                                        @Override
                                        public void onResponse(
                                                Call<MovimientoResponse> call,
                                                Response<MovimientoResponse> response) {

                                            if (response.isSuccessful()) {

                                                historial.add(
                                                        0,
                                                        request.getFecha()
                                                                + " | "
                                                                + combustible.getNombre()
                                                                + " | "
                                                                + galones
                                                                + " gal"
                                                );

                                                adapterHistorial
                                                        .notifyDataSetChanged();

                                                etSalida.setText("");

                                                actualizarInventarioUI();

                                                Toast.makeText(
                                                        SalidasActivity.this,
                                                        "Salida registrada",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                            } else {

                                                Toast.makeText(
                                                        SalidasActivity.this,
                                                        "Inventario insuficiente",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(
                                                Call<MovimientoResponse> call,
                                                Throwable t) {

                                            Toast.makeText(
                                                    SalidasActivity.this,
                                                    "Error conexión backend",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });
                }
            }

            @Override
            public void onFailure(
                    Call<PrecioResponse> call,
                    Throwable t) {

                Toast.makeText(
                        SalidasActivity.this,
                        "Error obteniendo precio",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // =====================================
    // OBTENER ID UBICACION
    // =====================================

    private Integer obtenerIdUbicacionSeleccionada() {

        if (rol.equalsIgnoreCase("OPERADOR")) {

            return idUbicacion;
        }

        if (spCiudad.getSelectedItem() == null
                || spZona.getSelectedItem() == null) {

            return null;
        }

        String ciudad =
                spCiudad.getSelectedItem().toString();

        String zona =
                spZona.getSelectedItem().toString();

        for (Ubicacion u : listaUbicaciones) {

            if (u.getCiudad().equalsIgnoreCase(ciudad)
                    && u.getLocalidad().equalsIgnoreCase(zona)) {

                return u.getIdUbicacion();
            }
        }

        return null;
    }

    // =====================================
    // TOOLBAR
    // =====================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_consulta,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Gestión de salidas",
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

    // =====================================
    // CERRAR SESION
    // =====================================

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