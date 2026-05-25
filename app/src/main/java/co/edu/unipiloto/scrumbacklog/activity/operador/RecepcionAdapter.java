package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.content.Intent;
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

public class RecepcionAdapter extends BaseAdapter {

    private Context context;
    private List<Pedido> listaPedidos;

    public RecepcionAdapter(
            Context context,
            List<Pedido> listaPedidos) {

        this.context = context;
        this.listaPedidos = listaPedidos;
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
        return listaPedidos.get(position).getIdPedido();
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.item_recepcion,
                            parent,
                            false
                    );
        }

        TextView tvInfo =
                convertView.findViewById(R.id.tvInfoRecepcion);

        Button btnConfirmar =
                convertView.findViewById(R.id.btnConfirmar);

        Button btnVolver =
                convertView.findViewById(R.id.btnVolver);

        Pedido pedido = listaPedidos.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido() +
                        "\nUbicación: " + pedido.getUbicacion() +
                        "\nCombustible: " + pedido.getCombustible() +
                        "\nCantidad: " + pedido.getCantidad() +
                        "\nFecha entrega: " + pedido.getFecha()
        );

        btnConfirmar.setOnClickListener(v -> {

            ApiService apiService =
                    ApiClient.getClient().create(ApiService.class);

            apiService.recibirPedido(
                    pedido.getIdPedido()
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                context,
                                "Recepción confirmada",
                                Toast.LENGTH_SHORT
                        ).show();

                        listaPedidos.remove(position);
                        notifyDataSetChanged();

                        Intent intent =
                                new Intent(
                                        context,
                                        InventarioActivity.class
                                );

                        context.startActivity(intent);

                    } else {

                        Toast.makeText(
                                context,
                                "Error al confirmar recepción",
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