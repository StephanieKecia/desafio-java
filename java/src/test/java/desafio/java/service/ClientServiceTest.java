package desafio.java.service;

import desafio.java.dto.*;
import desafio.java.entity.*;
import desafio.java.enums.PhoneType;
import desafio.java.mapper.ClientMapper;
import desafio.java.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PhoneRepository phoneRepository;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreate() {

        ClientRequestDTO dto = new ClientRequestDTO();
        dto.setName("João");
        dto.setCpf("123.456.789-00");

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail("joao@example.com");
        dto.setEmails(Collections.singletonList(emailDTO));

        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setNumber("999999999");
        phoneDTO.setType(PhoneType.COMERCIAL);
        dto.setPhones(Collections.singletonList(phoneDTO));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCep("01001-000");
        dto.setAddresses(Collections.singletonList(addressDTO));

        AddressService addressServiceMock = mock(AddressService.class);
        AddressResponseDTO addressResponse = new AddressResponseDTO();
        addressResponse.setCep("01001000");
        addressResponse.setLogradouro("Praça da Sé");
        addressResponse.setBairro("Sé");
        addressResponse.setCidade("São Paulo");
        addressResponse.setUf("SP");
        addressResponse.setComplemento("Apto 101");

        when(addressServiceMock.create(any(AddressDTO.class))).thenReturn(addressResponse);

        try {
            java.lang.reflect.Field field = ClientService.class.getDeclaredField("addressService");
            field.setAccessible(true);
            field.set(clientService, addressServiceMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ClientResponseDTO responseDTO = new ClientResponseDTO();
        responseDTO.setName(dto.getName());
        responseDTO.setCpf(dto.getCpf().replaceAll("\\D", ""));
        when(clientMapper.mapToResponseDTO(any(ClientEntity.class))).thenReturn(responseDTO);

        ClientResponseDTO response = clientService.create(dto);

        ArgumentCaptor<ClientEntity> captor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientRepository).save(captor.capture());
        ClientEntity saved = captor.getValue();
        assertEquals("João", saved.getName());
        assertEquals("12345678900", saved.getCpf());
        assertEquals(1, saved.getEmails().size());
        assertEquals(1, saved.getPhones().size());
        assertEquals(1, saved.getAddresses().size());

        assertEquals("João", response.getName());
        assertEquals("123456789-00".replaceAll("\\D", ""), response.getCpf());
    }

    @Test
    void testFindById_Found() {
        ClientEntity client = new ClientEntity();
        client.setName("Maria");
        client.setCpf("11122233344");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientResponseDTO responseDTO = new ClientResponseDTO();
        responseDTO.setName(client.getName());
        when(clientMapper.mapToResponseDTO(client)).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.findById(1L);
        assertEquals("Maria", result.getName());
    }

    @Test
    void testFindById_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> clientService.findById(1L));
    }

    @Test
    void testDelete() {
        ClientEntity client = new ClientEntity();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        clientService.delete(1L);
        verify(clientRepository).delete(client);
    }

    @Test
    void testDelete_NotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> clientService.delete(1L));
    }

    @Test
    void testFindAll() {
        ClientEntity client = new ClientEntity();
        client.setName("Teste");

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));

        ClientResponseDTO responseDTO = new ClientResponseDTO();
        responseDTO.setName("Teste");
        when(clientMapper.mapToResponseDTO(client)).thenReturn(responseDTO);

        assertEquals(1, clientService.findAll().size());
        assertEquals("Teste", clientService.findAll().get(0).getName());
    }
}

