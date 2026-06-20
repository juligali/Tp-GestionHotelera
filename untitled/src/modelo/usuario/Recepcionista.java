package modelo.usuario;

import enums.Rol;

public class Recepcionista extends UsuarioInterno {
    public Recepcionista(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.RECEPCIONISTA);
    }

}
