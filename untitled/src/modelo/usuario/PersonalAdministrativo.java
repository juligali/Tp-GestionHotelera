package modelo.usuario;

import enums.Rol;


public class PersonalAdministrativo extends UsuarioInterno {
    public PersonalAdministrativo(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.PERSONAL_ADMINISTRATIVO);
    }

}
