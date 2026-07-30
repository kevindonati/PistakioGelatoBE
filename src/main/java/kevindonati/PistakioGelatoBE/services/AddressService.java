package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Address;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.payloads.AddressDTO;
import kevindonati.PistakioGelatoBE.repositories.AddressRepository;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    public Address save(AddressDTO payload) {
        User foundedUser = userRepository.findById(payload.user()).orElseThrow(() -> new NotFoundException("User with id " + payload.user() + " not found"));
        Address newAddress = new Address(payload.addressLine1(), payload.addressLine2(), payload.postalCode(), payload.city(), payload.country(), foundedUser);

        return addressRepository.save(newAddress);
    }

    public Page<Address> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        return addressRepository.findAll(pageable);
    }

    public Address findById(UUID id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException("Address with id " + id + " not found"));
    }

    public Address findByIdAndUpdate(UUID id, AddressDTO payload) {
        Address foundedAddress = this.findById(id);
        User foundedUser = userRepository.findById(payload.user()).orElseThrow(() -> new NotFoundException("User with id " + payload.user() + " not found"));
        foundedAddress.setAddressLine1(payload.addressLine1());
        foundedAddress.setAddressLine2(payload.addressLine2());
        foundedAddress.setPostalCode(payload.postalCode());
        foundedAddress.setCity(payload.city());
        foundedAddress.setCountry(payload.country());
        foundedAddress.setUser(foundedUser);

        return addressRepository.save(foundedAddress);
    }

    public void findByIdAndDelete(UUID id) {
        Address foundedAddress = this.findById(id);
        addressRepository.delete(foundedAddress);
    }
}
