/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.project.templates.freemarker.portlet;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.BaseProjectTemplatesTestCase;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.util.FileTestUtil;

import java.io.File;

import java.net.URI;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Lawrence Lee
 */
@RunWith(Parameterized.class)
public class ProjectTemplatesFreemarkerPortletTest
	implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@Parameterized.Parameters(name = "Testcase-{index}: testing {0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(
			new Object[][] {{"7.0.6"}, {"7.1.3"}, {"7.2.1"}, {"7.3.1"}});
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		String gradleDistribution = System.getProperty("gradle.distribution");

		if (Validator.isNull(gradleDistribution)) {
			Properties properties = FileTestUtil.readProperties(
				"gradle-wrapper/gradle/wrapper/gradle-wrapper.properties");

			gradleDistribution = properties.getProperty("distributionUrl");
		}

		Assert.assertTrue(gradleDistribution.contains(GRADLE_WRAPPER_VERSION));

		_gradleDistribution = URI.create(gradleDistribution);
	}

	public ProjectTemplatesFreemarkerPortletTest(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplateFreemarkerPortlet() throws Exception {
		File gradleProjectDir = testBuildTemplatePortlet(
			temporaryFolder, "freemarker-portlet", "foo", "foo",
			_liferayVersion, mavenExecutor, _gradleDistribution);

		testContains(
			gradleProjectDir, "bnd.bnd", "Export-Package: foo.constants");
		testContains(
			gradleProjectDir, "src/main/java/foo/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");
		testContains(
			gradleProjectDir, "src/main/java/foo/portlet/FooPortlet.java",
			"javax.portlet.display-name=Foo",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends FreeMarkerPortlet {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.foo_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static URI _gradleDistribution;

	private final String _liferayVersion;

}