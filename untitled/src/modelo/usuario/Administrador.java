package modelo.usuario;


import enums.Rol;


import java.util.List;

public class Administrador extends UsuarioInterno{
    private List<UsuarioInterno> usuarios;

    public Administrador(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.ADMINISTRADOR);
        this.usuarios = new java.util.ArrayList<>();
    }


}
