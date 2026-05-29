package co.edu.unipiloto.scrumbacklog.activity.distribuidor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.scrumbacklog.R;
import co.edu.unipiloto.scrumbacklog.activity.logIn.LoginActivity;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiClient;
import co.edu.unipiloto.scrumbacklog.api.apiconfiguracion.ApiService;
import co.edu.unipiloto.scrumbacklog.model.Pedido;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutaDistribucionActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ApiService apiService;

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;

    private LatLng ubicacionActual;

    private final List<LatLng> puntosPedidos =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_ruta_distribucion
        );

        // =====================================================
        // API
        // =====================================================

        apiService =
                ApiClient.getClient()
                        .create(ApiService.class);

        // =====================================================
        // UBICACION
        // =====================================================

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(this);

        // =====================================================
        // TOOLBAR
        // =====================================================

        Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle("Ruta Inteligente");

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        // =====================================================
        // MAPA
        // =====================================================

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.mapRuta);

        if (mapFragment != null) {

            mapFragment.getMapAsync(this);
        }
    }

    // =====================================================
    // MAP READY
    // =====================================================

    @Override
    public void onMapReady(
            GoogleMap googleMap
    ) {

        mMap = googleMap;

        activarUbicacion();

        iniciarActualizacionesUbicacion();

        mMap.getUiSettings()
                .setZoomControlsEnabled(true);

        mMap.getUiSettings()
                .setCompassEnabled(true);

        mMap.getUiSettings()
                .setMapToolbarEnabled(true);

        cargarRutaDistribucion();

        LatLng bogota =
                new LatLng(
                        4.7110,
                        -74.0721
                );

        mMap.moveCamera(
                CameraUpdateFactory
                        .newLatLngZoom(
                                bogota,
                                11
                        )
        );
    }

    // =====================================================
    // ACTIVAR UBICACION
    // =====================================================

    private void activarUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings()
                .setMyLocationButtonEnabled(true);
    }

    // =====================================================
    // ACTUALIZACIONES UBICACION
    // =====================================================

    private void iniciarActualizacionesUbicacion() {

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        5000
                ).build();

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            @NonNull LocationResult locationResult
                    ) {

                        super.onLocationResult(locationResult);

                        for (Location location
                                : locationResult.getLocations()) {

                            ubicacionActual =
                                    new LatLng(
                                            location.getLatitude(),
                                            location.getLongitude()
                                    );

                            if (!puntosPedidos.isEmpty()) {

                                generarRutaCompleta();
                            }
                        }
                    }
                };

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }

    // =====================================================
    // CARGAR PEDIDOS
    // =====================================================

    private void cargarRutaDistribucion() {

        apiService.obtenerPedidosAceptados()
                .enqueue(new Callback<List<Pedido>>() {

                    @Override
                    public void onResponse(
                            Call<List<Pedido>> call,
                            Response<List<Pedido>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            obtenerUbicacionesPedidos(
                                    response.body()
                            );

                        } else {

                            Toast.makeText(
                                    RutaDistribucionActivity.this,
                                    "No hay pedidos aceptados",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Pedido>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                RutaDistribucionActivity.this,
                                "Error conexión backend",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =====================================================
    // OBTENER UBICACIONES PEDIDOS
    // =====================================================

    private void obtenerUbicacionesPedidos(
            List<Pedido> pedidos
    ) {

        apiService.obtenerUbicaciones()
                .enqueue(new Callback<List<Ubicacion>>() {

                    @Override
                    public void onResponse(
                            Call<List<Ubicacion>> call,
                            Response<List<Ubicacion>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Ubicacion> ubicaciones =
                                    response.body();

                            puntosPedidos.clear();

                            for (Pedido pedido : pedidos) {

                                for (Ubicacion ubicacion
                                        : ubicaciones) {

                                    if (pedido.getIdUbicacion()
                                            .equals(
                                                    ubicacion.getIdUbicacion()
                                            )) {

                                        if (ubicacion.getLatitud() == null
                                                || ubicacion.getLongitud() == null) {

                                            continue;
                                        }

                                        LatLng punto =
                                                new LatLng(
                                                        ubicacion.getLatitud(),
                                                        ubicacion.getLongitud()
                                                );

                                        puntosPedidos.add(punto);

                                        mMap.addMarker(

                                                new MarkerOptions()
                                                        .position(punto)
                                                        .title(
                                                                ubicacion.getNombre()
                                                        )
                                                        .snippet(
                                                                "Pedido #"
                                                                        + pedido.getIdPedido()
                                                                        + "\nCombustible: "
                                                                        + pedido.getCombustible()
                                                                        + "\nCantidad: "
                                                                        + pedido.getCantidad()
                                                        )
                                                        .icon(
                                                                BitmapDescriptorFactory
                                                                        .defaultMarker(
                                                                                BitmapDescriptorFactory.HUE_AZURE
                                                                        )
                                                        )
                                        );
                                    }
                                }
                            }

                            if (ubicacionActual != null) {

                                generarRutaCompleta();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Ubicacion>> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                RutaDistribucionActivity.this,
                                "Error obteniendo ubicaciones",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =====================================================
    // GENERAR RUTA COMPLETA
    // =====================================================

    private void generarRutaCompleta() {

        if (ubicacionActual == null) {

            return;
        }

        if (puntosPedidos.isEmpty()) {

            return;
        }

        LatLng origen = ubicacionActual;

        for (LatLng destino : puntosPedidos) {

            dibujarRuta(
                    origen,
                    destino
            );

            origen = destino;
        }
    }

    // =====================================================
    // DIBUJAR RUTA REAL
    // =====================================================

    private void dibujarRuta(
            LatLng origen,
            LatLng destino
    ) {

        String url =
                "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin="
                        + origen.latitude
                        + ","
                        + origen.longitude
                        + "&destination="
                        + destino.latitude
                        + ","
                        + destino.longitude
                        + "&mode=driving"
                        + "&key="
                        + getString(R.string.google_maps_key);

        RequestQueue queue =
                Volley.newRequestQueue(this);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,

                        response -> {

                            try {

                                String status =
                                        response.getString(
                                                "status"
                                        );

                                if (!status.equals("OK")) {

                                    Toast.makeText(
                                            this,
                                            "Error ruta: " + status,
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                JSONArray routes =
                                        response.getJSONArray(
                                                "routes"
                                        );

                                JSONObject route =
                                        routes.getJSONObject(0);

                                JSONObject polyline =
                                        route.getJSONObject(
                                                "overview_polyline"
                                        );

                                String points =
                                        polyline.getString(
                                                "points"
                                        );

                                List<LatLng> lista =
                                        PolyUtil.decode(points);

                                mMap.addPolyline(

                                        new PolylineOptions()
                                                .addAll(lista)
                                                .width(14)
                                                .color(Color.BLUE)
                                                .geodesic(false)
                                );

                            } catch (Exception e) {

                                e.printStackTrace();

                                Toast.makeText(
                                        this,
                                        "Error dibujando ruta",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> {

                            error.printStackTrace();

                            Toast.makeText(
                                    this,
                                    "Error conexión Google Directions",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    // =====================================================
    // PERMISOS
    // =====================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode
                == LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                activarUbicacion();

                iniciarActualizacionesUbicacion();
            }
        }
    }

    // =====================================================
    // DESTROY
    // =====================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (locationCallback != null) {

            fusedLocationClient
                    .removeLocationUpdates(
                            locationCallback
                    );
        }
    }

    // =====================================================
    // BACK TOOLBAR
    // =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void cerrarSesion() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "sesion",
                        MODE_PRIVATE
                );

        prefs.edit().clear().apply();

        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}