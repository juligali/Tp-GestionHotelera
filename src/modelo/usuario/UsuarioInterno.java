package modelo.usuario;

import enums.Rol;

public abstract class UsuarioInterno {

    private int id;
    private String nombre;
    private String email;
    private String contrasena;
    private Rol rol;

    public UsuarioInterno(int id, String nombre, String email, String contrasena, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public boolean login(String email, String contrasena) {
        return this.email.equals(email) && this.contrasena.equals(contrasena);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public Rol getRol() { return rol; }
}
