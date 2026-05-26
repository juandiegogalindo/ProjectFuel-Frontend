package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.InventarioResponse;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControlInventarioActivity
        extends AppCompatActivity {

    private LinearLayout layoutInventario,
            layoutHistorial;

    private Spinner spFiltroCombustible,
            spFiltroCiudad,
            spFiltroEstacion;

    private String rol;

    private int idUbicacionUsuario;

    private boolean inicializado = false;

    private ApiService apiService;

    private List<Ubicacion> listaUbicaciones =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_control_inventario
        );

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    "Control Inventario"
            );

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // =====================================================
        // API
        // =====================================================

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // =====================================================
        // SESION
        // =====================================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        rol = prefs.getString("rol", "");

        idUbicacionUsuario =
                prefs.getInt(
                        "id_ubicacion",
                        -1
                );

        // =====================================================
        // COMPONENTES
        // =====================================================

        spFiltroCombustible =
                findViewById(
                        R.id.spFiltroCombustible
                );

        spFiltroCiudad =
                findViewById(
                        R.id.spFiltroCiudad
                );

        spFiltroEstacion =
                findViewById(
                        R.id.spFiltroEstacion
                );

        layoutInventario =
                findViewById(
                        R.id.layoutInventario
                );

        layoutHistorial =
                findViewById(
                        R.id.layoutHistorial
                );

        // =====================================================
        // CONFIGURACION
        // =====================================================

        configurarPorRol();

        cargarCombustibles();

        configurarListeners();

        inicializado = true;
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
                R.menu.menu_toolbar_principal,
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
                    "Consulta de inventario y movimientos",
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

    // =====================================================
    // CONFIGURAR POR ROL
    // =====================================================

    private void configurarPorRol() {

        if (rol.equalsIgnoreCase("CLIENTE")) {

            finish();

            return;
        }

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

                            if (rol.equalsIgnoreCase("ADMIN")
                                    || rol.equalsIgnoreCase("DISTRIBUIDOR")) {

                                cargarCiudades();

                            } else if (rol.equalsIgnoreCase("OPERADOR")) {

                                configurarOperador();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ControlInventarioActivity.this,
                                "Error cargando ubicaciones",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // CONFIGURAR OPERADOR
    // =====================================================

    private void configurarOperador() {

        for (Ubicacion u : listaUbicaciones) {

            if (u.getIdUbicacion()
                    == idUbicacionUsuario) {

                spFiltroCiudad.setAdapter(
                        new ArrayAdapter<>(
                                this,
                                android.R.layout
                                        .simple_spinner_dropdown_item,
                                Collections.singletonList(
                                        u.getNombre()
                                )
                        )
                );

                spFiltroEstacion.setAdapter(
                        new ArrayAdapter<>(
                                this,
                                android.R.layout
                                        .simple_spinner_dropdown_item,
                                Collections.singletonList(
                                        u.getNombre()
                                )
                        )
                );

                spFiltroCiudad.setEnabled(false);

                spFiltroEstacion.setEnabled(false);

                refrescarVista(
                        idUbicacionUsuario
                );

                break;
            }
        }
    }

    // =====================================================
    // COMBUSTIBLES
    // =====================================================

    private void cargarCombustibles() {

        String[] combustibles = {
                "Todos",
                "Gasolina Corriente",
                "Gasolina Extra",
                "Gasolina Diesel"
        };

        spFiltroCombustible.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        combustibles
                )
        );
    }

    // =====================================================
    // CIUDADES
    // =====================================================

    private void cargarCiudades() {

        ArrayList<String> nombres =
                new ArrayList<>();

        for (Ubicacion u : listaUbicaciones) {

            nombres.add(u.getNombre());
        }

        spFiltroCiudad.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        nombres
                )
        );

        spFiltroCiudad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        cargarEstaciones(position);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                });
    }

    // =====================================================
    // ESTACIONES
    // =====================================================

    private void cargarEstaciones(
            int position
    ) {

        if (listaUbicaciones.isEmpty()) {
            return;
        }

        Ubicacion ubicacion =
                listaUbicaciones.get(position);

        ArrayList<String> estaciones =
                new ArrayList<>();

        estaciones.add(
                ubicacion.getNombre()
        );

        spFiltroEstacion.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_dropdown_item,
                        estaciones
                )
        );

        refrescarVista(
                ubicacion.getIdUbicacion()
        );
    }

    // =====================================================
    // LISTENERS
    // =====================================================

    private void configurarListeners() {

        spFiltroCombustible
                .setOnItemSelectedListener(
                        new AdapterView
                                .OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                if (!listaUbicaciones.isEmpty()) {

                                    int posicion =
                                            spFiltroCiudad
                                                    .getSelectedItemPosition();

                                    int idUbicacion =
                                            listaUbicaciones
                                                    .get(posicion)
                                                    .getIdUbicacion();

                                    refrescarVista(
                                            idUbicacion
                                    );
                                }
                            }

                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {
                            }
                        });
    }

    // =====================================================
    // REFRESCAR
    // =====================================================

    private void refrescarVista(
            int idUbicacion
    ) {

        mostrarInventario(
                idUbicacion
        );

        mostrarHistorial(
                idUbicacion
        );
    }

    // =====================================================
    // INVENTARIO
    // =====================================================

    private void mostrarInventario(
            int idUbicacion
    ) {

        layoutInventario.removeAllViews();

        apiService.obtenerInventarioPorUbicacion(
                idUbicacion
        ).enqueue(new Callback<List<InventarioResponse>>() {

            @Override
            public void onResponse(
                    Call<List<InventarioResponse>> call,
                    Response<List<InventarioResponse>> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    String filtro =
                            spFiltroCombustible
                                    .getSelectedItem()
                                    .toString();

                    for (InventarioResponse inv
                            : response.body()) {

                        if (!filtro.equals("Todos")
                                && !inv.getCombustible()
                                .equalsIgnoreCase(filtro)) {

                            continue;
                        }

                        TextView tv =
                                new TextView(
                                        ControlInventarioActivity.this
                                );

                        tv.setText(
                                inv.getCombustible()
                                        + ": "
                                        + inv.getCantidad()
                                        + " galones"
                        );

                        tv.setTextSize(16f);

                        layoutInventario.addView(tv);
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<List<InventarioResponse>> call,
                    Throwable t
            ) {

                Toast.makeText(
                        ControlInventarioActivity.this,
                        "Error cargando inventario",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // =====================================================
    // HISTORIAL
    // =====================================================

    private void mostrarHistorial(
            int idUbicacion
    ) {

        layoutHistorial.removeAllViews();

        apiService.obtenerMovimientosPorUbicacion(
                idUbicacion
        ).enqueue(new Callback<List<MovimientoResponse>>() {

            @Override
            public void onResponse(
                    Call<List<MovimientoResponse>> call,
                    Response<List<MovimientoResponse>> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    List<MovimientoResponse> movimientos =
                            response.body();

                    int limit =
                            Math.min(
                                    movimientos.size(),
                                    10
                            );

                    for (int i = 0; i < limit; i++) {

                        MovimientoResponse mov =
                                movimientos.get(i);

                        TextView tv =
                                new TextView(
                                        ControlInventarioActivity.this
                                );

                        tv.setText(
                                mov.getTipoMovimiento()
                                        + " - "
                                        + mov.getCombustible()
                                        + " - "
                                        + mov.getGalones()
                                        + " gal"
                        );

                        layoutHistorial.addView(tv);
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<List<MovimientoResponse>> call,
                    Throwable t
            ) {

                Toast.makeText(
                        ControlInventarioActivity.this,
                        "Error cargando historial",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}