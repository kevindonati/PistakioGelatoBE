package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Category;
import kevindonati.PistakioGelatoBE.entities.CategoryTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, UUID> {
    Optional<CategoryTranslation> findByCategoryAndLanguage(Category category, Language language);

    boolean existsByCategoryAndLanguage(Category category, Language language);

    void deleteByCategory(Category category);
}
