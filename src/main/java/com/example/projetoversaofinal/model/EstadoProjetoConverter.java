package com.example.projetoversaofinal.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Converte o objeto de estado (padrão State) em texto para a coluna "status" e vice-versa.
// Assim o State continua sendo um objeto com comportamento em Java,
// mas no banco aparece só como VARCHAR (ex.: "Planejamento").
@Converter
public class EstadoProjetoConverter implements AttributeConverter<EstadoProjeto, String> {

    @Override
    public String convertToDatabaseColumn(EstadoProjeto estado) {
        return estado == null ? null : estado.getClass().getSimpleName();
    }

    @Override
    public EstadoProjeto convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        return switch (valor) {
            case "Planejamento" -> new EstadoProjeto.Planejamento();
            case "EmAndamento" -> new EstadoProjeto.EmAndamento();
            case "Concluido" -> new EstadoProjeto.Concluido();
            case "Cancelado" -> new EstadoProjeto.Cancelado();
            default -> throw new IllegalArgumentException("Status desconhecido no banco: " + valor);
        };
    }
}
