package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;

public class MovimientoAdapter
        extends ArrayAdapter<MovimientoResponse> {

    public MovimientoAdapter(
            Context context,
            List<MovimientoResponse> lista
    ) {

        super(context, 0, lista);
    }

    @NonNull
    @Override
    public View getView(
            int position,
            View convertView,
            @NonNull ViewGroup parent
    ) {

        if (convertView == null) {

            convertView =
                    LayoutInflater.from(getContext())
                            .inflate(
                                    R.layout.item_movimiento,
                                    parent,
                                    false
                            );
        }

        MovimientoResponse movimiento =
                getItem(position);

        TextView txtTipo =
                convertView.findViewById(R.id.txtTipo);

        TextView txtCombustible =
                convertView.findViewById(R.id.txtCombustible);

        TextView txtGalones =
                convertView.findViewById(R.id.txtGalones);

        TextView txtTotal =
                convertView.findViewById(R.id.txtTotal);

        TextView txtFecha =
                convertView.findViewById(R.id.txtFecha);

        if (movimiento != null) {

            txtTipo.setText(
                    "Movimiento: "
                            + movimiento.getTipoMovimiento()
            );

            txtCombustible.setText(
                    "Combustible: "
                            + movimiento.getCombustible()
            );

            txtGalones.setText(
                    "Galones: "
                            + movimiento.getGalones()
            );

            txtTotal.setText(
                    "Total: $"
                            + movimiento.getTotal()
            );

            txtFecha.setText(
                    "Fecha: "
                            + movimiento.getFecha()
            );
        }

        return convertView;
    }
}