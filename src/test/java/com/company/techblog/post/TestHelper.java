package com.company.techblog.post;

import java.lang.reflect.Field;

public class TestHelper {
    // Entity ID 는 GeneratedValue 이므로 builder 필드 미지정
    // ID 기반 로직 테스트가 필요한 경우 리플렉션으로 설정하는 헬퍼 클래스입니다.
    public static void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}