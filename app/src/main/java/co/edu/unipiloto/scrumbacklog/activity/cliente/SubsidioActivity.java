package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.api.SubsidioRequest;
import co.edu.unipiloto.scrumbacklog.api.SubsidioResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubsidioActivity extends AppCompatActivity {

    private EditText etCodigo;

    private Button btnSolicitar;

    private ApiService apiService;

    // CARD RESULTADO
    private CardView cardResultado;

    private TextView tvEstado;
    private TextView tvMensaje;
    private TextView tvMonto;
    private TextView tvVencimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subsidio);

        etCodigo = findViewById(R.id.etCodigoSubsidio);
        btnSolicitar = findViewById(R.id.btnSolicitarSubsidio);
        cardResultado = findViewById(R.id.cardResultado);
        tvEstado = findViewById(R.id.tvEstadoSubsidio);
        tvMensaje = findViewById(R.id.tvMensajeSubsidio);
        tvMonto = findViewById(R.id.tvMontoSubsidio);
        tvVencimiento = findViewById(R.id.tvVencimientoSubsidio);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnSolicitar.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();

            if (codigo.isEmpty()) {

                Toast.makeText(this, "Ingrese un código", Toast.LENGTH_LONG).show();
                return;
            }

            validarSubsidio(codigo);
        });

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Subsidio, ¿Eres Subsidiario?");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void validarSubsidio(String codigo) {

        SubsidioRequest request = new SubsidioRequest(codigo);

        apiService.validarSubsidio(request).enqueue(new Callback<SubsidioResponse>() {

                    @Override
                    public void onResponse(Call<SubsidioResponse> call, Response<SubsidioResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            SubsidioResponse subsidio = response.body();
                            mostrarResultado(subsidio);

                        } else {

                            Toast.makeText(SubsidioActivity.this, "Error validando subsidio", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SubsidioResponse> call, Throwable t) {

                        Toast.makeText(SubsidioActivity.this, "Error conexión backend", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarResultado(SubsidioResponse subsidio) {

        cardResultado.setVisibility(View.VISIBLE);

        tvMensaje.setText(subsidio.getMensaje());
        tvMonto.setText("$ " + subsidio.getMonto());
        tvEstado.setText(subsidio.getEstado());
        tvVencimiento.setText(subsidio.getFechaVencimiento());

        if (subsidio.isAprobado()) {

            tvEstado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        } else {

            tvEstado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(this, "¿Quieres obtener un subsidio? Esta es la parte indicada", Toast.LENGTH_SHORT).show();
            return true;

        } else if (item.getItemId() == R.id.action_logout) {

            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
}