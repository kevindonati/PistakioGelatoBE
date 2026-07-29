package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.Flavor;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
