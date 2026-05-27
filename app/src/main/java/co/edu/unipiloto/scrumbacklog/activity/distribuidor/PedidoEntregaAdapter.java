package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoEntregaAdapter extends BaseAdapter {

    private Context context;

    private List<Pedido> lista;

    private ApiService apiService;

    public PedidoEntregaAdapter(
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
        );

        tvContador.setText("Pendiente");

        btnCompletar.setEnabled(false);

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

        btnCompletar.setOnClickListener(v -> {

            String fechaActual =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    ).format(new Date());

            apiService.entregarPedido(
                    pedido.getIdPedido(),
                    fechaActual
            ).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(
                        Call<Void> call,
                        Response<Void> response
                ) {

                    Toast.makeText(
                            context,
                            "Pedido entregado",
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