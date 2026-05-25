package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.InventarioResponse;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificadorActivity extends AppCompatActivity {

    Spinner spCiudad, spZona;
    Button btnVerificar;
    TextView txtAlerta;

    String rol;
    int idUbicacion;

    private ApiService apiService;

    private List<Ubicacion> listaUbicaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificador);

        // ===== TOOLBAR =====

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Notificador");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ===================

        SharedPreferences prefs =
                getSharedPreferences("sesion", MODE_PRIVATE);

        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        apiService =
                ApiClient.getClient().create(ApiService.class);

        spCiudad = findViewById(R.id.spCiudad);
        spZona = findViewById(R.id.spZona);
        btnVerificar = findViewById(R.id.btnVerificar);
        txtAlerta = findViewById(R.id.txtAlerta);

        configurarPorRol();

        btnVerificar.setOnClickListener(v -> verificarInventario());
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
                R.menu.menu_notificador,
                menu
        );

        return true;
    }

    // =====================================================
    // MENU ACTIONS
    // =====================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Verifica niveles críticos de inventario",
                    Toast.LENGTH_SHORT
            ).show();

            return true;

        } else if (item.getItemId() == R.id.action_logout) {

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
                getSharedPreferences("sesion", MODE_PRIVATE);

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
                            Response<List<Ubicacion>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            listaUbicaciones = response.body();

                            if (rol.equalsIgnoreCase("ADMIN")) {

                                cargarCiudades();

                            } else if (rol.equalsIgnoreCase("OPERADOR")) {

                                configurarOperador();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t) {

                        Toast.makeText(
                                NotificadorActivity.this,
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

            if (u.getIdUbicacion() == idUbicacion) {

                // =====================================
                // CIUDAD FIJA
                // =====================================

                spCiudad.setAdapter(
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_dropdown_item,
                                Collections.singletonList(
                                        u.getCiudad()
                                )
                        )
                );

                // =====================================
                // ZONA FIJA
                // =====================================

                spZona.setAdapter(
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_dropdown_item,
                                Collections.singletonList(
                                        u.getLocalidad()
                                )
                        )
                );

                // =====================================
                // BLOQUEAR SPINNERS
                // =====================================

                spCiudad.setEnabled(false);
                spCiudad.setClickable(false);

                spZona.setEnabled(false);
                spZona.setClickable(false);

                break;
            }
        }
    }

    // =====================================================
    // CARGAR CIUDADES
    // =====================================================

    private void cargarCiudades() {

        ArrayList<String> nombres = new ArrayList<>();

        for (Ubicacion u : listaUbicaciones) {

            nombres.add(u.getNombre());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        nombres
                );

        spCiudad.setAdapter(adapter);

        spCiudad.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        String nombre =
                                spCiudad.getSelectedItem().toString();

                        spZona.setAdapter(new ArrayAdapter<>(
                                NotificadorActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                Collections.singletonList(nombre)
                        ));
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                });
    }

    // =====================================================
    // VERIFICAR INVENTARIO
    // =====================================================

    private void verificarInventario() {

        int ubicacionSeleccionada = idUbicacion;

        if (rol.equalsIgnoreCase("OPERADOR")) {

            String nombre =
                    spCiudad.getSelectedItem().toString();

            for (Ubicacion u : listaUbicaciones) {

                if (u.getNombre().equals(nombre)) {

                    ubicacionSeleccionada =
                            u.getIdUbicacion();

                    break;
                }
            }
        }

        apiService.obtenerInventarioPorUbicacion(
                ubicacionSeleccionada
        ).enqueue(new Callback<List<InventarioResponse>>() {

            @Override
            public void onResponse(
                    Call<List<InventarioResponse>> call,
                    Response<List<InventarioResponse>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    List<InventarioResponse> inventarios =
                            response.body();

                    StringBuilder mensaje =
                            new StringBuilder();

                    if (!inventarios.isEmpty()) {

                        mensaje.append("📍 ")
                                .append(inventarios.get(0)
                                        .getNombreUbicacion())
                                .append("\n\n");
                    }

                    boolean normal = true;

                    for (InventarioResponse inv : inventarios) {

                        if (inv.getCantidad() < 1000) {

                            normal = false;

                            mensaje.append("⚠ ")
                                    .append(inv.getCombustible())
                                    .append(" crítico: ")
                                    .append(inv.getCantidad())
                                    .append("\n");
                        }
                    }

                    if (normal) {

                        mensaje.append(
                                "✔ Inventario en niveles normales"
                        );
                    }

                    txtAlerta.setText(
                            mensaje.toString()
                    );

                } else {

                    txtAlerta.setText(
                            "No se pudo consultar inventario"
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<List<InventarioResponse>> call,
                    Throwable t) {

                txtAlerta.setText(
                        "Error conexión backend"
                );
            }
        });
    }
}