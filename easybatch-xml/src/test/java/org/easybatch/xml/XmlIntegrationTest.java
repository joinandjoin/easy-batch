/*
 *  The MIT License
 *
 *   Copyright (c) 2015, Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package org.easybatch.xml;

import org.easybatch.core.api.Job;
import org.easybatch.core.api.Report;
import org.easybatch.core.api.Status;
import org.easybatch.core.impl.JobBuilder;
import org.easybatch.core.processor.RecordCollector;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class XmlIntegrationTest {

    private static final String EXPECTED_DATA_SOURCE_NAME = "XML stream";

    @Test
    public void testWebsitesProcessing() throws Exception {
        final InputStream xmlDataSource = getDataSource("/websites.xml");
        Job job = JobBuilder.aNewJob()
                .reader(new XmlRecordReader("website", xmlDataSource))
                .mapper(new XmlRecordMapper(Website.class))
                .processor(new RecordCollector<Website>())
                .build();

        Report report = job.call();

        List<Website> websites = (List<Website>) report.getJobResult();

        assertThat(websites).isNotEmpty().hasSize(3);

        Website website = websites.get(0);
        assertThat(website.getName()).isEqualTo("google");
        assertThat(website.getUrl()).isEqualTo("http://www.google.com?query=test&sort=asc");

        website = websites.get(1);
        assertThat(website.getName()).isEqualTo("l'equipe");
        assertThat(website.getUrl()).isEqualTo("http://www.lequipe.fr");

        website = websites.get(2);
        assertThat(website.getName()).isEqualTo("l\"internaute.com");
        assertThat(website.getUrl()).isEqualTo("http://www.linternaute.com");
    }

    @Test
    public void testPersonsProcessing() throws Exception {

        final InputStream xmlDataSource = getDataSource("/persons.xml");

        Job job = JobBuilder.aNewJob()
                .reader(new XmlRecordReader("person", xmlDataSource))
                .mapper(new XmlRecordMapper<Person>(Person.class))
                .processor(new RecordCollector<Person>())
                .build();

        Report report = job.call();

        assertThatReportIsCorrect(report);

        List<Person> persons = (List<Person>) report.getJobResult();

        assertThat(persons).isNotEmpty().hasSize(2);

        Person person = persons.get(0);
        assertThat(person.getId()).isEqualTo(1);
        assertThat(person.getFirstName()).isEqualTo("foo");
        assertThat(person.getLastName()).isEqualTo("bar");
        assertThat(person.isMarried()).isTrue();

        person = persons.get(1);
        assertThat(person.getId()).isEqualTo(2);
        assertThat(person.getFirstName()).isEqualTo("bar");
        assertThat(person.getLastName()).isEqualTo("foo");
        assertThat(person.isMarried()).isFalse();

    }

    @Test
    public void testMavenDependenciesProcessing() throws Exception {

        final InputStream xmlDataSource = getDataSource("/dependencies.xml");

        Job job = JobBuilder.aNewJob()
                .reader(new XmlRecordReader("dependency", xmlDataSource))
                .mapper(new XmlRecordMapper<Dependency>(Dependency.class))
                .processor(new RecordCollector<Dependency>())
                .build();

        Report report = job.call();

        assertThatReportIsCorrect(report);

        List<Dependency> dependencies = (List<Dependency>) report.getJobResult();

        assertThat(dependencies).isNotEmpty().hasSize(2);

        Dependency dependency = dependencies.get(0);
        assertThat(dependency).isNotNull();
        assertThat(dependency.getArtifactId()).isEqualTo("junit");
        assertThat(dependency.getGroupId()).isEqualTo("junit");
        assertThat(dependency.getVersion()).isEqualTo("4.12");
        assertThat(dependency.getScope()).isEqualTo("test");
        assertThat(dependency.getClassifier()).isNull();
        assertThat(dependency.getSystemPath()).isNull();
        assertThat(dependency.getType()).isNull();
        assertThat(dependency.getExclusions()).isNull();
        assertThat(dependency.isOptional()).isFalse();

        dependency = dependencies.get(1);
        assertThat(dependency).isNotNull();
        assertThat(dependency.getArtifactId()).isEqualTo("fake-core");
        assertThat(dependency.getGroupId()).isEqualTo("org.fake");
        assertThat(dependency.getVersion()).isEqualTo("1.0");
        assertThat(dependency.getScope()).isNull();
        assertThat(dependency.getClassifier()).isNull();
        assertThat(dependency.getSystemPath()).isNull();
        assertThat(dependency.getType()).isNull();
        assertThat(dependency.isOptional()).isTrue();

        Dependency.Exclusions exclusions = dependency.getExclusions();
        assertThat(exclusions).isNotNull();
        assertThat(exclusions.getExclusion()).hasSize(1);

        Exclusion exclusion = exclusions.getExclusion().get(0);
        assertThat(exclusion).isNotNull();
        assertThat(exclusion.getGroupId()).isNotNull().isEqualTo("some.excluded.dep");
        assertThat(exclusion.getArtifactId()).isNotNull().isEqualTo("dep-core");


    }

    @Test
    public void testXmlProcessingWithCustomNamespace() throws Exception {

        final InputStream xmlDataSource = getDataSource("/beans.xml");

        Job job = JobBuilder.aNewJob()
                .reader(new XmlRecordReader("bean", xmlDataSource))
                .mapper(new XmlRecordMapper(Bean.class))
                .processor(new RecordCollector<Bean>())
                .build();

        Report report = job.call();

        assertThat(report).isNotNull();
        assertThat(report.getTotalRecords()).isEqualTo(2);

        List<Bean> beans = (List<Bean>) report.getJobResult();

        assertThat(beans).isNotEmpty().hasSize(2);

        Bean bean = beans.get(0);
        assertThat(bean).isNotNull();
        assertThat(bean.getId()).isEqualTo("foo");
        assertThat(bean.getClazz()).isEqualTo("java.lang.String");

        bean = beans.get(1);
        assertThat(bean).isNotNull();
        assertThat(bean.getId()).isEqualTo("bar");
        assertThat(bean.getClazz()).isEqualTo("java.lang.String");

    }

    private void assertThatReportIsCorrect(Report report) {
        assertThat(report).isNotNull();
        assertThat(report.getTotalRecords()).isEqualTo(2);
        assertThat(report.getErrorRecordsCount()).isEqualTo(0);
        assertThat(report.getFilteredRecordsCount()).isEqualTo(0);
        assertThat(report.getSuccessRecordsCount()).isEqualTo(2);
        assertThat(report.getStatus()).isEqualTo(Status.FINISHED);
        assertThat(report.getDataSource()).isEqualTo(EXPECTED_DATA_SOURCE_NAME);
    }

    private InputStream getDataSource(String name) {
        return this.getClass().getResourceAsStream(name);
    }

}
