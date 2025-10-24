package desafio.java.service;

import desafio.java.dto.AddressDTO;
import desafio.java.dto.AddressResponseDTO;
import desafio.java.entity.AddressEntity;
import desafio.java.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponseDTO create(AddressDTO dto) {

        String cepLimpo = dto.getCep().replaceAll("\\D", "");

        String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        AddressResponseDTO viaCep = restTemplate.getForObject(url, AddressResponseDTO.class);

        if (dto.getLogradouro() != null) viaCep.setLogradouro(dto.getLogradouro());
        if (dto.getBairro() != null) viaCep.setBairro(dto.getBairro());
        if (dto.getCidade() != null) viaCep.setCidade(dto.getCidade());
        if (dto.getUf() != null) viaCep.setUf(dto.getUf());
        viaCep.setComplemento(dto.getComplemento());

        // Persiste sem máscara
        AddressEntity entity = new AddressEntity();
        entity.setCep(cepLimpo);
        entity.setLogradouro(viaCep.getLogradouro());
        entity.setBairro(viaCep.getBairro());
        entity.setCidade(viaCep.getCidade());
        entity.setUf(viaCep.getUf());
        entity.setComplemento(viaCep.getComplemento());

        addressRepository.save(entity);

        // Retorna com máscara
        AddressResponseDTO response = new AddressResponseDTO();
        response.setCep(entity.getCep());
        response.setLogradouro(entity.getLogradouro());
        response.setBairro(entity.getBairro());
        response.setCidade(entity.getCidade());
        response.setUf(entity.getUf());
        response.setComplemento(entity.getComplemento());

        return response;
    }

    public AddressResponseDTO fetchFromCep(AddressDTO dto) {
        String cepLimpo = dto.getCep().replaceAll("\\D", "");
        String url = "https://viacep.com.br/ws/" + cepLimpo + "/json/";
        return restTemplate.getForObject(url, AddressResponseDTO.class);
    }

}