import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    private static final String TYPE_REGEX = "^(int|float|char|double|byte|short|long|boolean|" +
                                            "Integer|Float|Character|Double|Byte|Short|Long" +
                                            "|Boolean|String|Object)$";

    private static final String IDENTIFIER_REGEX = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    private static final String CONDITION_REGEX = "\\((.+?)\\)"; 
    private static final String VALID_CONDITIONS_REGEX = "^\\s*[a-zA-Z]+\\s*(==|!=|<=|>=|<|>)\\s*[a-zA-Z0-9]+\\s*$";
    private static final String CALCULATION_REGEX = "\\{.*?(\\+|\\-|\\*|\\/|\\+\\+).+?\\}";


    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Selecione o que deseja validar:");
        System.out.println("1 -> Declarações e Atribuições");
        System.out.println("2 -> Comandos if e switch-case");
        System.out.println("3 -> Laços while e for");
        int opcao = inputScanner.nextInt();
        inputScanner.nextLine();

        System.out.print("Digite o caminho do arquivo para validar: ");
        String filename = inputScanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            Validator validator = new Validator(reader);

            switch (opcao) {
                case 1:
                    validator.validateDeclarationsAndAssignments();
                    break;
                case 2:
                    validator.validateConditions();
                    break;
                case 3:
                    validator.validateLoops();
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputScanner.close();
        }
    }

    static class Validator {
        private final BufferedReader reader;

        public Validator(BufferedReader reader) {
            this.reader = reader;
        }

        public void validateDeclarationsAndAssignments() throws IOException {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                System.out.println("\n");

                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                // Separar por tipo e identificadores
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2 || !Pattern.matches(TYPE_REGEX, parts[0])) {
                    // Identifica se existe os tipos válidos
                    System.err.println("Erro na linha " + lineNumber + ": Tipo inválido ou não encontrado --> " + line);
                } else {
                    String identifiers = parts[1];
                    // Remover possível ponto e vírgula final
                    if (identifiers.endsWith(";")) {
                        identifiers = identifiers.substring(0, identifiers.length() - 1).trim();
                    }

                    // Dividir por vírgulas
                    String[] identifierList = identifiers.split("\\s*,\\s*");
                    boolean hasError = false;
                
                    for (String identifier : identifierList) {
                        if (!Pattern.matches(IDENTIFIER_REGEX, identifier)) {
                            hasError = true;
                            break;
                        }
                    }
                
                    // Verifica se encontrou erro durante o processo
                    if (hasError) {
                        System.err.println("Erro na linha " + lineNumber + ": Identificador inválido --> " + line);
                    } else {
                        System.out.println("Linha " + lineNumber + " válida --> " + line);
                    }
                    
                }


                lineNumber++;
            }
        }

        public void validateConditions() throws IOException {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                System.out.println("\n");

                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (line.startsWith("if")) {
                    Matcher conditionMatcher = Pattern.compile(CONDITION_REGEX).matcher(line);
                    boolean hasError = true;
                    if (conditionMatcher.find()) {
                        // Validar a condição dentro do parenteses
                        String condition = conditionMatcher.group(1).trim(); 

                        if (Pattern.matches(VALID_CONDITIONS_REGEX, condition)) {
                            hasError = false;
                            System.out.println("Linha " + lineNumber + " contém um comando if válido.");
                            System.out.println("Condição identificada: " + condition);
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": condição inválida no if --> " + condition);
                        }
                    } else {
                        // Retorna erro e já sai da função
                        System.err.println("Erro na linha " + lineNumber + ": não foi possível identificar a condição no if.");
                    }

                    // Se não houver erros na verificação acima, agora iremos validar o que está dentro do { }
                    if(!hasError){
                        Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);

                        if (calculationMatcher.find()) {
                            String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                            System.out.println("Cálculo identificado dentro do bloco: " + calculation);
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do if.");
                        }
                    }
                } else if (line.startsWith("switch")) {
                    int inicioParenteses = line.indexOf('(');
                    int fimParenteses = line.indexOf(')');
                    if (inicioParenteses != -1 && fimParenteses != -1) {
                        String variavel = line.substring(inicioParenteses + 1, fimParenteses).trim();
                        System.out.println("Variável do switch: " + variavel);
                    } else {
                        System.err.println("Erro na linha " + lineNumber + ": formato inválido do switch.");
                        return;
                    }
                
                    // Procurar pelos cases e seus blocos
                    while ((line = reader.readLine()) != null && !line.trim().equals("}")) {
                        line = line.trim();
                
                        if (line.startsWith("case")) {
                            if (line.contains(":")) {  
                                // Procurar pelo bloco de cálculo no case
                                Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);
                
                                if (calculationMatcher.find()) {
                                    String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                                    System.out.println("Cálculo identificado no case: " + calculation);
                                } else {
                                    System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do case.");
                                }
                            } else {
                                System.err.println("Erro na linha " + lineNumber + ": case mal formatado.");
                            }
                        } else if (line.startsWith("default")) {
                            if (line.contains(":")) {
                                Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);
                
                                if (calculationMatcher.find()) {
                                    String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                                    System.out.println("Cálculo identificado no default: " + calculation);
                                } else {
                                    System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do default.");
                                }
                            } else {
                                System.err.println("Erro na linha " + lineNumber + ": default mal formatado.");
                            }
                        }
                    }
                } else {
                    System.err.println("Erro na linha " + lineNumber + ": não identificado condição if ou switch --> " + line);
                }
                lineNumber++;
            }
        }

        public void validateLoops() throws IOException {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (Pattern.matches(WHILE_REGEX, line)) {
                    System.out.println("Linha " + lineNumber + " contém um laço while válido.");
                } else if (Pattern.matches(FOR_REGEX, line)) {
                    System.out.println("Linha " + lineNumber + " contém um laço for válido.");
                } else {
                    System.err.println("Erro na linha " + lineNumber + ": comando de laço inválido -> " + line);
                }

                lineNumber++;
            }
        }

    }
}
