package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;

public class HorarioAdapter extends BaseAdapter {

    private Context context;

    private List<Ubicacion> listaUbicaciones;

    public HorarioAdapter(Context context, List<Ubicacion> listaUbicaciones) {
        this.context = context;
        this.listaUbicaciones = listaUbicaciones;
    }

    @Override
    public int getCount() {
        return listaUbicaciones.size();
    }

    @Override
    public Object getItem(int position) {
        return listaUbicaciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaUbicaciones.get(position).getIdUbicacion();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.item_horario, parent, false);
        }

        TextView tvNombre = convertView.findViewById(R.id.tvNombre);

        TextView tvHorario = convertView.findViewById(R.id.tvHorario);

        TextView tvEstado = convertView.findViewById(R.id.tvEstado);

        Ubicacion ubicacion = listaUbicaciones.get(position);

        tvNombre.setText(ubicacion.getNombre());

        tvHorario.setText("Horario: " + ubicacion.getHoraApertura() + " - " + ubicacion.getHoraCierre()
        );

        String estado = ubicacion.getEstado();

        if (estado != null && estado.equalsIgnoreCase("MANTENIMIENTO")) {

            tvEstado.setText("🔴 En mantenimiento");

            tvEstado.setTextColor(Color.RED);
            tvNombre.setTextColor(Color.RED);

        } else {

            tvEstado.setText("🟢 Activa");
            tvEstado.setTextColor(Color.parseColor("#1B5E20"));
            tvNombre.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}