package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.Flavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Flavor> findByAvailableTrue();

    List<Flavor> findByCategoryId(UUID categoryId);

    List<Flavor> findByVeganTrue();

    List<Flavor> findByGlutenFreeTrue();

    List<Flavor> findByLactoseFreeTrue();

    List<Flavor> findBySugarFreeTrue();
}
