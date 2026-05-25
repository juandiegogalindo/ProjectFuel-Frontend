package co.edu.unipiloto.scrumbacklog.model;

public class Usuario {

    private int id;
    private String nombre;
    private String usuario;
    private String correo;
    private String direccion;
    private String password;
    private String rol;

    // 🔥 NUEVO: RELACIÓN CON ESTACIÓN
    private int idUbicacion;

    private String fechaNacimiento;
    private String genero;
    private double latitud;
    private double longitud;

    private int verificado;
    private String codigoVerificacion;

    public Usuario() {}

    public Usuario(String nombre, String usuario, String correo, String direccion,
                   String password, String rol, int idUbicacion,
                   String fechaNacimiento, String genero,
                   double latitud, double longitud,
                   int verificado, String codigoVerificacion) {

        this.nombre = nombre;
        this.usuario = usuario;
        this.correo = correo;
        this.direccion = direccion;
        this.password = password;
        this.rol = rol;
        this.idUbicacion = idUbicacion;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.latitud = latitud;
        this.longitud = longitud;
        this.verificado = verificado;
        this.codigoVerificacion = codigoVerificacion;
    }

    // GETTERS
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }
    public int getIdUbicacion() { return idUbicacion; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero() { return genero; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public int getVerificado() { return verificado; }
    public String getCodigoVerificacion() { return codigoVerificacion; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
    public void setIdUbicacion(int idUbicacion) { this.idUbicacion = idUbicacion; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public void setVerificado(int verificado) { this.verificado = verificado; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }
}