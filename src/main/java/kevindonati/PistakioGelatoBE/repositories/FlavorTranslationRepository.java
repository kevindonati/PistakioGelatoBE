package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Flavor;
import kevindonati.PistakioGelatoBE.entities.FlavorTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FlavorTranslationRepository extends JpaRepository<FlavorTranslation, UUID> {
    Optional<FlavorTranslation> findByFlavorAndLanguage(Flavor flavor, Language language);
    
    boolean existsByFlavorAndLanguage(Flavor flavor, Language language);
}
