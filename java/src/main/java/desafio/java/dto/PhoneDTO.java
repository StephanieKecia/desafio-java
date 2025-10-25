package desafio.java.dto;

import desafio.java.enums.PhoneType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {

    @NotNull(message = "O tipo de telefone é obrigatório")
    private PhoneType type;

    @NotBlank(message = "O número do telefone é obrigatório")
    @Pattern(regexp = "^[0-9()\\-\\s]+$", message = "Número inválido")
    private String number;

    public void setNumber(String number) {
        if (number != null) {
            this.number = number.replaceAll("\\D", "");
        } else {
            this.number = null;
        }
    }

}
