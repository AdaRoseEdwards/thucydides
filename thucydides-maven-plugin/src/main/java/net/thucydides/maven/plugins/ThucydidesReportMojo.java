package net.thucydides.maven.plugins;

import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Thucydides Maven site integration
 * This plugin generates an aggregate Thucydides report and integrates it into the Maven-generated site.
 * @goal thucydides
 * @requiresReports true
 * @phase site
 */
public class ThucydidesReportMojo extends AbstractMavenReport {
        /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Aggregate reports are generated here
     * @parameter expression="${thucydides.outputDirectory}" default-value="${project.build.directory}/site/thucydides/"
     * @required
     */
    public String outputDirectory;

    /**
     * Thucydides test reports are read from here
     *
     * @parameter expression="${thucydides.sourceDirectory}" default-value="${project.build.directory}/site/thucydides/"
     * @required
     */
    public File sourceDirectory;

   /**
     * @component
     * @required
     * @readonly
     */
    private Renderer siteRenderer;

    private HtmlAggregateStoryReporter reporter;

    private ThucydidesHTMLReportGenerator htmlReportGenerator;

    @Override
    protected MavenProject getProject() {
        return project;
    }

    protected ThucydidesHTMLReportGenerator getHtmlReportGenerator() {
        if (htmlReportGenerator == null) {
            htmlReportGenerator = new ThucydidesHTMLReportGenerator();
        }
        return htmlReportGenerator;
    }

    // Not used by Maven site plugin but required by API!
    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    // Not used by Maven site plugin but required by API!
    // (The site plugin is only calling getOutputName(), the output dir is fixed!)
    @Override
    protected String getOutputDirectory() {
        return outputDirectory;
    }

    protected File getThucydidesOutputDirectory() {
        return new File(outputDirectory);
    }

    public String getOutputName() {
        return "thucydides";
    }

    public String getName(Locale locale) {
        return "Thucydides Web tests";
    }

    public String getDescription(Locale locale) {
        return "Test reports generated by Thucydides.";
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        getLog().info("Generating Thucydides Reports");
        TestOutcomes testOutcomes = generateHtmlReports();

        getHtmlReportGenerator().generateReport(testOutcomes, getSink());

    }

    protected HtmlAggregateStoryReporter getReporter() {
        if (reporter == null) {
            reporter = new HtmlAggregateStoryReporter(MavenProjectHelper.getProjectIdentifier(project));
        }
        return reporter;

    }

    private TestOutcomes generateHtmlReports() throws MavenReportException {
        getReporter().setOutputDirectory(getThucydidesOutputDirectory());

        try {
            return getReporter().generateReportsForTestResultsFrom(sourceDirectory);
        } catch (IOException e) {
            throw new MavenReportException("Error generating aggregate thucydides reports", e);
        }
    }

}
