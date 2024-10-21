package bandung.se;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * naming convention test
 *
 * @author iMinusMinus
 * @date 2024-10-21
 */
public class NamingConventionTest {

    @Test
    public void testLowerCamelCaseToPascalCase() {
        Assertions.assertEquals("PascalCase", NamingConvention.LOWER_CAMEL_CASE.translate("pascalCase", NamingConvention.PASCAL_CASE));
    }

    @Test
    public void testLowerCamelCaseToSnakeCase() {
        Assertions.assertEquals("snake_case", NamingConvention.LOWER_CAMEL_CASE.translate("snakeCase", NamingConvention.SNAKE_CASE));
    }

    @Test
    public void testLowerCamelCaseToKebabCase() {
        Assertions.assertEquals("kebab-case", NamingConvention.LOWER_CAMEL_CASE.translate("kebabCase", NamingConvention.KEBAB_CASE));
    }

    @Test
    public void testPascalCaseToLowerCamelCase() {
        Assertions.assertEquals("pascalCase", NamingConvention.PASCAL_CASE.translate("PascalCase", NamingConvention.LOWER_CAMEL_CASE));
    }

    @Test
    public void testPascalCaseToSnakeCase() {
        Assertions.assertEquals("snake_case", NamingConvention.PASCAL_CASE.translate("SnakeCase", NamingConvention.SNAKE_CASE));
    }

    @Test
    public void testPascalCaseToKebabCase() {
        Assertions.assertEquals("kebab-case", NamingConvention.PASCAL_CASE.translate("KebabCase", NamingConvention.KEBAB_CASE));
    }

    @Test
    public void testSnakeCaseToKebabCase() {
        Assertions.assertEquals("kebab-case", NamingConvention.SNAKE_CASE.translate("kebab_case", NamingConvention.KEBAB_CASE));
    }

    @Test
    public void testKebabCaseToSnakeCase() {
        Assertions.assertEquals("snake_case", NamingConvention.KEBAB_CASE.translate("snake-case", NamingConvention.SNAKE_CASE));
    }

    @Test
    public void testKebabCaseToPascalCase() {
        Assertions.assertEquals("PascalCase", NamingConvention.KEBAB_CASE.translate("pascal-case", NamingConvention.PASCAL_CASE));
    }

    @Test
    public void testKebabCaseToLowerCamelCase() {
        Assertions.assertEquals("lowerCamelCase", NamingConvention.KEBAB_CASE.translate("lower-camel-case", NamingConvention.LOWER_CAMEL_CASE));
    }

    @Test
    public void testSnakeCaseToPascalCase() {
        Assertions.assertEquals("PascalCase", NamingConvention.SNAKE_CASE.translate("pascal_case", NamingConvention.PASCAL_CASE));
    }

    @Test
    public void testSnakeCaseTLowerCamelCase() {
        Assertions.assertEquals("lowerCamelCase", NamingConvention.SNAKE_CASE.translate("lower_camel_case", NamingConvention.LOWER_CAMEL_CASE));
    }

    @Test
    public void testPascalCaseTLowerCamelCase() {
        Assertions.assertEquals("lowerCamelCase", NamingConvention.PASCAL_CASE.translate("LowerCamelCase", NamingConvention.LOWER_CAMEL_CASE));
    }
}
