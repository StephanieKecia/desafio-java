package desafio.java.service;

import desafio.java.dto.*;
import desafio.java.entity.AddressEntity;
import desafio.java.entity.ClientEntity;
import desafio.java.entity.EmailEntity;
import desafio.java.entity.PhoneEntity;
import desafio.java.mapper.ClientMapper;
import desafio.java.repository.AddressRepository;
import desafio.java.repository.ClientRepository;
import desafio.java.repository.EmailRepository;
import desafio.java.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private ClientRepository clientRepository;

    private ClientMapper clientMapper;

    private AddressService addressService;


    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, AddressRepository addressRepository, PhoneRepository phoneRepository, EmailRepository emailRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.addressService = new AddressService(addressRepository);
    }

    private static final String CLIENT_NOT_FOUND = "Cliente não encontrado com ID: ";

    public ClientResponseDTO create(ClientRequestDTO dto) {

        ClientEntity client = new ClientEntity();
        client.setName(dto.getName());
        client.setCpf(dto.getCpf().replaceAll("\\D", ""));


        List<EmailEntity> emails = new ArrayList<>();
        if (dto.getEmails() != null) {
            for (EmailDTO e : dto.getEmails()) {
                EmailEntity email = new EmailEntity();
                email.setEmail(e.getEmail());
                email.setClient(client);
                emails.add(email);
            }
        }

        List<PhoneEntity> phones = new ArrayList<>();
        if (dto.getPhones() != null) {
            for (PhoneDTO p : dto.getPhones()) {
                PhoneEntity phone = new PhoneEntity();
                phone.setNumber(p.getNumber());
                phone.setType(p.getType());
                phone.setClient(client);
                phones.add(phone);
            }
        }


        List<AddressEntity> addresses = new ArrayList<>();
        if (dto.getAddresses() != null) {
            for (AddressDTO a : dto.getAddresses()) {
                AddressResponseDTO viaCepResponse = addressService.create(a);

                AddressEntity address = new AddressEntity();
                address.setCep(viaCepResponse.getCep().replaceAll("\\D", ""));
                address.setLogradouro(viaCepResponse.getLogradouro());
                address.setBairro(viaCepResponse.getBairro());
                address.setCidade(viaCepResponse.getCidade());
                address.setUf(viaCepResponse.getUf());
                address.setComplemento(viaCepResponse.getComplemento());
                address.setClient(client);
                addresses.add(address);
            }
        }

        client.setEmails(emails);
        client.setPhones(phones);
        client.setAddresses(addresses);

        clientRepository.save(client);

        return clientMapper.mapToResponseDTO(client);
    }

    public ClientResponseDTO findById(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CLIENT_NOT_FOUND + id));
        return clientMapper.mapToResponseDTO(client);
    }

    public ClientResponseDTO update(Long id, ClientRequestDTO dto) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CLIENT_NOT_FOUND + id));

        if (dto.getName() != null) {
            client.setName(dto.getName());
        }
        if (dto.getCpf() != null) {
            client.setCpf(dto.getCpf().replaceAll("\\D", ""));
        }

        client.getEmails().clear();
        if (dto.getEmails() != null) {
            for (EmailDTO e : dto.getEmails()) {
                boolean exists = client.getEmails().stream()
                        .anyMatch(email -> email.getEmail().equalsIgnoreCase(e.getEmail()));
                if (!exists) {
                    EmailEntity email = new EmailEntity();
                    email.setEmail(e.getEmail());
                    email.setClient(client);
                    client.getEmails().add(email);
                }
            }
        }

        client.getPhones().clear();
        if (dto.getPhones() != null) {
            for (PhoneDTO p : dto.getPhones()) {
                boolean exists = client.getPhones().stream()
                        .anyMatch(phone -> phone.getNumber().equals(p.getNumber()) && phone.getType().equals(p.getType()));
                if (!exists) {
                    PhoneEntity phone = new PhoneEntity();
                    phone.setNumber(p.getNumber());
                    phone.setType(p.getType());
                    phone.setClient(client);
                    client.getPhones().add(phone);
                }
            }
        }

        client.getAddresses().clear();
        if (dto.getAddresses() != null) {
            for (AddressDTO a : dto.getAddresses()) {
                boolean exists = client.getAddresses().stream()
                        .anyMatch(addr -> addr.getCep().equals(a.getCep().replaceAll("\\D", "")));
                if (!exists) {
                    AddressResponseDTO viaCepResponse = addressService.create(a);

                    AddressEntity address = new AddressEntity();
                    address.setCep(viaCepResponse.getCep().replaceAll("\\D", ""));
                    address.setLogradouro(viaCepResponse.getLogradouro());
                    address.setBairro(viaCepResponse.getBairro());
                    address.setCidade(viaCepResponse.getCidade());
                    address.setUf(viaCepResponse.getUf());
                    address.setComplemento(viaCepResponse.getComplemento());
                    address.setClient(client);
                    client.getAddresses().add(address);
                }
            }
        }

        clientRepository.save(client);
        return clientMapper.mapToResponseDTO(client);
    }


    public void delete(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CLIENT_NOT_FOUND + id));
        clientRepository.delete(client);
    }

    public List<ClientResponseDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}
