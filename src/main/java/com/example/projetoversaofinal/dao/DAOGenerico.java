package com.example.projetoversaofinal.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.function.Function;

// Template Method: o algoritmo de cada operação do CRUD (abrir EntityManager,
// iniciar transação, validar, gravar, confirmar ou desfazer, fechar) está escrito
// uma vez só aqui. Os DAOs concretos só preenchem os passos variáveis:
//   - getId(item)              -> qual é a chave da entidade
//   - validar(item)            -> regras antes de inserir/atualizar (gancho opcional)
//   - antesDeRemover(em, item) -> o que fazer com os relacionamentos antes de excluir (gancho opcional)
public abstract class DAOGenerico<T, ID> {

    private final Class<T> classe;

    protected DAOGenerico(Class<T> classe) {
        this.classe = classe;
    }

    protected abstract ID getId(T item);

    protected void validar(T item) {
    }

    protected void antesDeRemover(EntityManager em, T item) {
    }

    // ---------- CRUD (final: as subclasses não mudam a ordem dos passos) ----------

    public final ArrayList<T> listar() {
        return consultar(em -> new ArrayList<>(
                em.createQuery("SELECT e FROM " + classe.getSimpleName() + " e ORDER BY e.id", classe)
                        .getResultList()));
    }

    public final T buscarPorId(ID id) {
        if (id == null) {
            return null;
        }
        return consultar(em -> em.find(classe, id));
    }

    public final void adicionar(T item) {
        validar(item);
        executarEmTransacao(em -> {
            if (em.find(classe, getId(item)) != null) {
                throw new IllegalArgumentException("Já existe um registro com o ID " + getId(item) + ".");
            }
            em.persist(item);
            return null;
        });
    }

    public final boolean atualizar(T item) {
        validar(item);
        return executarEmTransacao(em -> {
            if (em.find(classe, getId(item)) == null) {
                return false;
            }
            em.merge(item);
            return true;
        });
    }

    public final boolean remover(ID id) {
        return executarEmTransacao(em -> {
            T item = em.find(classe, id);
            if (item == null) {
                return false;
            }
            antesDeRemover(em, item);
            em.remove(item);
            return true;
        });
    }

    // ---------- infraestrutura compartilhada pelos DAOs ----------

    // Leitura: não precisa de transação.
    protected final <R> R consultar(Function<EntityManager, R> operacao) {
        EntityManager em = JPAUtil.getInstancia().criarEntityManager();
        try {
            return operacao.apply(em);
        } finally {
            em.close();
        }
    }

    // Escrita: tudo ou nada. Qualquer exceção faz rollback.
    protected final <R> R executarEmTransacao(Function<EntityManager, R> operacao) {
        EntityManager em = JPAUtil.getInstancia().criarEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = operacao.apply(em);
            tx.commit();
            return resultado;
        } catch (PersistenceException e) {
            if (tx.isActive()) tx.rollback();
            // Traduz o erro do banco numa mensagem que a tela consegue mostrar.
            throw new IllegalArgumentException("Erro no banco de dados: " + causaRaiz(e).getMessage(), e);
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    protected static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    protected static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static Throwable causaRaiz(Throwable e) {
        while (e.getCause() != null && e.getCause() != e) {
            e = e.getCause();
        }
        return e;
    }
}
