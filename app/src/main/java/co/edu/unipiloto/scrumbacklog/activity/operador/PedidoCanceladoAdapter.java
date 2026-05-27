package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

public class PedidoCanceladoAdapter extends BaseAdapter {

    private Context context;
    private List<Pedido> lista;

    public PedidoCanceladoAdapter(
            Context context,
            List<Pedido> lista) {

        this.context = context;
        this.lista = lista;
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
            ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(
                            R.layout.item_pedido_cancelado,
                            parent,
                            false
                    );
        }

        Pedido pedido = lista.get(position);

        TextView txtPedido =
                convertView.findViewById(R.id.tvInfoPedido);

        txtPedido.setText(
                "Pedido #" + pedido.getIdPedido() +
                        "\nCombustible: " + pedido.getCombustible() +
                        "\nCantidad: " + pedido.getCantidad() +
                        "\nMotivo: " + pedido.getMotivoCancelacion()
        );

        return convertView;
    }
}