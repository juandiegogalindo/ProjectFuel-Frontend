package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoEntregaAdapter extends BaseAdapter {

    private Context context;

    private List<Pedido> listaPedidos;

    private ApiService apiService;

    public PedidoEntregaAdapter(
            Context context,
            List<Pedido> listaPedidos
    ) {

        this.context = context;
        this.listaPedidos = listaPedidos;

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);
    }

    @Override
    public int getCount() {

        return listaPedidos.size();
    }

    @Override
    public Object getItem(int position) {

        return listaPedidos.get(position);
    }

    @Override
    public long getItemId(int position) {

        return listaPedidos.get(position)
                .getIdPedido();
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        if (convertView == null) {

            convertView =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.item_pedido_entrega,
                                    parent,
                                    false
                            );
        }

        TextView tvInfo =
                convertView.findViewById(
                        R.id.tvInfoEntrega
                );

        TextView tvContador =
                convertView.findViewById(
                        R.id.tvContador
                );

        Button btnIniciar =
                convertView.findViewById(
                        R.id.btnIniciarEntrega
                );

        Button btnCompletar =
                convertView.findViewById(
                        R.id.btnCompletarEntrega
                );

        Button btnVolver =
                convertView.findViewById(
                        R.id.btnVolver
                );

        Pedido pedido =
                listaPedidos.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido() +
                        "\nUbicación: "
                        + pedido.getUbicacion() +
                        "\nCombustible: "
                        + pedido.getCombustible() +
                        "\nCantidad: "
                        + pedido.getCantidad()
        );

        tvContador.setText("Pendiente");

        btnCompletar.setEnabled(false);

        // =====================================================
        // INICIAR ENTREGA
        // =====================================================

        btnIniciar.setOnClickListener(v -> {

            btnIniciar.setEnabled(false);

            int tiempoTotal = 3000;

            int interval = 100;

            new CountDownTimer(
                    tiempoTotal,
                    interval
            ) {

                double restante =
                        pedido.getCantidad();

                @Override
                public void onTick(
                        long millisUntilFinished
                ) {

                    restante -= (
                            pedido.getCantidad()
                                    / (tiempoTotal / interval)
                    );

                    if (restante < 0) {
                        restante = 0;
                    }

                    tvContador.setText(
                            "Surtido: "
                                    + (int) restante
                                    + " galones"
                    );
                }

                @Override
                public void onFinish() {

                    tvContador.setText(
                            "Completado"
                    );

                    btnCompletar.setEnabled(true);
                }

            }.start();
        });

        // =====================================================
        // COMPLETAR ENTREGA
        // =====================================================

        btnCompletar.setOnClickListener(v -> {

            apiService.entregarPedido(
                    pedido.getIdPedido()
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response
                ) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                context,
                                "Entrega enviada a: "
                                        + pedido.getUbicacion(),
                                Toast.LENGTH_SHORT
                        ).show();

                        listaPedidos.remove(position);

                        notifyDataSetChanged();

                    } else {

                        Toast.makeText(
                                context,
                                "Error al completar entrega",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t
                ) {

                    Toast.makeText(
                            context,
                            "Error conexión backend",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        // =====================================================
        // VOLVER
        // =====================================================

        btnVolver.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            MainActivity.class
                    );

            context.startActivity(intent);
        });

        return convertView;
    }
}