package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlavorRepository extends JpaRepository<Flavor, UUID> {
    boolean existsByName(String name);
    
    List<Flavor> findByAvailableTrue();

    List<Flavor> findByCategoryId(UUID categoryId);

    List<Flavor> findByVeganTrue();

    List<Flavor> findByGlutenFreeTrue();

    List<Flavor> findByLactoseFreeTrue();

    List<Flavor> findBySugarFreeTrue();
}
