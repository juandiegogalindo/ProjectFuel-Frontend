package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.MainActivity;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

public class PedidoCanceladoAdapter extends BaseAdapter {

    private Context context;
    private List<Pedido> listaPedidos;

    public PedidoCanceladoAdapter(
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

            convertView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_pedido_cancelado,
                            parent,
                            false
                    );
        }

        TextView tvInfo =
                convertView.findViewById(R.id.tvInfoCancelado);

        TextView tvMotivo =
                convertView.findViewById(R.id.tvMotivo);

        Button btnReagendar =
                convertView.findViewById(R.id.btnReagendar);

        Button btnVolver =
                convertView.findViewById(R.id.btnVolver);

        Pedido pedido = listaPedidos.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido() +
                        "\nUbicación: " + pedido.getUbicacion() +
                        "\nCombustible: " + pedido.getCombustible() +
                        "\nCantidad: " + pedido.getCantidad() +
                        "\nFecha: " + pedido.getFecha()
        );

        tvMotivo.setText(
                "Motivo: " + pedido.getMotivoCancelacion()
        );

        btnReagendar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            ProgramarPedidoActivity.class
                    );

            intent.putExtra(
                    "ubicacion",
                    pedido.getUbicacion()
            );

            intent.putExtra(
                    "combustible",
                    pedido.getCombustible()
            );

            intent.putExtra(
                    "cantidad",
                    pedido.getCantidad()
            );

            context.startActivity(intent);
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