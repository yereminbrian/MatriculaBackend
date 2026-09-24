package pe.edu.upeu.MatriculaBackend.service.service;

import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;

import java.util.List;

package pe.edu.upeu.MatriculaBackend.service.service;

import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculadosPorCursoDTO;

import java.util.List;

public interface MatriculaService {

    MatriculaResponseDTO crear(MatriculaRequestDTO request);
    MatriculaResponseDTO obtenerPorId(Long id);
    List<MatriculaResponseDTO> listar();
    MatriculaResponseDTO anular(Long id);
    List<MatriculadosPorCursoDTO> reporteMatriculadosPorCurso(String periodo, Long carreraId);
}