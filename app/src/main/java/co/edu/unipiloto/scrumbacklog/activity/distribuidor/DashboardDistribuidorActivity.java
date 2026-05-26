package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import co.edu.unipiloto.scrumbacklog.R;

import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;

import co.edu.unipiloto.scrumbacklog.model.DashboardDistribuidor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardDistribuidorActivity
        extends AppCompatActivity {

    private TextView tvPendientes;
    private TextView tvAceptados;
    private TextView tvEntregados;
    private TextView tvCancelados;
    private TextView tvGalones;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_dashboard_distribuidor
        );

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle("Dashboard Distribuidor");

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        tvPendientes =
                findViewById(R.id.tvPendientes);

        tvAceptados =
                findViewById(R.id.tvAceptados);

        tvEntregados =
                findViewById(R.id.tvEntregados);

        tvCancelados =
                findViewById(R.id.tvCancelados);

        tvGalones =
                findViewById(R.id.tvGalones);

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        cargarDashboard();
    }

    private void cargarDashboard() {

        apiService.obtenerDashboardDistribuidor()
                .enqueue(new Callback<DashboardDistribuidor>() {

                    @Override
                    public void onResponse(
                            Call<DashboardDistribuidor> call,
                            Response<DashboardDistribuidor> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            DashboardDistribuidor d =
                                    response.body();

                            tvPendientes.setText(
                                    String.valueOf(
                                            d.getPendientes()
                                    )
                            );

                            tvAceptados.setText(
                                    String.valueOf(
                                            d.getAceptados()
                                    )
                            );

                            tvEntregados.setText(
                                    String.valueOf(
                                            d.getEntregados()
                                    )
                            );

                            tvCancelados.setText(
                                    String.valueOf(
                                            d.getCancelados()
                                    )
                            );

                            tvGalones.setText(
                                    d.getTotalGalones()
                                            + " gal"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<DashboardDistribuidor> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                DashboardDistribuidorActivity.this,
                                "Error cargando dashboard",
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
}