import java.util.regex.Pattern;

public class VariableValidator {

    // Regex para identificar um tipo válido
    private static final String TYPE_REGEX = "^(int|float|char|double|byte|short|long|boolean|" +
                                             "Integer|Float|Character|Double|Byte|Short|Long" +
                                             "|Boolean|String|Object)$";

    // Regex para validar identificadores
    private static final String IDENTIFIER_REGEX = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    public static boolean isValidDeclaration(String declaration) {
        declaration = declaration.trim();

        // Verifica se termina com ';'
        if (!isSyntaxValid(declaration)) return false;

        // Remove o ponto e vírgula final
        declaration = declaration.substring(0, declaration.length() - 1).trim();

        // Divide em tipo e lista de variáveis
        String[] parts = declaration.split("\\s+", 2);
        if (parts.length != 2) {
            System.out.println("Erro: formato inválido (esperado: <tipo> <variáveis>).");
            return false;
        }

        String type = parts[0];
        String variableList = parts[1];

        // Valida o tipo
        if (!isTypeValid(type)) return false;
        // Valida variaveis
        if (!isVariablesValid(variableList)) return false;

        return true;
    }

    private static boolean isSyntaxValid(String declaration) {
        if (!declaration.endsWith(";")) {
            System.out.println("Erro: declaração não termina com ';'.");
            return false;
        }
        return true;
    }

    private static boolean isVariablesValid(String variableList) {
        String[] variables = variableList.split(",");
        for (String variable : variables) {
            variable = variable.trim();
            if (!Pattern.matches(IDENTIFIER_REGEX, variable)) {
                System.out.println("Erro: identificador inválido '" + variable + "'.");
                return false;
            }
        }
        return true;
    }

    private static boolean isTypeValid(String type) {
        if (!Pattern.matches(TYPE_REGEX, type)) {
            System.out.println("Erro: tipo inválido '" + type + "'.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        // Exemplos de entrada
        String[] declarations = {
                "int a, b, c;",
                "float x;",
                "String letter;",
                "double _value1, num2, var_3;",
                "int 1x, y;",         // Inválido (identificador começa com número)
                "boolean flag;",      // Inválido (tipo não permitido)
                "int a b c;",         // Inválido (faltando vírgulas)
                "float x, y ;"        // Inválido (espaço antes de ponto e vírgula)
        };

        // Testa as declarações
        for (String declaration : declarations) {
            System.out.println("Entrada: " + declaration);
            boolean isValid = isValidDeclaration(declaration);
            System.out.println("Resultado: " + (isValid ? "Válido" : "Inválido"));
            System.out.println("----");
        }
    }
}
