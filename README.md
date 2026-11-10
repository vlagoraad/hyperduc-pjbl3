# HYPERDUC! – Plataforma de Gestão de Projetos Colaborativos

Projeto da disciplina **Design de Software (RA3)** – PUCPR, Engenharia de Software, 2026.

Equipe: Amanda Freire, Gabriella Tavares Pedroso, Giovanna Rita Santos Daniel,
João Lucas do Prado Ribeiro e Valentina Lago.

Aplicação JavaFX com persistência em **MySQL** usando **JPA (Jakarta Persistence 3.1) com EclipseLink 4**.
Evolução do projeto do RA2: a gravação em arquivos `.dat` foi substituída por mapeamento objeto-relacional.

## Organização (3 camadas)

| Pacote | Conteúdo |
|---|---|
| `model` | As 7 entidades JPA, os estados (State), os conversores dos estados e o `Historico` (Memento) |
| `dao` | `JPAUtil` (Singleton do `EntityManagerFactory`), `DAOGenerico<T, ID>` (Template Method) e um DAO por entidade |
| `view` | Telas JavaFX |
| raiz | `Launcher` (ponto de entrada), `Programa` (menu), `DadosExemplo`, `DemonstracaoPadroes` |

## Entidades e relacionamentos

| Entidade | Tabela | Relacionamentos |
|---|---|---|
| Projeto | `projeto` | 1:N Atividade (cascade REMOVE), 1:N Participacao |
| Atividade | `atividade` | N:1 Projeto (obrigatório), N:1 Colaborador responsável (opcional), 1:N Comentario (composição), N:N Etiqueta |
| Colaborador | `colaborador` | 1:N Participacao |
| Participacao | `participacao` | Entidade associativa Projeto × Colaborador (papel, data de entrada), `UNIQUE(projeto_id, colaborador_id)` |
| Comentario | `comentario` | N:1 Atividade, N:1 Colaborador autor |
| Etiqueta | `etiqueta` | N:N Atividade (tabela de junção `atividade_etiqueta`) |
| Denuncia | `denuncia` | N:1 Colaborador autor |

Os diagramas estão em `docs` (`modelo-dominio.puml` e `diagrama-classes-v2.puml`, com os PNG gerados).

## Padrões de projeto (do RA2, adaptados)

| Padrão | Onde está |
|---|---|
| **Singleton** | `JPAUtil`: um único `EntityManagerFactory` |
| **Template Method** | `DAOGenerico`: abre o `EntityManager`, controla a transação e chama os ganchos `getId`, `validar` e `antesDeRemover` |
| **State** | `EstadoProjeto`, `EstadoAtividade`, `EstadoDenuncia`. Gravados na coluna `status` por um `AttributeConverter` |
| **Memento** | `Projeto.Memento`, `Atividade.Memento`, `Denuncia.Memento` + `Historico<M>` (botão "Desfazer") |

## Como executar

Requisitos: **JDK 21+** e **MAMP** com os servidores ligados.

1. Abra o MAMP e clique em **Start**. Não é preciso criar o banco: o `hyperduc` é criado sozinho, e as tabelas
   são geradas pelo EclipseLink na primeira execução.
2. Confira a conexão em `src/main/resources/META-INF/persistence.xml`:
   - MAMP no **macOS**: porta **8889**, usuário `root`, senha `root` (padrão do arquivo);
   - MAMP no **Windows**: normalmente porta **3306**. Troque `8889` por `3306` na URL.
3. Rode:
   ```bash
   ./mvnw clean javafx:run          # Linux/macOS
   mvnw.cmd clean javafx:run        # Windows
   ```
   No IntelliJ: abra como projeto Maven e rode **`Launcher.main()`** (não `Programa`).
4. No menu, o botão **Carregar dados de exemplo** preenche o banco (se estiver vazio) com projetos,
   atividades, colaboradores e assim por diante, para a apresentação.

As tabelas podem ser vistas no **phpMyAdmin** do MAMP (banco `hyperduc`). Os SQL gerados pelo EclipseLink aparecem no console.

### Demonstração no console

`DemonstracaoPadroes` executa o CRUD das 7 entidades, os relacionamentos, as regras de exclusão e os
padrões, conferindo cada passo (`[OK]`/`[FALHA]`). Usa IDs 900+ e apaga tudo no final.
