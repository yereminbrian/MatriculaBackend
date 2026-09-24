package pe.edu.upeu.MatriculaBackend.service.generic;
import java.util.Optional;

public interface CrudService<REQ, RES, ID> {

    RES create(REQ req);

    RES update(ID id, REQ req);

    Optional<RES> read(ID id);

    void delete(ID id);

    Iterable<RES> readAll();
}