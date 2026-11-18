import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Превращает разобранный JSON в список команд с проверкой полей
 */
public class Translator {
    private final static String OP_KEY = "op";
    private final static int MIN_VALUE = 0;

    /**
     * Переводит всю программу во внутреннее представление
     *
     * @param program команды в виде словарей, как их вернул JsonParser
     * @return список команд
     * @throws AssemblerException если какая-то команда записана неверно
     */
    public static List<Command> translate(List<Map<String, Object>> program)
            throws AssemblerException {
        List<Command> commands = new ArrayList<>();
        for (int i = 0; i < program.size(); i++) {
            try {
                commands.add(translateCommand(program.get(i)));
            } catch (AssemblerException e) {
                throw new AssemblerException("Команда " + (i + 1) +
                        ": " + e.getMessage());
            }
        }
        return commands;
    }

    /**
     * Переводит одну команду
     *
     * @param raw поля команды из JSON
     * @return команда
     * @throws AssemblerException если команда записана неверно
     */
    private static Command translateCommand(Map<String, Object> raw)
            throws AssemblerException {
        if (!(raw.get(OP_KEY) instanceof String name)) {
            throw new AssemblerException("нет поля '" + OP_KEY + "' со " +
                    "строкой-названием");
        }
        Opcode opcode = Opcode.fromMnemonic(name);
        checkUnknownKeys(raw, opcode);
        Map<String, Integer> fields = new HashMap<>();
        for (FieldSpec spec : opcode.getFields()) {
            fields.put(spec.name(), readField(raw, spec));
        }
        return new Command(opcode, fields);
    }

    /**
     * Читает значение поля и проверяет, что оно помещается в свое место
     *
     * @param raw   поля команды из JSON
     * @param field описание поля
     * @return значение поля
     * @throws AssemblerException если поля нет, оно не число
     *                            или не помещается
     */
    private static int readField(Map<String, Object> raw, FieldSpec field)
            throws AssemblerException {
        if (!(raw.get(field.name()) instanceof Integer number)) {
            throw new AssemblerException("поле '" + field.name() + "' должно " +
                    "быть целым числом");
        }
        if (number < MIN_VALUE || number > field.maxValue()) {
            throw new AssemblerException("поле '" + field.name() + "' должно " +
                    "быть от " + MIN_VALUE + " до " + field.maxValue() + ", а " +
                    "не " + number);
        }
        return number;
    }

    /**
     * Проверяет, что в команде нет лишних полей
     *
     * @param raw    поля команды из JSON
     * @param opcode команда
     * @throws AssemblerException если есть поле, которого у этой команды не бывает
     */
    private static void checkUnknownKeys(Map<String, Object> raw, Opcode opcode)
            throws AssemblerException {
        for (String key : raw.keySet()) {
            if (!key.equals(OP_KEY) && !hasField(opcode, key)) {
                throw new AssemblerException("у команды " + opcode.getMnemonic() + " нет " +
                        "поля '" + key + "'");
            }
        }
    }

    /**
     * Проверяет, есть ли у команды поле с таким именем
     *
     * @param opcode команда
     * @param name   имя поля
     * @return true, если есть
     */
    private static boolean hasField(Opcode opcode, String name) {
        for (FieldSpec spec : opcode.getFields()) {
            if (spec.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
