package br.edu.ufc.quixada.sd.t1.protocol;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonCodec {
    private JsonCodec() {
    }

    public static String toJson(Object value) {
        StringBuilder builder = new StringBuilder();
        writeJson(builder, value);
        return builder.toString();
    }

    public static <T> T fromJson(String json, Class<T> type) throws IOException {
        Object parsed = new Parser(json).parseValue();
        return convertValue(parsed, type);
    }

    private static void writeJson(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
            return;
        }

        if (value instanceof String || value instanceof Character) {
            appendQuoted(builder, String.valueOf(value));
            return;
        }

        if (value instanceof Enum<?>) {
            appendQuoted(builder, ((Enum<?>) value).name());
            return;
        }

        if (value instanceof Number || value instanceof Boolean) {
            builder.append(value);
            return;
        }

        if (value instanceof Instant) {
            appendQuoted(builder, value.toString());
            return;
        }

        if (value instanceof Map<?, ?>) {
            builder.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                appendQuoted(builder, String.valueOf(entry.getKey()));
                builder.append(':');
                writeJson(builder, entry.getValue());
            }
            builder.append('}');
            return;
        }

        if (value instanceof Iterable<?>) {
            builder.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                writeJson(builder, item);
            }
            builder.append(']');
            return;
        }

        if (value.getClass().isArray()) {
            builder.append('[');
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++) {
                if (index > 0) {
                    builder.append(',');
                }
                writeJson(builder, Array.get(value, index));
            }
            builder.append(']');
            return;
        }

        Map<String, Object> fields = new LinkedHashMap<>();
        Class<?> current = value.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    fields.put(field.getName(), field.get(value));
                } catch (IllegalAccessException exception) {
                    throw new IllegalStateException("Falha ao serializar objeto.", exception);
                }
            }
            current = current.getSuperclass();
        }
        writeJson(builder, fields);
    }

    private static void appendQuoted(StringBuilder builder, String value) {
        builder.append('"');
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        builder.append(String.format("\\u%04x", (int) character));
                    } else {
                        builder.append(character);
                    }
                    break;
            }
        }
        builder.append('"');
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertValue(Object value, Class<T> type) throws IOException {
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }

        if (type == String.class) {
            return type.cast(String.valueOf(value));
        }

        if (type == Integer.class || type == int.class) {
            return (T) Integer.valueOf(((Number) value).intValue());
        }
        if (type == Long.class || type == long.class) {
            return (T) Long.valueOf(((Number) value).longValue());
        }
        if (type == Double.class || type == double.class) {
            return (T) Double.valueOf(((Number) value).doubleValue());
        }
        if (type == Float.class || type == float.class) {
            return (T) Float.valueOf(((Number) value).floatValue());
        }
        if (type == Boolean.class || type == boolean.class) {
            if (value instanceof Boolean) {
                return (T) value;
            }
            return (T) Boolean.valueOf(String.valueOf(value));
        }
        if (type == Short.class || type == short.class) {
            return (T) Short.valueOf(((Number) value).shortValue());
        }
        if (type == Byte.class || type == byte.class) {
            return (T) Byte.valueOf(((Number) value).byteValue());
        }
        if (type == BigDecimal.class) {
            return (T) new BigDecimal(String.valueOf(value));
        }
        if (type == BigInteger.class) {
            return (T) new BigInteger(String.valueOf(value));
        }
        if (type == Instant.class) {
            return type.cast(Instant.parse(String.valueOf(value)));
        }
        if (type.isEnum()) {
            return (T) Enum.valueOf((Class<? extends Enum>) type.asSubclass(Enum.class), String.valueOf(value));
        }
        if (Map.class.isAssignableFrom(type)) {
            return type.cast(value);
        }
        if (List.class.isAssignableFrom(type)) {
            return type.cast(value);
        }

        if (!(value instanceof Map<?, ?>)) {
            throw new IOException("Não foi possível converter o valor JSON para " + type.getName());
        }

        Map<String, Object> data = (Map<String, Object>) value;
        T instance;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (Exception exception) {
            throw new IOException("Não foi possível instanciar " + type.getName(), exception);
        }

        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                if (!data.containsKey(field.getName())) {
                    continue;
                }

                Object rawValue = data.get(field.getName());
                Object convertedValue = convertNested(rawValue, field.getType());

                try {
                    field.setAccessible(true);
                    field.set(instance, convertedValue);
                } catch (IllegalAccessException exception) {
                    throw new IOException("Não foi possível definir o campo " + field.getName(), exception);
                }
            }
            current = current.getSuperclass();
        }

        return instance;
    }

    private static Object convertNested(Object value, Class<?> type) throws IOException {
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (type == String.class) {
            return String.valueOf(value);
        }
        if (type == Instant.class) {
            return Instant.parse(String.valueOf(value));
        }
        if (type.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object enumValue = Enum.valueOf((Class<? extends Enum>) type.asSubclass(Enum.class), String.valueOf(value));
            return enumValue;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            if (type == int.class || type == Integer.class) {
                return number.intValue();
            }
            if (type == long.class || type == Long.class) {
                return number.longValue();
            }
            if (type == double.class || type == Double.class) {
                return number.doubleValue();
            }
            if (type == float.class || type == Float.class) {
                return number.floatValue();
            }
            if (type == short.class || type == Short.class) {
                return number.shortValue();
            }
            if (type == byte.class || type == Byte.class) {
                return number.byteValue();
            }
        }
        if (Map.class.isAssignableFrom(type) || List.class.isAssignableFrom(type)) {
            return value;
        }
        if (value instanceof Map<?, ?>) {
            return convertValue(value, type);
        }
        return value;
    }

    private static final class Parser {
        private final String text;
        private int index;

        private Parser(String text) {
            this.text = text.trim();
        }

        private Object parseValue() throws IOException {
            skipWhitespace();
            if (index >= text.length()) {
                throw new IOException("JSON inesperado.");
            }

            char character = text.charAt(index);
            switch (character) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                    expect("true");
                    return Boolean.TRUE;
                case 'f':
                    expect("false");
                    return Boolean.FALSE;
                case 'n':
                    expect("null");
                    return null;
                default:
                    if (character == '-' || Character.isDigit(character)) {
                        return parseNumber();
                    }
                    throw new IOException("Valor JSON inválido na posição " + index);
            }
        }

        private Map<String, Object> parseObject() throws IOException {
            Map<String, Object> object = new LinkedHashMap<>();
            consume('{');
            skipWhitespace();
            if (peek('}')) {
                consume('}');
                return object;
            }

            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                consume(':');
                Object value = parseValue();
                object.put(key, value);
                skipWhitespace();
                if (peek(',')) {
                    consume(',');
                    continue;
                }
                if (peek('}')) {
                    consume('}');
                    return object;
                }
                throw new IOException("Objeto JSON inválido na posição " + index);
            }
        }

        private List<Object> parseArray() throws IOException {
            List<Object> array = new ArrayList<>();
            consume('[');
            skipWhitespace();
            if (peek(']')) {
                consume(']');
                return array;
            }

            while (true) {
                array.add(parseValue());
                skipWhitespace();
                if (peek(',')) {
                    consume(',');
                    continue;
                }
                if (peek(']')) {
                    consume(']');
                    return array;
                }
                throw new IOException("Array JSON inválido na posição " + index);
            }
        }

        private String parseString() throws IOException {
            consume('"');
            StringBuilder builder = new StringBuilder();
            while (index < text.length()) {
                char character = text.charAt(index++);
                if (character == '"') {
                    return builder.toString();
                }
                if (character == '\\') {
                    if (index >= text.length()) {
                        throw new IOException("Escape JSON inválido.");
                    }
                    char escaped = text.charAt(index++);
                    switch (escaped) {
                        case '"':
                        case '\\':
                        case '/':
                            builder.append(escaped);
                            break;
                        case 'b':
                            builder.append('\b');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'u':
                            if (index + 4 > text.length()) {
                                throw new IOException("Unicode escape inválido.");
                            }
                            String hex = text.substring(index, index + 4);
                            builder.append((char) Integer.parseInt(hex, 16));
                            index += 4;
                            break;
                        default:
                            throw new IOException("Escape JSON desconhecido: "+ escaped);
                    }
                } else {
                    builder.append(character);
                }
            }
            throw new IOException("String JSON não terminada.");
        }

        private Number parseNumber() {
            int start = index;
            if (text.charAt(index) == '-') {
                index++;
            }
            while (index < text.length() && Character.isDigit(text.charAt(index))) {
                index++;
            }
            boolean decimal = false;
            if (index < text.length() && text.charAt(index) == '.') {
                decimal = true;
                index++;
                while (index < text.length() && Character.isDigit(text.charAt(index))) {
                    index++;
                }
            }
            if (index < text.length()) {
                char exponent = text.charAt(index);
                if (exponent == 'e' || exponent == 'E') {
                    decimal = true;
                    index++;
                    if (index < text.length()) {
                        char sign = text.charAt(index);
                        if (sign == '+' || sign == '-') {
                            index++;
                        }
                    }
                    while (index < text.length() && Character.isDigit(text.charAt(index))) {
                        index++;
                    }
                }
            }
            String number = text.substring(start, index);
            if (decimal) {
                return Double.valueOf(number);
            }
            return Long.valueOf(number);
        }

        private void skipWhitespace() {
            while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
                index++;
            }
        }

        private void consume(char expected) throws IOException {
            skipWhitespace();
            if (index >= text.length() || text.charAt(index) != expected) {
                throw new IOException("Esperado '" + expected + "' na posição " + index);
            }
            index++;
        }

        private boolean peek(char expected) {
            skipWhitespace();
            return index < text.length() && text.charAt(index) == expected;
        }

        private void expect(String keyword) throws IOException {
            if (!text.startsWith(keyword, index)) {
                throw new IOException("Esperado '" + keyword + "' na posição " + index);
            }
            index += keyword.length();
        }
    }
}