package co.edu.unipiloto.scrumbacklog.activity.operador;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.scrumbacklog.R;

import co.edu.unipiloto.scrumbacklog.api.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.ApiService;
import co.edu.unipiloto.scrumbacklog.api.MovimientoRequest;

import co.edu.unipiloto.scrumbacklog.model.Pedido;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoInventarioAdapter extends BaseAdapter {

    private Context context;

    private List<Pedido> lista;

    private int idUbicacion;

    private Activity activity;

    public PedidoInventarioAdapter(
            Context context,
            List<Pedido> lista,
            int idUbicacion,
            Activity activity
    ) {

        this.context = context;
        this.lista = lista;
        this.idUbicacion = idUbicacion;
        this.activity = activity;
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

            convertView = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.item_pedido_inventario,
                            parent,
                            false
                    );
        }

        TextView tvInfo =
                convertView.findViewById(R.id.tvInfoPedido);

        Button btnAgregar =
                convertView.findViewById(R.id.btnAgregarInventario);

        Pedido pedido = lista.get(position);

        tvInfo.setText(
                "Pedido #" + pedido.getIdPedido()
                        + "\nCombustible: " + pedido.getCombustible()
                        + "\nCantidad: " + pedido.getCantidad() + " gal"
        );

        btnAgregar.setOnClickListener(v -> {

            ApiService apiService =
                    ApiClient.getClient()
                            .create(ApiService.class);

            String fecha =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    ).format(new Date());

            MovimientoRequest request =
                    new MovimientoRequest();

            request.setIdCombustible(
                    pedido.getIdCombustible()
            );

            request.setIdUbicacion(
                    idUbicacion
            );

            request.setGalones(
                    pedido.getCantidad()
            );

            request.setPrecioUnitario(0.0);

            request.setFecha(fecha);

            // ======================================
            // REGISTRAR ENTRADA
            // ======================================

            apiService.registrarEntrada(request)
                    .enqueue(new Callback<>() {

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) {

                            if (response.isSuccessful()) {

                                // =========================
                                // MARCAR PEDIDO RECIBIDO
                                // =========================

                                apiService.recibirPedido(
                                        pedido.getIdPedido()
                                ).enqueue(new Callback<Void>() {

                                    @Override
                                    public void onResponse(
                                            Call<Void> call,
                                            Response<Void> response
                                    ) {

                                        Toast.makeText(
                                                context,
                                                "Inventario actualizado",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        activity.recreate();
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<Void> call,
                                            Throwable t
                                    ) {

                                        Toast.makeText(
                                                context,
                                                "Error recibiendo pedido",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });

                            } else {

                                Toast.makeText(
                                        context,
                                        "Error registrando entrada",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call call,
                                Throwable t
                        ) {

                            Toast.makeText(
                                    context,
                                    "Error conexión backend",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        return convertView;
    }
}