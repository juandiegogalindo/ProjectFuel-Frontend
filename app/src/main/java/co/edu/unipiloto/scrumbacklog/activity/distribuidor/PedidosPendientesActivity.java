package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.database.DatabaseHelper;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosPendientesActivity extends AppCompatActivity {

    private ListView listView;
    private ApiService apiService;

    private String rol;

    private int idUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ===== TOOLBAR =====
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pedidos Pendientes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // ===================
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_pendientes);

        listView = findViewById(R.id.listViewPedidos);

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

        if (rol.equalsIgnoreCase("CLIENTE")) {

            finish();
            return;
        }

        cargarPedidos();
    }

    // ===== BOTÓN ← TOOLBAR =====
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ===== MENÚ TOOLBAR =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pedido_pendiente, menu);
        return true;
    }

    // ===== ACCIONES TOOLBAR =====
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {
            Toast.makeText(this, "¿Que pedidos pendientes tengo? 😂", Toast.LENGTH_SHORT).show();
            return true;

        } else if (item.getItemId() == R.id.action_logout) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===== CERRAR SESIÓN =====
    private void cerrarSesion() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void cargarPedidos() {

        Call<List<Pedido>> call;

        // =====================================
        // OPERADOR
        // =====================================

        if (rol.equalsIgnoreCase("OPERADOR")) {

            call = apiService.obtenerPedidosPendientesPorUbicacion(
                    idUbicacion
            );

        } else {

            // ADMIN Y DISTRIBUIDOR
            call = apiService.obtenerPedidosPendientes();
        }

        call.enqueue(new Callback<List<Pedido>>() {

            @Override
            public void onResponse(
                    Call<List<Pedido>> call,
                    Response<List<Pedido>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    PedidoAdapter adapter =
                            new PedidoAdapter(
                                    PedidosPendientesActivity.this,
                                    response.body()
                            );

                    listView.setAdapter(adapter);

                } else {

                    Toast.makeText(
                            PedidosPendientesActivity.this,
                            "Error obteniendo pedidos",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<List<Pedido>> call,
                    Throwable t) {

                Toast.makeText(
                        PedidosPendientesActivity.this,
                        "Error conexión backend",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
