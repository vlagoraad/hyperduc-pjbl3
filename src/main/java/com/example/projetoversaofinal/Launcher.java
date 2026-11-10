package com.example.projetoversaofinal;

// Ponto de entrada da aplicação. Fica separado de Programa (que estende Application)
// porque, sem module-info, o JavaFX exige que o main não esteja na subclasse de Application.
// No IntelliJ: botão direito em Launcher -> Run 'Launcher.main()'.
public class Launcher {
    public static void main(String[] args) {
        Programa.main(args);
    }
}
