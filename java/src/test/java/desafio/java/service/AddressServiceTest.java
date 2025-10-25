package desafio.java.service;

import desafio.java.dto.AddressDTO;
import desafio.java.dto.AddressResponseDTO;
import desafio.java.entity.AddressEntity;
import desafio.java.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        java.lang.reflect.Field field = AddressService.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        field.set(addressService, restTemplate);
    }

    @Test
    void testCreate() {

        AddressDTO dto = new AddressDTO();
        dto.setCep("01001-000");
        dto.setComplemento("Apto 101");

        AddressResponseDTO viaCepResponse = new AddressResponseDTO();
        viaCepResponse.setCep("01001000");
        viaCepResponse.setLogradouro("Praça da Sé");
        viaCepResponse.setBairro("Sé");
        viaCepResponse.setCidade("São Paulo");
        viaCepResponse.setUf("SP");

        when(restTemplate.getForObject(anyString(), eq(AddressResponseDTO.class)))
                .thenReturn(viaCepResponse);

        AddressResponseDTO response = addressService.create(dto);

        ArgumentCaptor<AddressEntity> entityCaptor = ArgumentCaptor.forClass(AddressEntity.class);
        verify(addressRepository).save(entityCaptor.capture());
        AddressEntity savedEntity = entityCaptor.getValue();

        assertEquals("01001000", savedEntity.getCep());
        assertEquals("Praça da Sé", savedEntity.getLogradouro());
        assertEquals("Sé", savedEntity.getBairro());
        assertEquals("São Paulo", savedEntity.getCidade());
        assertEquals("SP", savedEntity.getUf());
        assertEquals("Apto 101", savedEntity.getComplemento());

        assertEquals("01001-000", response.getCep());
        assertEquals("Praça da Sé", response.getLogradouro());
        assertEquals("Apto 101", response.getComplemento());
    }

    @Test
    void testFetchFromCep() {
        String cepInput = "01001-000";
        AddressResponseDTO viaCepResponse = new AddressResponseDTO();
        viaCepResponse.setCep("01001000");
        viaCepResponse.setLogradouro("Praça da Sé");

        when(restTemplate.getForObject(anyString(), eq(AddressResponseDTO.class)))
                .thenReturn(viaCepResponse);

        AddressResponseDTO response = addressService.fetchFromCep(cepInput);

        assertNotNull(response);
        assertEquals("01001-000", response.getCep());
        assertEquals("Praça da Sé", response.getLogradouro());

        verify(restTemplate).getForObject("https://viacep.com.br/ws/01001000/json/", AddressResponseDTO.class);
    }
}
