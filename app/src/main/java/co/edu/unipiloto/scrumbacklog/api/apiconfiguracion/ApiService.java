package co.edu.unipiloto.scrumbacklog.api.apiconfiguracion;

import java.util.List;

import co.edu.unipiloto.scrumbacklog.api.InventarioResponse;
import co.edu.unipiloto.scrumbacklog.api.login.LoginRequest;
import co.edu.unipiloto.scrumbacklog.api.login.LoginResponse;
import co.edu.unipiloto.scrumbacklog.api.MovimientoRequest;
import co.edu.unipiloto.scrumbacklog.api.MovimientoResponse;
import co.edu.unipiloto.scrumbacklog.api.PrecioResponse;
import co.edu.unipiloto.scrumbacklog.api.login.RegisterRequest;
import co.edu.unipiloto.scrumbacklog.api.SubsidioRequest;
import co.edu.unipiloto.scrumbacklog.api.SubsidioResponse;
import co.edu.unipiloto.scrumbacklog.model.Combustible;
import co.edu.unipiloto.scrumbacklog.model.DashboardDistribuidor;
import co.edu.unipiloto.scrumbacklog.model.Pedido;
import co.edu.unipiloto.scrumbacklog.model.Ubicacion;
import co.edu.unipiloto.scrumbacklog.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("usuarios/registro")
    Call<Usuario> registrarUsuario(@Body RegisterRequest request);

    @GET("ubicaciones")
    Call<List<Ubicacion>> obtenerUbicaciones();

    @GET("ubicaciones/ciudades")
    Call<List<String>> obtenerCiudades();

    @GET("ubicaciones/zonas/{ciudad}")
    Call<List<String>> obtenerZonas(
            @Path("ciudad") String ciudad
    );

    @GET("precios/ubicacion")
    Call<PrecioResponse> obtenerPrecioPorUbicacion(
            @Query("combustible") String combustible,
            @Query("idUbicacion") int idUbicacion
    );

    @GET("precios/zona")
    Call<PrecioResponse> obtenerPrecioZona(
            @Query("combustible") String combustible,
            @Query("ciudad") String ciudad,
            @Query("localidad") String localidad
    );

    @PUT("precios/ubicacion")
    Call<Void> actualizarPrecioUbicacion(
            @Query("combustible") String combustible,
            @Query("idUbicacion") int idUbicacion,
            @Query("precio") double precio
    );

    @PUT("precios/zona")
    Call<Void> actualizarPrecioZona(
            @Query("combustible") String combustible,
            @Query("ciudad") String ciudad,
            @Query("localidad") String localidad,
            @Query("precio") double precio
    );

    @GET("inventarios/ubicacion/{idUbicacion}")
    Call<List<InventarioResponse>> obtenerInventarioPorUbicacion(
            @Path("idUbicacion") int idUbicacion
    );

    @POST("pedidos")
    Call<Pedido> crearPedido(@Body Pedido pedido);

    @GET("pedidos/entregados/{idUbicacion}")
    Call<List<Pedido>> obtenerPedidosEntregados(
            @Path("idUbicacion") int idUbicacion
    );

    @PUT("pedidos/{idPedido}/recibir")
    Call<Void> recibirPedido(
            @Path("idPedido") int idPedido
    );

    @GET("pedidos/cancelados")
    Call<List<Pedido>> obtenerPedidosCancelados();

    @GET("combustibles")
    Call<List<Combustible>> obtenerCombustibles();

    @POST("movimientos/entrada")
    Call<MovimientoResponse> registrarEntrada(
            @Body MovimientoRequest request
    );

    @POST("movimientos/salida")
    Call<MovimientoResponse> registrarSalida(
            @Body MovimientoRequest request
    );

    @GET("movimientos/ubicacion/{idUbicacion}")
    Call<List<MovimientoResponse>> obtenerMovimientosPorUbicacion(
            @Path("idUbicacion") int idUbicacion
    );

    @GET("pedidos/pendientes")
    Call<List<Pedido>> obtenerPedidosPendientes();

    @GET("pedidos/pendientes/{idUbicacion}")
    Call<List<Pedido>> obtenerPedidosPendientesPorUbicacion(
            @Path("idUbicacion") int idUbicacion
    );

    @PUT("pedidos/{idPedido}/aceptar")
    Call<Void> aceptarPedido(
            @Path("idPedido") int idPedido
    );

    @PUT("pedidos/{idPedido}/cancelar")
    Call<Void> cancelarPedido(
            @Path("idPedido") int idPedido,
            @Query("motivo") String motivo
    );

    @GET("pedidos/aceptados")
    Call<List<Pedido>> obtenerPedidosAceptados();

    @PUT("pedidos/{idPedido}/entregar")
    Call<Void> entregarPedido(
            @Path("idPedido") int idPedido,
            @Query("fecha") String fecha
    );

    @POST("subsidios/validar")
    Call<SubsidioResponse> validarSubsidio(
            @Body SubsidioRequest request
    );

    @GET("pedidos")
    Call<List<Pedido>> obtenerTodosPedidos();

    @GET("pedidos/dashboard/distribuidor")
    Call<DashboardDistribuidor> obtenerDashboardDistribuidor();
}