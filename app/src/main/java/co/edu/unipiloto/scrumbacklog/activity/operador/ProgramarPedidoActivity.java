package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Combustible;
import co.edu.unipiloto.scrumbacklog.model.Pedido;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.Menu;
import android.view.MenuItem;

public class ProgramarPedidoActivity extends AppCompatActivity {

    private EditText etCantidad, etFecha;
    private Button btnGuardar, btnFecha;
    private Spinner spUbicacion, spCombustible;

    private ApiService apiService;

    private int idUbicacionUsuario;

    private List<Combustible> listaCombustibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programar_pedido);

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Programar Pedido");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // =====================================================
        // API
        // =====================================================

        apiService = ApiClient.getClient().create(ApiService.class);

        // =====================================================
        // SESIÓN
        // =====================================================

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);

        idUbicacionUsuario = prefs.getInt("id_ubicacion", -1);

        // =====================================================
        // COMPONENTES
        // =====================================================

        spUbicacion = findViewById(R.id.spUbicacion);
        spCombustible = findViewById(R.id.spCombustible);

        etCantidad = findViewById(R.id.etCantidad);
        etFecha = findViewById(R.id.etFecha);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnFecha = findViewById(R.id.btnSeleccionarFecha);

        // =====================================================
        // CARGAR DATOS
        // =====================================================

        cargarUbicacionUsuario();
        cargarCombustibles();

        // =====================================================
        // EVENTOS
        // =====================================================

        btnFecha.setOnClickListener(v -> mostrarDatePicker());

        btnGuardar.setOnClickListener(v -> guardarPedido());
    }

    // =====================================================
    // BOTÓN ATRÁS TOOLBAR
    // =====================================================

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // =====================================================
    // MENÚ TOOLBAR
    // =====================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    // =====================================================
    // OPCIONES TOOLBAR
    // =====================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Permite programar pedidos de combustible",
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
    // CERRAR SESIÓN
    // =====================================================

    private void cerrarSesion() {

        SharedPreferences prefs =
                getSharedPreferences("sesion", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.apply();

        Intent intent =
                new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // =====================================================
    // CARGAR UBICACIÓN
    // =====================================================

    private void cargarUbicacionUsuario() {

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<String> nombres = new ArrayList<>();

                            for (Ubicacion u : response.body()) {

                                if (u.getIdUbicacion() == idUbicacionUsuario) {

                                    nombres.add(u.getNombre());
                                    break;
                                }
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            ProgramarPedidoActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            nombres
                                    );

                            spUbicacion.setAdapter(adapter);

                            spUbicacion.setEnabled(false);
                            spUbicacion.setClickable(false);
                            spUbicacion.setFocusable(false);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ProgramarPedidoActivity.this,
                                "Error cargando ubicación",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
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

                            listaCombustibles = response.body();

                            List<String> nombres = new ArrayList<>();

                            for (Combustible c : listaCombustibles) {
                                nombres.add(c.getNombre());
                            }

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(
                                            ProgramarPedidoActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            nombres
                                    );

                            spCombustible.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Combustible>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ProgramarPedidoActivity.this,
                                "Error cargando combustibles",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // DATE PICKER
    // =====================================================

    private void mostrarDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    String fecha =
                            year + "-" +
                                    (month + 1) + "-" +
                                    day;

                    etFecha.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // =====================================================
    // GUARDAR PEDIDO
    // =====================================================

    private void guardarPedido() {

        String cantidadTexto =
                etCantidad.getText().toString().trim();

        String fecha =
                etFecha.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese cantidad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (fecha.isEmpty()) {

            Toast.makeText(
                    this,
                    "Seleccione una fecha",
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

        Combustible combustibleSeleccionado =
                listaCombustibles.get(
                        spCombustible.getSelectedItemPosition()
                );

        Pedido pedido = new Pedido();

        pedido.setIdUbicacion(idUbicacionUsuario);

        pedido.setIdCombustible(
                combustibleSeleccionado.getIdCombustible()
        );

        pedido.setCantidad(cantidad);

        pedido.setFecha(fecha);

        apiService.crearPedido(pedido)
                .enqueue(new Callback<Pedido>() {

                    @Override
                    public void onResponse(
                            Call<Pedido> call,
                            Response<Pedido> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    ProgramarPedidoActivity.this,
                                    "Pedido programado correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            limpiarCampos();

                        } else {

                            Toast.makeText(
                                    ProgramarPedidoActivity.this,
                                    "Error al guardar pedido",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Pedido> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ProgramarPedidoActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // =====================================================
    // LIMPIAR CAMPOS
    // =====================================================

    private void limpiarCampos() {

        etCantidad.setText("");

        etFecha.setText("");

        spCombustible.setSelection(0);
    }
}