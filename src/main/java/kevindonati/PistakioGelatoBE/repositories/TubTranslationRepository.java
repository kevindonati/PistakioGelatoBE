package kevindonati.PistakioGelatoBE.repositories;

import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.entities.TubTranslation;
import kevindonati.PistakioGelatoBE.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TubTranslationRepository extends JpaRepository<TubTranslation, UUID> {
    Optional<TubTranslation> findByTubAndLanguage(Tub tub, Language language);
}
