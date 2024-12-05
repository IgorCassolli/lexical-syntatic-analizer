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
    private static final String CALCULATION_REGEX = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*.*[+\\-*/]\\s*.*\\b;";


    private static final String INIT_FOR_REGEX = "^(int|Integer)\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*\\d+$";
    private static final String INCREMENT_REGEX = "^[a-zA-Z_][a-zA-Z0-9_]*\\s*(\\+\\+|--|\\+=\\s*\\d+|-=\\s*\\d+|=\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*[+\\-*/]\\s*\\d+)$";


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
                            System.out.println("Condição identificada: " + condition);
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": condição inválida no if --> " + condition);
                        }
                    } else {
                        System.err.println("Erro na linha " + lineNumber + ": não foi possível identificar a condição no if.");
                    }

                    // Se não houver erros na verificação acima, agora iremos validar o que está dentro do { }
                    if(!hasError){
                        Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);

                        if (calculationMatcher.find()) {
                            String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                            System.out.println("Cálculo identificado dentro do bloco: " + calculation);
                            System.out.println("Linha " + lineNumber + " contém um comando if válido.");
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do if.");
                        }
                    }
                } else if (line.startsWith("switch")) {
                    
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
                System.out.println("\n");

                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (line.startsWith("while")) {
                    Matcher conditionMatcher = Pattern.compile(CONDITION_REGEX).matcher(line);
                    boolean hasError = true;
                    if (conditionMatcher.find()) {
                        // Validar a condição dentro do parenteses
                        String condition = conditionMatcher.group(1).trim(); 

                        if (Pattern.matches(VALID_CONDITIONS_REGEX, condition)) {
                            hasError = false;
                            System.out.println("Condição identificada do laço while --> ( " + condition + " )");
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": condição inválida no while --> ( " + condition + " )");
                        }
                    } else {
                        // Retorna erro e já sai da função
                        System.err.println("Erro na linha " + lineNumber + ": Não foi possível identificar a condição no while.");
                    }

                    if(!hasError){
                        Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);

                        if (calculationMatcher.find()) {
                            String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                            System.out.println("Cálculo identificado dentro do bloco while --> " + calculation);
                            System.out.println("Linha " + lineNumber + " contém um comando while válido.");
                        } else {
                            System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do while.");
                        }
                    }
                } else if (line.startsWith("for")) {
                    Matcher conditionMatcher = Pattern.compile(CONDITION_REGEX).matcher(line);
                    boolean hasError = true;
                    if (conditionMatcher.find()) {
                        String condition = conditionMatcher.group(1).trim(); 

                        String[] partsOfFor = condition.split(";");
                        if (partsOfFor.length != 3) {
                            System.err.println("Erro na linha " + lineNumber + ": A estrutura do 'for' deve conter exatamente três partes separadas por ponto e vírgula.");
                        } else {
                            hasError = false;
                        }

                        String initialization = partsOfFor[0].trim();
                        String conditionPart = partsOfFor[1].trim();
                        String increment = partsOfFor[2].trim();
                        
                        if (!Pattern.matches(INIT_FOR_REGEX, initialization)) {
                            System.err.println("Erro na linha " + lineNumber + ": Não identificado variável de inicio no laço for ");
                        } else {
                            hasError = false;
                            System.out.println("Variavel de inicio identificada do laço for --> ( " + initialization + " )");
                        }
                        
                        if (!Pattern.matches(VALID_CONDITIONS_REGEX, conditionPart)) {
                            System.err.println("Erro na linha " + lineNumber + ": condição inválida no laço for --> (" + conditionPart + ") ");
                        } else {
                            hasError = false;
                            System.out.println("Condição identificada do laço for --> ( " + conditionPart + " )");
                        }

                        if (!Pattern.matches(INCREMENT_REGEX, increment)) {
                            System.err.println("Erro na linha " + lineNumber + ": não indentificada variável de incremento no laço for --> ( " + increment + ") ");
                        }  else {
                            hasError = false;
                            System.out.println("Variável de incremento identificada no laço for --> ( " + increment + " )");
                        }

                        if(!hasError){
                            Matcher calculationMatcher = Pattern.compile(CALCULATION_REGEX).matcher(line);

                            if (calculationMatcher.find()) {
                                String calculation = calculationMatcher.group(0).replaceAll("[{}]", "").trim(); // Remove { }
                                System.out.println("Cálculo identificado dentro do bloco for --> " + calculation);
                                System.out.println("Linha " + lineNumber + " contém um comando for válido.");
                            } else {
                                System.err.println("Erro na linha " + lineNumber + ": cálculo inválido ou ausente no bloco do for." + line);
                            } 
                        }
                    } else {
                        System.err.println("Erro na linha " + lineNumber + ": Não foi possível identificar a estrutura do for (ini_variável; condição; incremento/decremento).");
                    }
                }
                lineNumber++;
            }
        }

    }
}
