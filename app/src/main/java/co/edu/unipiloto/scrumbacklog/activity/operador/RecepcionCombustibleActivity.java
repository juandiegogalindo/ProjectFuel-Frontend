package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecepcionCombustibleActivity
        extends AppCompatActivity {

    private ListView listView;

    private int idUbicacionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recepcion_combustible
        );

        // =====================================
        // TOOLBAR
        // =====================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle(
                    "Recepción Combustible"
            );

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // =====================================
        // LISTVIEW
        // =====================================

        listView =
                findViewById(R.id.listViewRecepcion);

        // =====================================
        // SESION
        // =====================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        idUbicacionUsuario =
                prefs.getInt(
                        "id_ubicacion",
                        -1
                );

        cargarPedidos();
    }

    // =====================================
    // CARGAR PEDIDOS
    // =====================================

    private void cargarPedidos() {

        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.obtenerPedidosEntregados(
                idUbicacionUsuario
        ).enqueue(new Callback<List<Pedido>>() {

            @Override
            public void onResponse(
                    Call<List<Pedido>> call,
                    Response<List<Pedido>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    RecepcionAdapter adapter =
                            new RecepcionAdapter(
                                    RecepcionCombustibleActivity.this,
                                    response.body()
                            );

                    listView.setAdapter(adapter);

                } else {

                    Toast.makeText(
                            RecepcionCombustibleActivity.this,
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
                        RecepcionCombustibleActivity.this,
                        "Error conexión backend",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    // =====================================
    // TOOLBAR BACK
    // =====================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    // =====================================
    // MENU
    // =====================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_recepcion,
                menu
        );

        return true;
    }

    // =====================================
    // ACCIONES MENU
    // =====================================

    @Override
    public boolean onOptionsItemSelected(
            MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Listado de pedidos entregados",
                    Toast.LENGTH_SHORT
            ).show();

            return true;

        } else if (item.getItemId()
                == R.id.action_refresh) {

            cargarPedidos();

            Toast.makeText(
                    this,
                    "Lista actualizada",
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