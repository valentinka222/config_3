import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Разбор JSON: массив объектов, значения
 * которых строки и целые числа
 */
public class JsonParser {

    private final String text;
    private int pos;

    /**
     * @param text текст JSON
     */
    public JsonParser(String text) {
        this.text = text;
        this.pos = 0;
    }

    /**
     * Создает ошибку с номером текущей позиции
     *
     * @param message описание ошибки
     * @return исключение, которое нужно выбросить через throw
     */
    private AssemblerException error(String message) {
        return new AssemblerException("Ошибка JSON в позиции " + pos + ": " + message);
    }

    /**
     * Пропускает пробелы, табуляции и переводы строк
     */
    private void skipSpaces() {
        while (pos < text.length() &&
                Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }

    /**
     * Показывает следующий символ
     *
     * @return следующий символ после пробелов
     * @throws AssemblerException если текст закончился
     */
    private char peek()
            throws AssemblerException {
        skipSpaces();
        if (pos >= text.length()) {
            throw error("неожиданный конец текста");
        }
        return text.charAt(pos);
    }

    /**
     * Проверяет, что дальше стоит нужный символ
     *
     * @param expected ожидаемый символ
     * @throws AssemblerException если символ другой
     */
    private void expect(char expected)
            throws AssemblerException {
        if (peek() != expected) {
            throw error("ожидался символ '" + expected + "'");
        }
        pos++;
    }

    /**
     * Читает строку в кавычках
     *
     * @return содержимое строки без кавычек
     * @throws AssemblerException если строка не закрыта
     */
    private String parseString()
            throws AssemblerException {
        expect('"');
        int start = pos;
        while (pos < text.length() && text.charAt(pos) != '"') {
            pos++;
        }
        if (pos >= text.length()) {
            throw error("строка не закрыта");
        }
        String value = text.substring(start, pos);
        pos++;
        return value;
    }

    /**
     * Читает целое число (может быть с минусом)
     *
     * @return число
     * @throws AssemblerException если на этом месте нет целого числа
     */
    private int parseNumber()
            throws AssemblerException {
        skipSpaces();
        int start = pos;
        if (pos < text.length() && text.charAt(pos) == '-') {
            pos++;
        }
        while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
            pos++;
        }
        try {
            return Integer.parseInt(text.substring(start, pos));
        } catch (NumberFormatException e) {
            throw error("ожидалось целое число");
        }
    }

    /**
     * Читает значение поля: строку в кавычках/целое число
     *
     * @return String или Integer
     * @throws AssemblerException если неверное значение
     */
    private Object parseValue()
            throws AssemblerException {
        if (peek() == '"') {
            return parseString();
        }
        return parseNumber();
    }

    /**
     * Если дальше запятая, переходит за нее
     *
     * @return true, если была запятая
     * @throws AssemblerException если текст закончился
     */
    private boolean skipComma()
            throws AssemblerException {
        if (peek() == ',') {
            pos++;
            return true;
        }
        return false;
    }

    /**
     * Читает объект {"ключ": значение, ...}
     *
     * @return словарь: ключ и значение (String или Integer)
     * @throws AssemblerException если объект записан неверно или ключ повторяется
     */
    private Map<String, Object> parseObject()
            throws AssemblerException {
        Map<String, Object> result = new LinkedHashMap<>();
        expect('{');
        if (peek() == '}') {
            pos++;
            return result;
        }
        do {
            String key = parseString();
            if (result.containsKey(key)) {
                throw error("повторяется ключ '" + key + "'");
            }
            expect(':');
            result.put(key, parseValue());
        } while (skipComma());
        expect('}');
        return result;
    }

    /**
     * Разбирает всю программу: массив объектов-команд
     *
     * @return список команд, каждая - словарь из полей
     * @throws AssemblerException если JSON неверный
     */
    public List<Map<String, Object>> parseProgram()
            throws AssemblerException {
        List<Map<String, Object>> commands = new ArrayList<>();
        expect('[');
        if (peek() == ']') {
            pos++;
        } else {
            do {
                commands.add(parseObject());
            } while (skipComma());
            expect(']');
        }
        skipSpaces();
        if (pos < text.length()) {
            throw error("лишний текст после массива");
        }
        return commands;
    }
}
