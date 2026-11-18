import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Ассемблер УВМ: читает программу в формате JSON и переводит ее команды
 */
public class Assembler {
    private final static int MIN_ARGS = 2;
    private final static int MAX_ARGS = 3;
    private final static int SOURCE_INDEX = 0;
    private final static int TEST_INDEX = 2;
    private final static String TEST_FLAG = "--test";
    private final static String USAGE = "Использование: " +
            "Assembler <программа.json> " +
            "<результат.bin> [--test]";

    /**
     * Точка входа ассемблера
     *
     * @param args путь к программе, путь к результату, необязательный флаг --test
     */
    public static void main(String[] args) {
        try {
            boolean testMode = parseArguments(args);
            String text = readSource(args[SOURCE_INDEX]);
            List<Command> commands = Translator.translate(new JsonParser(text).parseProgram());
            report(commands, testMode);
        } catch (AssemblerException e) {
            System.out.println("Ошибка ассемблера: " + e.getMessage());
        }
    }

    /**
     * Проверяет аргументы командной строки
     *
     * @param args аргументы
     * @return true, если включен режим тестирования
     * @throws AssemblerException если аргументов не хватает или флаг неизвестен
     */
    private static boolean parseArguments(String[] args)
            throws AssemblerException {
        if (args.length < MIN_ARGS || args.length > MAX_ARGS) {
            throw new AssemblerException(USAGE);
        }
        if (args.length == MAX_ARGS && !args[TEST_INDEX].equals(TEST_FLAG)) {
            throw new AssemblerException("Неизвестный параметр '" + args[TEST_INDEX]
                    + "'. " + USAGE);
        }
        return args.length == MAX_ARGS;
    }

    /**
     * Читает текст исходного файла
     *
     * @param path путь к файлу
     * @return содержимое файла
     * @throws AssemblerException если файл не удалось прочитать
     */
    private static String readSource(String path)
            throws AssemblerException {
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AssemblerException("Не удалось прочитать файл " + path);
        }
    }

    /**
     * Выводит результат работы
     *
     * @param commands список команд
     * @param testMode true - вывести поля и значения каждой команды
     */
    private static void report(List<Command> commands, boolean testMode) {
        if (testMode) {
            for (Command command : commands) {
                System.out.println(command.describe());
            }
        } else {
            System.out.println("Разобрано команд: " + commands.size());
        }
    }
}
