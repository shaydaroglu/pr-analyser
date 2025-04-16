package testutils;

import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReflectionTestUtils {
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
