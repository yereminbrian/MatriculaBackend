package pe.edu.upeu.MatriculaBackend.service.service;

import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.generic.CrudService;

public interface MatriculaService extends CrudService<MatriculaRequestDTO, MatriculaResponseDTO, Long> {

    MatriculaResponseDTO anular(Long id);
}