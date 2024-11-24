package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FornecedorCBDTO {

    private Integer id;
    private String nome;

    @Override
    public String toString() {
        return nome;
    }

}
