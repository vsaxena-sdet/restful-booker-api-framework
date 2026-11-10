package com.automation.annotations;

import io.qameta.allure.LabelAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Allure label annotation that sets the {@code layer} label used by:
 * <ul>
 *   <li>Testing pyramid chart — groups tests by layer (unit / integration / e2e)</li>
 *   <li>Durations by layer histogram — shows duration distribution per layer</li>
 * </ul>
 *
 * <p>Valid values match the layers configured in {@code allure.config.js}:
 * {@code "unit"}, {@code "integration"}, {@code "e2e"}.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "layer")
public @interface Layer {
    String value();
}
