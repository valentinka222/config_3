import java.util.Map;

/**
 * Одна команда программы: операция и значения ее полей
 */
public class Command {

    private final Opcode opcode;
    private final Map<String, Integer> fields;

    /**
     * @param opcode операция
     * @param fields значения полей: имя поля (b, c, d) и число
     */
    public Command(Opcode opcode, Map<String, Integer> fields) {
        this.opcode = opcode;
        this.fields = Map.copyOf(fields);
    }

    /**
     * @return операция
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * @param name имя поля: b, c, d
     * @return значение поля
     */
    public int getField(String name) {
        return fields.get(name);
    }

    /**
     * Записывает команду в виде полей и значений
     *
     * @return строка вида A=..., B=..., C=...
     */
    public String describe() {
        StringBuilder text = new StringBuilder("A=" + opcode.getCode());
        for (FieldSpec field : opcode.getFields()) {
            text.append(", ");
            text.append(field.name().toUpperCase());
            text.append("=");
            text.append(fields.get(field.name()));
        }
        return text.toString();
    }
}
