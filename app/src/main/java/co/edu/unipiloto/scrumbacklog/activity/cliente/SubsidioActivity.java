package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
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

        etCodigo =
                findViewById(R.id.etCodigoSubsidio);

        btnSolicitar =
                findViewById(R.id.btnSolicitarSubsidio);

        cardResultado =
                findViewById(R.id.cardResultado);

        tvEstado =
                findViewById(R.id.tvEstadoSubsidio);

        tvMensaje =
                findViewById(R.id.tvMensajeSubsidio);

        tvMonto =
                findViewById(R.id.tvMontoSubsidio);

        tvVencimiento =
                findViewById(R.id.tvVencimientoSubsidio);

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        btnSolicitar.setOnClickListener(v -> {

            String codigo =
                    etCodigo.getText()
                            .toString()
                            .trim();

            if (codigo.isEmpty()) {

                Toast.makeText(
                        this,
                        "Ingrese un código",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            validarSubsidio(codigo);
        });
    }

    // =====================================================
    // VALIDAR SUBSIDIO
    // =====================================================

    private void validarSubsidio(String codigo) {

        SubsidioRequest request =
                new SubsidioRequest(codigo);

        apiService.validarSubsidio(request)
                .enqueue(new Callback<SubsidioResponse>() {

                    @Override
                    public void onResponse(
                            Call<SubsidioResponse> call,
                            Response<SubsidioResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            SubsidioResponse subsidio =
                                    response.body();

                            mostrarResultado(subsidio);

                        } else {

                            Toast.makeText(
                                    SubsidioActivity.this,
                                    "Error validando subsidio",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<SubsidioResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                SubsidioActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =====================================================
    // MOSTRAR RESULTADO
    // =====================================================

    private void mostrarResultado(
            SubsidioResponse subsidio
    ) {

        cardResultado.setVisibility(View.VISIBLE);

        tvMensaje.setText(
                subsidio.getMensaje()
        );

        tvMonto.setText(
                "$ " + subsidio.getMonto()
        );

        tvEstado.setText(
                subsidio.getEstado()
        );

        tvVencimiento.setText(
                subsidio.getFechaVencimiento()
        );

        // COLOR ESTADO
        if (subsidio.isAprobado()) {

            tvEstado.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_green_dark
                    )
            );

        } else {

            tvEstado.setTextColor(
                    getResources().getColor(
                            android.R.color.holo_red_dark
                    )
            );
        }
    }
}