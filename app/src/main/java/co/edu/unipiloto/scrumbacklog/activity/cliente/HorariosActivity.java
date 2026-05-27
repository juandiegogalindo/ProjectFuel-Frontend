package co.edu.unipiloto.scrumbacklog.activity.cliente;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorariosActivity extends AppCompatActivity {

    private ListView listView;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Horarios Estaciones");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        apiService = ApiClient.getClient().create(ApiService.class);
        listView = findViewById(R.id.listViewHorarios);

        cargarHorarios();
    }

    private void cargarHorarios() {

        apiService.obtenerUbicaciones().enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(Call<List<Ubicacion>> call, Response<List<Ubicacion>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Ubicacion> lista = response.body();

                            HorarioAdapter adapter = new HorarioAdapter(HorariosActivity.this, lista);

                            listView.setAdapter(adapter);

                        } else {

                            Toast.makeText(HorariosActivity.this, "Error obteniendo horarios", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ubicacion>> call, Throwable t) {
                        Toast.makeText(HorariosActivity.this, "Error conexión backend", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_info) {

            Toast.makeText(this, "Permite ver las estaciones en servicio", Toast.LENGTH_SHORT).show();
            return true;

        } else if (item.getItemId() == R.id.action_logout) {

            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}