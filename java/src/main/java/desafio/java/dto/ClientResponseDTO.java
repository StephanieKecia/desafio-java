package desafio.java.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponseDTO {

    private Long id;
    private String name;
    private String cpf;
    private List<AddressResponseDTO> addresses;
    private List<PhoneDTO> phones;
    private List<EmailDTO> emails;

    @JsonGetter("cpf")
    public String getCpfMasked() {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0,3) + "." +
                cpf.substring(3,6) + "." +
                cpf.substring(6,9) + "-" +
                cpf.substring(9,11);
    }
}
