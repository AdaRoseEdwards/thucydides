package net.thucydides.core.model

import spock.lang.Specification
import spock.lang.Unroll
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.annotations.Screenshots
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import net.thucydides.core.util.MockEnvironmentVariables
import com.google.common.base.Optional

class WhenDecidingWhetherToTakeScreenshots extends Specification {

    def configuration = Mock(Configuration)

    @Unroll
    def "should be able to configure screenshots using the legacy system properties"() {

        when:
            configuration.takeVerboseScreenshots() >> takeVerboseScreenshots
            configuration.onlySaveFailingScreenshots() >> onlyTakeFailingverboseScreenshots
            configuration.screenshotLevel >> Optional.absent()
            ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        then:
            permissions.areAllowed(screenshotLevel) == shouldBeAllowed

        where:
        onlyTakeFailingverboseScreenshots | takeVerboseScreenshots | screenshotLevel                            | shouldBeAllowed
        true                              | true                    | TakeScreenshots.FOR_EACH_ACTION           | false
        true                              | true                    | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP| false
        true                              | true                    | TakeScreenshots.AFTER_EACH_STEP           | false
        true                              | true                    | TakeScreenshots.FOR_FAILURES              | true

        false                             | false                   | TakeScreenshots.FOR_EACH_ACTION           | false
        false                             | false                   | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP| true
        false                             | false                   | TakeScreenshots.AFTER_EACH_STEP           | true
        false                             | false                   | TakeScreenshots.FOR_FAILURES              | true

        false                             | true                    | TakeScreenshots.FOR_EACH_ACTION           | true
        false                             | true                    | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP| true
        false                             | true                    | TakeScreenshots.AFTER_EACH_STEP           | true
        false                             | true                    | TakeScreenshots.FOR_FAILURES              | true
    }


    def environmentVariables = new MockEnvironmentVariables()
    def systemPropConfiguration = new SystemPropertiesConfiguration(environmentVariables)

    @Unroll
    def "should be able to configure screenshot level via the new thucydides.take.screenshots system properties"() {

        when:
            environmentVariables.setProperty("thucydides.take.screenshots", takeScreenshotsConfiguration)
            ScreenshotPermission permissions = new ScreenshotPermission(systemPropConfiguration)

        then:
            permissions.areAllowed(screenshotLevel) == shouldBeAllowed

        where:
        takeScreenshotsConfiguration | screenshotLevel                            | shouldBeAllowed
        "FOR_EACH_ACTION"            | TakeScreenshots.FOR_EACH_ACTION            | true
        "FOR_EACH_ACTION"            | TakeScreenshots.FOR_FAILURES               | true
        "FOR_EACH_ACTION"            | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        "FOR_EACH_ACTION"            | TakeScreenshots.AFTER_EACH_STEP            | true

        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.FOR_EACH_ACTION            | false
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.FOR_FAILURES               | true
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | true
        "BEFORE_AND_AFTER_EACH_STEP" | TakeScreenshots.AFTER_EACH_STEP            | true

        "AFTER_EACH_STEP"            | TakeScreenshots.FOR_EACH_ACTION            | false
        "AFTER_EACH_STEP"            | TakeScreenshots.FOR_FAILURES               | true
        "AFTER_EACH_STEP"            | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | false
        "AFTER_EACH_STEP"            | TakeScreenshots.AFTER_EACH_STEP            | true

        "FOR_FAILURES"               | TakeScreenshots.FOR_EACH_ACTION            | false
        "FOR_FAILURES"               | TakeScreenshots.FOR_FAILURES               | true
        "FOR_FAILURES"               | TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP | false
        "FOR_FAILURES"               | TakeScreenshots.AFTER_EACH_STEP            | false
    }

    def "overriding screenshot configuration using method annotations"() {
        when:
            configuration.takeVerboseScreenshots() >> true
        then:
            checkOnlyTakeOnFailures()
            checkOnEachStep()
            checkOnBeforeAndAfterEachStep()
            checkDefaultScreenshotPermissions()
    }

    def "overriding only-on-failure screenshot configuration using method annotations"() {
        when:
            configuration.onlySaveFailingScreenshots() >> true
        then:
            checkOverrideOnlyOnFailure()
            checkOverrideOnlyOnFailureWithPerStepMode()
            checkOverrideOnlyOnFailureWithVerboseMode()
    }

    @Screenshots(onlyOnFailures=true)
    void checkOnlyTakeOnFailures() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert !permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(afterEachStep=true)
    void checkOnEachStep() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert !permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(beforeAndAfterEachStep=true)
    void checkOnBeforeAndAfterEachStep() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }


    @Screenshots()
    void checkDefaultScreenshotPermissions() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots()
    void checkOverrideOnlyOnFailure() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(afterEachStep=true)
    void checkOverrideOnlyOnFailureWithPerStepMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert !permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }

    @Screenshots(forEachAction=true)
    void checkOverrideOnlyOnFailureWithVerboseMode() {
        ScreenshotPermission permissions = new ScreenshotPermission(configuration)

        assert permissions.areAllowed(TakeScreenshots.FOR_EACH_ACTION)
        assert permissions.areAllowed(TakeScreenshots.AFTER_EACH_STEP)
        assert permissions.areAllowed(TakeScreenshots.FOR_FAILURES)
    }
}
