package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class PedidoAdapter extends BaseAdapter {

    private Context context;

    private List<Pedido> listaPedidos;

    private ApiService apiService;

    public PedidoAdapter(
            Context context,
            List<Pedido> listaPedidos) {

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

        return listaPedidos
                .get(position)
                .getIdPedido();
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {

            convertView =
                    LayoutInflater.from(context)
                            .inflate(
                                    R.layout.item_pedido,
                                    parent,
                                    false
                            );
        }

        TextView tvInfo =
                convertView.findViewById(R.id.tvInfo);

        EditText etMotivo =
                convertView.findViewById(R.id.etMotivo);

        Button btnAceptar =
                convertView.findViewById(R.id.btnAceptar);

        Button btnCancelar =
                convertView.findViewById(R.id.btnCancelar);

        Button btnVolver =
                convertView.findViewById(R.id.btnVolver);

        Pedido pedido =
                listaPedidos.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido() +
                        "\nUbicación: " + pedido.getUbicacion() +
                        "\nCombustible: " + pedido.getCombustible() +
                        "\nCantidad: " + pedido.getCantidad() +
                        "\nFecha: " + pedido.getFecha()
        );

        // =====================================
        // ACEPTAR
        // =====================================

        btnAceptar.setOnClickListener(v -> {

            apiService.aceptarPedido(
                    pedido.getIdPedido()
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                context,
                                "Pedido aceptado",
                                Toast.LENGTH_SHORT
                        ).show();

                        listaPedidos.remove(position);

                        notifyDataSetChanged();

                    } else {

                        Toast.makeText(
                                context,
                                "Error aceptando pedido",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t) {

                    Toast.makeText(
                            context,
                            "Error conexión backend",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        // =====================================
        // CANCELAR
        // =====================================

        btnCancelar.setOnClickListener(v -> {

            String motivo =
                    etMotivo.getText()
                            .toString()
                            .trim();

            if (motivo.isEmpty()) {

                Toast.makeText(
                        context,
                        "Ingrese motivo",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            apiService.cancelarPedido(
                    pedido.getIdPedido(),
                    motivo
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                context,
                                "Pedido cancelado",
                                Toast.LENGTH_SHORT
                        ).show();

                        listaPedidos.remove(position);

                        notifyDataSetChanged();

                    } else {

                        Toast.makeText(
                                context,
                                "Error cancelando pedido",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t) {

                    Toast.makeText(
                            context,
                            "Error conexión backend",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        // =====================================
        // VOLVER
        // =====================================

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