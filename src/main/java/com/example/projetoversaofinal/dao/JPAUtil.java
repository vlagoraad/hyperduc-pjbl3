package com.example.projetoversaofinal.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

// Singleton: o EntityManagerFactory é caro de criar (lê o persistence.xml,
// abre o pool de conexões e gera as tabelas), então existe um só na aplicação.
// Cada operação do DAO pede um EntityManager novo, que é leve e descartável.
public final class JPAUtil {

    private static final String UNIDADE_PERSISTENCIA = "hyperducPU";

    private static JPAUtil instancia;

    private final EntityManagerFactory emf;

    private JPAUtil() {
        // Permite trocar URL/usuário/senha sem mexer no persistence.xml, ex.:
        // -Dhyperduc.db.url=jdbc:mysql://localhost:3306/hyperduc
        Map<String, String> sobrescritas = new HashMap<>();
        copiar("hyperduc.db.url", "jakarta.persistence.jdbc.url", sobrescritas);
        copiar("hyperduc.db.usuario", "jakarta.persistence.jdbc.user", sobrescritas);
        copiar("hyperduc.db.senha", "jakarta.persistence.jdbc.password", sobrescritas);
        emf = Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA, sobrescritas);
    }

    private static void copiar(String propriedadeSistema, String chaveJpa, Map<String, String> destino) {
        String valor = System.getProperty(propriedadeSistema);
        if (valor != null) {
            destino.put(chaveJpa, valor);
        }
    }

    public static synchronized JPAUtil getInstancia() {
        if (instancia == null) {
            instancia = new JPAUtil();
        }
        return instancia;
    }

    public EntityManager criarEntityManager() {
        return emf.createEntityManager();
    }

    // Abre e fecha uma conexão só para descobrir se o MySQL está no ar.
    public void testarConexao() {
        EntityManager em = criarEntityManager();
        try {
            em.createNativeQuery("SELECT 1").getSingleResult();
        } finally {
            em.close();
        }
    }

    public static synchronized void fechar() {
        if (instancia != null) {
            instancia.emf.close();
            instancia = null;
        }
    }
}
