package desafio.java.controller;

import desafio.java.dto.AddressResponseDTO;
import desafio.java.service.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cep")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/{cep}")
    public AddressResponseDTO getAddress(@PathVariable String cep) {
        return addressService.fetchFromCep(cep);
    }
}
