package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoAdapter extends BaseAdapter {

    private Context context;

    private List<Pedido> lista;

    private ApiService apiService;

    public PedidoAdapter(
            Context context,
            List<Pedido> lista,
            ApiService apiService
    ) {

        this.context = context;
        this.lista = lista;
        this.apiService = apiService;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getIdPedido();
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

        Pedido pedido =
                lista.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido()
                        + "\nUbicación: "
                        + pedido.getUbicacion()
                        + "\nCombustible: "
                        + pedido.getCombustible()
                        + "\nCantidad: "
                        + pedido.getCantidad()
                        + "\nFecha: "
                        + pedido.getFecha()
        );

        btnAceptar.setOnClickListener(v -> {

            apiService.aceptarPedido(
                    pedido.getIdPedido()
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response
                ) {

                    Toast.makeText(
                            context,
                            "Pedido aceptado",
                            Toast.LENGTH_SHORT
                    ).show();

                    lista.remove(position);

                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t
                ) {

                    Toast.makeText(
                            context,
                            "Error backend",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

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
                        Response<Void> response
                ) {

                    Toast.makeText(
                            context,
                            "Pedido cancelado",
                            Toast.LENGTH_SHORT
                    ).show();

                    lista.remove(position);

                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(
                        Call<Void> call,
                        Throwable t
                ) {

                    Toast.makeText(
                            context,
                            "Error backend",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        return convertView;
    }
}