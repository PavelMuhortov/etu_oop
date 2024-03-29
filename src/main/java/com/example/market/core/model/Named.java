package com.example.market.core.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Предоставляет возможность указать имя поля для отображения
 * {@link BaseModel.PropDef#propertyDisplayedName}
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {

    /**
     * @return имя для отображения
     */
    String value();
}
