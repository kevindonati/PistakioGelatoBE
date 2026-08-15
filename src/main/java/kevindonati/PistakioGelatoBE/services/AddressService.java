package kevindonati.PistakioGelatoBE.services;

import kevindonati.PistakioGelatoBE.entities.Address;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.enums.UserRole;
import kevindonati.PistakioGelatoBE.exceptions.NotFoundException;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.payloads.AddressDTO;
import kevindonati.PistakioGelatoBE.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public Address save(AddressDTO payload) {
        User authenticatedUser = getAuthenticatedUser();
        Address newAddress = new Address(payload.addressLine1(), payload.addressLine2(), payload.postalCode(), payload.city(), payload.country(), authenticatedUser);

        return addressRepository.save(newAddress);
    }

    public Page<Address> findAll(int page, int size, String orderBy) {
        if (size > 50) size = 50;
        if (size < 1) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));

        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == UserRole.ADMIN) {
            return addressRepository.findAll(pageable);
        }

        return addressRepository.findByUserId(authenticatedUser.getId(), pageable);
    }

    public Address findById(UUID id) {
        Address foundedAddress = addressRepository.findById(id).orElseThrow(() -> new NotFoundException("Address with id " + id + " not found"));

        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() != UserRole.ADMIN &&
                !foundedAddress.getUser().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedException("You cannot access another user's address");
        }
        return foundedAddress;
    }

    public Address findByIdAndUpdate(UUID id, AddressDTO payload) {
        Address foundedAddress = this.findById(id);

        foundedAddress.setAddressLine1(payload.addressLine1());
        foundedAddress.setAddressLine2(payload.addressLine2());
        foundedAddress.setPostalCode(payload.postalCode());
        foundedAddress.setCity(payload.city());
        foundedAddress.setCountry(payload.country());

        return addressRepository.save(foundedAddress);
    }

    public void findByIdAndDelete(UUID id) {
        Address foundedAddress = this.findById(id);
        addressRepository.delete(foundedAddress);
    }
}
