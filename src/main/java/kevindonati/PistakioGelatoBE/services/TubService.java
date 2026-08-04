package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Tub;
import kevindonati.PistakioGelatoBE.exceptions.BadRequestException;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.TubDTO;
import kevindonati.PistakioGelatoBE.repositories.TubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TubService {
    @Autowired
    private TubRepository tubRepository;

    public Tub save(TubDTO payload) {
        if (tubRepository.existsByName(payload.name())) {
            throw new BadRequestException("The tub with the name " + payload.name() + " already exists");
        }

        Tub newTub = new Tub(
                payload.name(),
                payload.description(),
                payload.weight(),
                payload.price(),
                payload.image(),
                payload.available()
        );
        return tubRepository.save(newTub);
    }

    public Page<Tub> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return tubRepository.findAll(pageable);
    }

    public Tub findById(UUID id) {
        return tubRepository.findById(id).orElseThrow(() -> new NotFoundException("Tub with id " + id + " not found"));
    }

    public Tub findByIdAndUpdate(UUID id, TubDTO payload) {
        Tub foundedTub = this.findById(id);

        if (!foundedTub.getName().equals(payload.name()) && tubRepository.existsByName(payload.name())) {
            throw new BadRequestException("This tub name is already registered");
        }

        foundedTub.setName(payload.name());
        foundedTub.setDescription(payload.description());
        foundedTub.setWeight(payload.weight());
        foundedTub.setPrice(payload.price());
        foundedTub.setImage(payload.image());
        foundedTub.setAvailable(payload.available());

        return tubRepository.save(foundedTub);
    }

    public void findByIdAndDelete(UUID id) {
        Tub foundedTub = this.findById(id);
        tubRepository.delete(foundedTub);
    }
}
