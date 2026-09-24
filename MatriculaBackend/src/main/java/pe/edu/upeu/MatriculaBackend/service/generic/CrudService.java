package pe.edu.upeu.MatriculaBackend.service.generic;

import java.util.List;

public interface CrudService<REQ, RES, ID> {
    RES crear(REQ req);
    RES actualizar(ID id, REQ req);
    RES obtenerPorId(ID id);
    void eliminar(ID id);
    List<RES> listarTodos();
}