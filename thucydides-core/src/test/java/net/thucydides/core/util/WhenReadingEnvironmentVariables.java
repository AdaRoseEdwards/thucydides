package net.thucydides.core.util;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenReadingEnvironmentVariables {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();


    @Test
    public void should_read_environment_variable_from_system() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("PATH");
        assertThat(value, is(not(nullValue())));
    }

    @Test
    public void should_return_null_for_inexistant_environment_variable() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void should_return_default_for_inexistant_environment_variable_if_specified() {
        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getValue("DOES_NOT_EXIST","DEFAULT");
        assertThat(value, is("DEFAULT"));
    }

    @Test
    public void should_read_system_properties_from_the_system() {
        System.setProperty("webdriver.base.url","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("webdriver.base.url");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_system_properties_with_default_values_from_the_system() {
        System.setProperty("webdriver.base.url","some.value");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("webdriver.base.url", "DEFAULT");
        assertThat(value, is("some.value"));
    }

    @Test
    public void should_read_default_system_properties_with_default_values_from_the_system() {
        System.clearProperty("webdriver.base.url");

        EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
        String value = environmentVariables.getProperty("webdriver.base.url", "DEFAULT");
        assertThat(value, is("DEFAULT"));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("property","value");
        assertThat(environmentVariables.getProperty("property"), is("value"));
    }

    @Test
    public void mock_environment_variables_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getProperty("property","default"), is("default"));
    }

    @Test
    public void mock_environment_variables_allow_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("property","30");
        assertThat(environmentVariables.getPropertyAsInteger("property", 0), is(30));
    }

    @Test
    public void mock_environment_variables_allow_default_integer_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsInteger("property", 10), is(10));
    }

    @Test
    public void mock_environment_variables_allow_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("property","true");
        assertThat(environmentVariables.getPropertyAsBoolean("property", false), is(true));
    }

    @Test
    public void mock_environment_variables_allow_default_boolean_properties() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getPropertyAsBoolean("property", true), is(true));
    }

    @Test
    public void mock_environment_variables_can_be_used_for_testing_environment_values_in_other_modules() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setValue("env","value");
        assertThat(environmentVariables.getValue("env"), is("value"));
    }

    @Test
    public void mock_environment_values_allow_defaults() {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        assertThat(environmentVariables.getValue("env","default"), is("default"));
    }

}
