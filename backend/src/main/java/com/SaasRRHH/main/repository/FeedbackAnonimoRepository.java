package com.SaasRRHH.main.repository;

import com.SaasRRHH.main.model.FeedbackAnonimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackAnonimoRepository extends JpaRepository<FeedbackAnonimo, Long> {

    List<FeedbackAnonimo> findByCategoriaOrderByFechaEnvioDesc(FeedbackAnonimo.CategoriaFeedback categoria);

    List<FeedbackAnonimo> findByEstadoOrderByFechaEnvioDesc(FeedbackAnonimo.EstadoFeedback estado);

    List<FeedbackAnonimo> findByFechaEnvioBetweenOrderByFechaEnvioDesc(LocalDateTime inicio, LocalDateTime fin);

    Long countByEstado(FeedbackAnonimo.EstadoFeedback estado);
}
