package com.example.projetoversaofinal.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Converte o objeto de estado (padrão State) em texto para a coluna "status" e vice-versa.
// Assim o State continua sendo um objeto com comportamento em Java,
// mas no banco aparece só como VARCHAR (ex.: "Aberta").
@Converter
public class EstadoAtividadeConverter implements AttributeConverter<EstadoAtividade, String> {

    @Override
    public String convertToDatabaseColumn(EstadoAtividade estado) {
        return estado == null ? null : estado.getClass().getSimpleName();
    }

    @Override
    public EstadoAtividade convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        return switch (valor) {
            case "Aberta" -> new EstadoAtividade.Aberta();
            case "Atribuida" -> new EstadoAtividade.Atribuida();
            case "Revisao" -> new EstadoAtividade.Revisao();
            case "Concluida" -> new EstadoAtividade.Concluida();
            case "Cancelada" -> new EstadoAtividade.Cancelada();
            default -> throw new IllegalArgumentException("Status desconhecido no banco: " + valor);
        };
    }
}
