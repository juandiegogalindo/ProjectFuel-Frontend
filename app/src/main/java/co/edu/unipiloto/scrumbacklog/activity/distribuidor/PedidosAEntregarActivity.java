package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosAEntregarActivity
        extends AppCompatActivity {

    private ListView listView;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_pedidos_aentregar
        );

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle("Pedidos a Entregar");

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        listView =
                findViewById(R.id.listViewEntregas);

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        cargarPedidos();
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    private void cargarPedidos() {

        apiService.obtenerPedidosAceptados()
                .enqueue(new Callback<List<Pedido>>() {

                    @Override
                    public void onResponse(
                            Call<List<Pedido>> call,
                            Response<List<Pedido>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            PedidoEntregaAdapter adapter =
                                    new PedidoEntregaAdapter(
                                            PedidosAEntregarActivity.this,
                                            response.body(),
                                            apiService
                                    );

                            listView.setAdapter(adapter);

                        } else {

                            Toast.makeText(
                                    PedidosAEntregarActivity.this,
                                    "Error cargando pedidos",
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
                                PedidosAEntregarActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

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