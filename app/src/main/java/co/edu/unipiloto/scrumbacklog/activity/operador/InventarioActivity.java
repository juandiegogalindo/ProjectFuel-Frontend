package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.api.InventarioResponse;
import co.edu.unipiloto.scrumbacklog.api.MovimientoRequest;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;
import co.edu.unipiloto.scrumbacklog.model.Combustible;

import co.edu.unipiloto.scrumbacklog.model.Pedido;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioActivity extends AppCompatActivity {

    private Spinner spCiudad,
            spZona;
    private List<Ubicacion> listaUbicaciones;
    Spinner spCombustible;

    EditText etCantidad;

    Button btnAgregar;

    TextView txtInventarioTotal,
            txtInventarioDiesel,
            txtInventarioCorriente,
            txtInventarioExtra;

    String rol;

    int idUbicacion;

    ApiService apiService;

    ListView listPedidosRecibidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Inventario");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences prefs =
                getSharedPreferences("sesion", MODE_PRIVATE);

        rol = prefs.getString("rol", "");
        idUbicacion = prefs.getInt("id_ubicacion", -1);

        spCiudad = findViewById(R.id.spCiudad);

        spZona = findViewById(R.id.spZona);

        apiService =
                ApiClient.getClient().create(ApiService.class);

        spCombustible = findViewById(R.id.spCombustible);

        etCantidad = findViewById(R.id.etCantidad);

        btnAgregar = findViewById(R.id.btnAgregar);

        txtInventarioTotal =
                findViewById(R.id.txtInventarioTotal);

        txtInventarioDiesel =
                findViewById(R.id.txtInventarioDiesel);

        txtInventarioCorriente =
                findViewById(R.id.txtInventarioCorriente);

        txtInventarioExtra =
                findViewById(R.id.txtInventarioExtra);

        listPedidosRecibidos =
                findViewById(R.id.listPedidosRecibidos);

        cargarCombustibles();
        cargarUbicaciones();

        actualizarInventarioOperador();

        btnAgregar.setOnClickListener(v -> registrarEntrada());

        cargarPedidosPendientes();
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
                            Response<List<Combustible>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ArrayAdapter<Combustible> adapter =
                                    new ArrayAdapter<>(
                                            InventarioActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            response.body()
                                    );

                            spCombustible.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Combustible>> call,
                            Throwable t) {

                        Toast.makeText(
                                InventarioActivity.this,
                                "Error cargando combustibles",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

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

                            listaUbicaciones = response.body();

                            // =====================================
                            // OPERADOR
                            // =====================================

                            if (rol.equalsIgnoreCase("OPERADOR")) {

                                for (Ubicacion u : listaUbicaciones) {

                                    if (u.getIdUbicacion() == idUbicacion) {

                                        ArrayAdapter<String> ciudadAdapter =
                                                new ArrayAdapter<>(
                                                        InventarioActivity.this,
                                                        android.R.layout.simple_spinner_item,
                                                        java.util.Collections.singletonList(
                                                                u.getCiudad()
                                                        )
                                                );

                                        ciudadAdapter.setDropDownViewResource(
                                                android.R.layout.simple_spinner_dropdown_item
                                        );

                                        spCiudad.setAdapter(ciudadAdapter);

                                        ArrayAdapter<String> zonaAdapter =
                                                new ArrayAdapter<>(
                                                        InventarioActivity.this,
                                                        android.R.layout.simple_spinner_item,
                                                        java.util.Collections.singletonList(
                                                                u.getLocalidad()
                                                        )
                                                );

                                        zonaAdapter.setDropDownViewResource(
                                                android.R.layout.simple_spinner_dropdown_item
                                        );

                                        spZona.setAdapter(zonaAdapter);

                                        spCiudad.setEnabled(false);
                                        spZona.setEnabled(false);

                                        break;
                                    }
                                }

                                return;
                            }

                            // =====================================
                            // ADMIN / DISTRIBUIDOR
                            // =====================================

                            java.util.ArrayList<String> ciudades =
                                    new java.util.ArrayList<>();

                            for (Ubicacion u : listaUbicaciones) {

                                if (!ciudades.contains(u.getCiudad())) {

                                    ciudades.add(u.getCiudad());
                                }
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            InventarioActivity.this,
                                            android.R.layout.simple_spinner_item,
                                            ciudades
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            spCiudad.setAdapter(adapter);

                            spCiudad.setOnItemSelectedListener(
                                    new android.widget.AdapterView.OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(
                                                android.widget.AdapterView<?> parent,
                                                android.view.View view,
                                                int position,
                                                long id
                                        ) {

                                            cargarZonas();
                                        }

                                        @Override
                                        public void onNothingSelected(
                                                android.widget.AdapterView<?> parent
                                        ) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                InventarioActivity.this,
                                "Error cargando ubicaciones",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void cargarZonas() {

        if (spCiudad.getSelectedItem() == null)
            return;

        String ciudad =
                spCiudad.getSelectedItem().toString();

        java.util.ArrayList<String> zonas =
                new java.util.ArrayList<>();

        for (Ubicacion u : listaUbicaciones) {

            if (u.getCiudad().equalsIgnoreCase(ciudad)) {

                zonas.add(u.getLocalidad());
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        zonas
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spZona.setAdapter(adapter);
    }

    // =====================================================
    // REGISTRAR ENTRADA
    // =====================================================

    private void registrarEntrada() {

        String cantidadTexto =
                etCantidad.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese cantidad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double cantidad;

        try {

            cantidad = Double.parseDouble(cantidadTexto);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Cantidad inválida",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Combustible combustible =
                (Combustible) spCombustible.getSelectedItem();

        MovimientoRequest request =
                new MovimientoRequest();

        request.setIdUbicacion(idUbicacion);
        request.setIdCombustible(
                combustible.getIdCombustible()
        );

        request.setTipoMovimiento("ENTRADA");

        request.setGalones(cantidad);

        request.setPrecioUnitario(0.0);

        String fecha =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        request.setFecha(fecha);

        apiService.registrarEntrada(request)
                .enqueue(new Callback<MovimientoResponse>() {

                    @Override
                    public void onResponse(
                            Call<MovimientoResponse> call,
                            Response<MovimientoResponse> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    InventarioActivity.this,
                                    "Entrada registrada",
                                    Toast.LENGTH_SHORT
                            ).show();

                            etCantidad.setText("");

                            actualizarInventarioOperador();

                        } else {

                            Toast.makeText(
                                    InventarioActivity.this,
                                    "Error registrando entrada",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<MovimientoResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                InventarioActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =====================================================
    // INVENTARIO
    // =====================================================

    private void actualizarInventarioOperador() {

        apiService.obtenerInventarioPorUbicacion(idUbicacion)
                .enqueue(new Callback<List<InventarioResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<InventarioResponse>> call,
                            Response<List<InventarioResponse>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            double diesel = 0;
                            double corriente = 0;
                            double extra = 0;

                            for (InventarioResponse inventario
                                    : response.body()) {

                                String combustible =
                                        inventario.getCombustible();

                                if (combustible.equalsIgnoreCase(
                                        "Gasolina Diesel")) {

                                    diesel =
                                            inventario.getCantidad();

                                } else if (combustible.equalsIgnoreCase(
                                        "Gasolina Corriente")) {

                                    corriente =
                                            inventario.getCantidad();

                                } else if (combustible.equalsIgnoreCase(
                                        "Gasolina Extra")) {

                                    extra =
                                            inventario.getCantidad();
                                }
                            }

                            actualizarTextos(
                                    diesel,
                                    corriente,
                                    extra
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<InventarioResponse>> call,
                            Throwable t) {

                        Toast.makeText(
                                InventarioActivity.this,
                                "Error obteniendo inventario",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
// PEDIDOS RECIBIDOS PENDIENTES
// =====================================================

    private void cargarPedidosPendientes() {

        apiService.obtenerPedidosEntregados(idUbicacion)
                .enqueue(new Callback<List<Pedido>>() {

                    @Override
                    public void onResponse(
                            Call<List<Pedido>> call,
                            Response<List<Pedido>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            PedidoInventarioAdapter adapter =
                                    new PedidoInventarioAdapter(
                                            InventarioActivity.this,
                                            response.body(),
                                            idUbicacion,
                                            InventarioActivity.this
                                    );

                            listPedidosRecibidos.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    InventarioActivity.this,
                                    "No hay pedidos pendientes",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Pedido>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                InventarioActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // TEXTOS
    // =====================================================

    private void actualizarTextos(
            double diesel,
            double corriente,
            double extra) {

        double total =
                diesel + corriente + extra;

        txtInventarioDiesel.setText(
                "Diesel: " + diesel + " gal"
        );

        txtInventarioCorriente.setText(
                "Corriente: " + corriente + " gal"
        );

        txtInventarioExtra.setText(
                "Extra: " + extra + " gal"
        );

        txtInventarioTotal.setText(
                "Total: " + total + " gal"
        );
    }

    // =====================================================
    // TOOLBAR
    // =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_toolbar_principal,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Gestión de inventario",
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
}