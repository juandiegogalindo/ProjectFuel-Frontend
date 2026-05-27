package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;

import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialOperadorActivity
        extends AppCompatActivity {

    private ListView listView;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_historial_operador
        );

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle("Historial Operador");

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
        // LISTVIEW
        // =====================================================

        listView =
                findViewById(R.id.listViewMovimientos);

        // =====================================================
        // SESION
        // =====================================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        int idUbicacion =
                prefs.getInt(
                        "id_ubicacion",
                        -1
                );

        cargarMovimientos(idUbicacion);
    }

    // =====================================================
    // CARGAR MOVIMIENTOS
    // =====================================================

    private void cargarMovimientos(
            int idUbicacion
    ) {

        apiService.obtenerMovimientosPorUbicacion(
                        idUbicacion
                )
                .enqueue(
                        new Callback<List<MovimientoResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<MovimientoResponse>> call,
                                    Response<List<MovimientoResponse>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    MovimientoAdapter adapter =
                                            new MovimientoAdapter(
                                                    HistorialOperadorActivity.this,
                                                    response.body()
                                            );

                                    listView.setAdapter(adapter);

                                } else {

                                    Toast.makeText(
                                            HistorialOperadorActivity.this,
                                            "No hay movimientos",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<List<MovimientoResponse>> call,
                                    Throwable t
                            ) {

                                Toast.makeText(
                                        HistorialOperadorActivity.this,
                                        "Error conexión backend",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}