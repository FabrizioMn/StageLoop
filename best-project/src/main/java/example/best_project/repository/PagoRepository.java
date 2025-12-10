package example.best_project.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import example.best_project.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    @Query("SELECT SUM(p.monto) FROM Pago p")
    BigDecimal sumarIngresosTotales();

    void deleteByOrden_IdOrden(Integer idOrden);
}