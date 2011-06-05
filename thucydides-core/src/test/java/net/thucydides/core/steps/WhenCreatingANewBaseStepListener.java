package net.thucydides.core.steps;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

public class WhenCreatingANewBaseStepListener {

    @Mock
    WebdriverProxyFactory proxyFactory;

    @Mock
    File outputDirectory;

    @Mock
    FirefoxDriver driver;

    class TestableBaseStepListener extends BaseStepListener {

        public TestableBaseStepListener(final Class<? extends WebDriver> driverClass, final File outputDirectory) {
            super(driverClass, outputDirectory);
        }

        public TestableBaseStepListener(final File outputDirectory, final Pages pages) {
            super(outputDirectory, pages);
        }

        @Override
        protected WebdriverProxyFactory getProxyFactory() {
            return proxyFactory;    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_create_a_new_proxy_driver_using_the_specified_driver_class() {
        BaseStepListener baseStepListener = new TestableBaseStepListener(FirefoxDriver.class, outputDirectory);

        verify(proxyFactory).proxyFor(FirefoxDriver.class);
    }

    @Test
    public void should_create_a_new_proxy_driver_using_the_pages_driver_if_provided() {
        Pages pages = new Pages(driver);
        BaseStepListener baseStepListener = new BaseStepListener(outputDirectory, pages);

        assertThat(baseStepListener.getDriver(), is(pages.getDriver()));
    }

    @Test
    public void should_use_the_driver_from_the_pages_object_if_assigend() {
        BaseStepListener baseStepListener = new TestableBaseStepListener(FirefoxDriver.class, outputDirectory);

        verify(proxyFactory).proxyFor(FirefoxDriver.class);
    }

}
