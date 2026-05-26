package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

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

public class HistoricoDistribuidorActivity
        extends AppCompatActivity {

    private ListView listView;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_historico_distribuidor
        );

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle("Histórico");

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        listView =
                findViewById(R.id.listViewHistorico);

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        cargarHistorico();
    }

    private void cargarHistorico() {

        apiService.obtenerTodosPedidos()
                .enqueue(new Callback<List<Pedido>>() {

                    @Override
                    public void onResponse(
                            Call<List<Pedido>> call,
                            Response<List<Pedido>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            HistoricoAdapter adapter =
                                    new HistoricoAdapter(
                                            HistoricoDistribuidorActivity.this,
                                            response.body()
                                    );

                            listView.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    HistoricoDistribuidorActivity.this,
                                    "Error cargando histórico",
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
                                HistoricoDistribuidorActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_pedido_entregar,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()
                == R.id.action_info) {

            Toast.makeText(
                    this,
                    "Historial completo de movimientos.",
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

    private void cerrarSesion() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        prefs.edit().clear().apply();

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