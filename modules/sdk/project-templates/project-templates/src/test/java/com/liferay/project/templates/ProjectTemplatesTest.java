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

package com.liferay.project.templates;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.WorkspaceUtil;
import com.liferay.project.templates.util.FileTestUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.compress.archivers.zip.ZipFile;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Lawrence Lee
 * @author Gregory Amerson
 * @author Andrea Di Giorgi
 */
public class ProjectTemplatesTest implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

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

		XPathFactory xPathFactory = XPathFactory.newInstance();

		XPath xPath = xPathFactory.newXPath();

		_pomXmlNpmInstallXPathExpression = xPath.compile(
			"//id[contains(text(),'npm-install')]/parent::*");
	}

	@Test
	public void testBuildTemplate() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			null, "hello-world-portlet");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir, "src/main/resources/META-INF/resources/init.jsp");
		testExists(
			gradleProjectDir, "src/main/resources/META-INF/resources/view.jsp");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"");
		testContains(
			gradleProjectDir,
			"src/main/java/hello/world/portlet/portlet/HelloWorldPortlet.java",
			"public class HelloWorldPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "mvc-portlet", "hello-world-portlet", "com.test",
			mavenExecutor, "-DclassName=HelloWorld",
			"-Dpackage=hello.world.portlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateContentDTDVersionLayoutTemplate70()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"layout-template", "foo-bar", "--liferay-version", "7.0.6");

		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-layout-templates.xml",
			"liferay-layout-templates_7_0_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionLayoutTemplate71()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"layout-template", "foo-bar", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-layout-templates.xml",
			"liferay-layout-templates_7_1_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionLayoutTemplate72()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"layout-template", "foo-bar", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-layout-templates.xml",
			"liferay-layout-templates_7_2_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionLayoutTemplate73()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"layout-template", "foo-bar", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-layout-templates.xml",
			"liferay-layout-templates_7_3_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarHook70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "foo-bar", "--liferay-version", "7.0.6");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
			"liferay-hook_7_0_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarHook71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "foo-bar", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
			"liferay-hook_7_1_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarHook72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "foo-bar", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
			"liferay-hook_7_2_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarHook73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "foo-bar", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
			"liferay-hook_7_3_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarMVCPortlet70()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "foo-bar", "--liferay-version", "7.0.6");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
			"liferay-display_7_0_0.dtd");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
			"liferay-portlet-app_7_0_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarMVCPortlet71()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "foo-bar", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
			"liferay-display_7_1_0.dtd");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
			"liferay-portlet-app_7_1_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarMVCPortlet72()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "foo-bar", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
			"liferay-display_7_2_0.dtd");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
			"liferay-portlet-app_7_2_0.dtd");
	}

	@Test
	public void testBuildTemplateContentDTDVersionWarMVCPortlet73()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "foo-bar", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
			"liferay-display_7_3_0.dtd");

		testContains(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
			"liferay-portlet-app_7_3_0.dtd");
	}

	@Test
	public void testBuildTemplateFormField70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"form-field", "foobar", "--liferay-version", "7.0.6");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar",
			"Web-ContextPath: /dynamic-data-foobar-form-field");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java",
			"property = \"ddm.form.field.type.name=foobar\"",
			"public class FoobarDDMFormFieldRenderer extends " +
				"BaseDDMFormFieldRenderer {",
			"ddm.Foobar", "/META-INF/resources/foobar.soy");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldType.java",
			"ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar",
			"ddm.form.field.type.js.module=foobar-form-field",
			"ddm.form.field.type.label=foobar-label",
			"ddm.form.field.type.name=foobar",
			"public class FoobarDDMFormFieldType extends BaseDDMFormFieldType",
			"return \"foobar\";");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/config.js",
			"foobar-group", "'foobar-form-field': {",
			"path: 'foobar_field.js',", "'foobar-form-field-template': {");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.soy",
			"{namespace ddm}", "{template .Foobar autoescape",
			"<div class=\"form-group foobar-form-field\"");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar_field.js",
			"'foobar-form-field',", "var FoobarField",
			"value: 'foobar-form-field'", "NAME: 'foobar-form-field'",
			"Liferay.namespace('DDM.Field').Foobar = FoobarField;");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "form-field", "foobar", "com.test", mavenExecutor,
			"-DclassName=Foobar", "-Dpackage=foobar", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Ignore
	@Test
	public void testBuildTemplateFormField71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"form-field", "foobar", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar",
			"Web-ContextPath: /dynamic-data-foobar-form-field");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir, "package.json",
			"\"name\": \"dynamic-data-foobar-form-field\"",
			",foobar_field.js &&");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java",
			"property = \"ddm.form.field.type.name=foobar\"",
			"public class FoobarDDMFormFieldRenderer extends " +
				"BaseDDMFormFieldRenderer {",
			"DDMFoobar.render", "/META-INF/resources/foobar.soy");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldType.java",
			"ddm.form.field.type.description=foobar-description",
			"ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar",
			"ddm.form.field.type.js.module=foobar-form-field",
			"ddm.form.field.type.label=foobar-label",
			"ddm.form.field.type.name=foobar",
			"public class FoobarDDMFormFieldType extends BaseDDMFormFieldType",
			"return \"foobar\";");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/config.js",
			"field-foobar", "'foobar-form-field': {",
			"path: 'foobar_field.js',");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.soy",
			"{namespace DDMFoobar}", "variant=\"'foobar'\"",
			"foobar-form-field");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.es.js",
			"import templates from './foobar.soy';", "* Foobar Component",
			"class Foobar extends Component", "Soy.register(Foobar,",
			"!window.DDMFoobar", "window.DDMFoobar",
			"window.DDMFoobar.render = Foobar;", "export default Foobar;");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar_field.js",
			"'foobar-form-field',", "var FoobarField",
			"value: 'foobar-form-field'", "NAME: 'foobar-form-field'",
			"Liferay.namespace('DDM.Field').Foobar = FoobarField;");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "form-field", "foobar", "com.test", mavenExecutor,
			"-DclassName=Foobar", "-Dpackage=foobar", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Ignore
	@Test
	public void testBuildTemplateFormField71WithHyphen() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"form-field", "foo-bar", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-Name: foo-bar",
			"Web-ContextPath: /dynamic-data-foo-bar-form-field");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir, "package.json",
			"\"name\": \"dynamic-data-foo-bar-form-field\"",
			",foo-bar_field.js &&");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/bar/form/field/FooBarDDMFormFieldRenderer.java",
			"property = \"ddm.form.field.type.name=fooBar\"",
			"public class FooBarDDMFormFieldRenderer extends " +
				"BaseDDMFormFieldRenderer {",
			"DDMFooBar.render", "/META-INF/resources/foo-bar.soy");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/bar/form/field/FooBarDDMFormFieldType.java",
			"ddm.form.field.type.description=foo-bar-description",
			"ddm.form.field.type.js.class.name=Liferay.DDM.Field.FooBar",
			"ddm.form.field.type.js.module=foo-bar-form-field",
			"ddm.form.field.type.label=foo-bar-label",
			"ddm.form.field.type.name=fooBar",
			"public class FooBarDDMFormFieldType extends BaseDDMFormFieldType",
			"return \"fooBar\";");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/config.js",
			"field-foo-bar", "'foo-bar-form-field': {",
			"path: 'foo-bar_field.js',");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foo-bar.soy",
			"{namespace DDMFooBar}", "variant=\"'fooBar'\"",
			"foo-bar-form-field");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foo-bar.es.js",
			"import templates from './foo-bar.soy';", "* FooBar Component",
			"class FooBar extends Component", "Soy.register(FooBar,",
			"!window.DDMFooBar", "window.DDMFooBar",
			"window.DDMFooBar.render = FooBar;", "export default FooBar;");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foo-bar_field.js",
			"'foo-bar-form-field',", "var FooBarField",
			"value: 'foo-bar-form-field'", "NAME: 'foo-bar-form-field'",
			"Liferay.namespace('DDM.Field').FooBar = FooBarField;");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "form-field", "foo-bar", "com.test", mavenExecutor,
			"-DclassName=FooBar", "-Dpackage=foo.bar",
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateFormFieldInWorkspace() throws Exception {
		_testBuildTemplateWithWorkspace(
			"form-field", "foobar", "build/libs/foobar-1.0.0.jar",
			"--liferay-version", "7.1.3", "--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplateInWorkspace() throws Exception {
		_testBuildTemplateWithWorkspace(
			null, "hello-world-portlet",
			"build/libs/hello.world.portlet-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplateLayoutTemplate() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"layout-template", "foo");

		testExists(gradleProjectDir, "src/main/webapp/foo.png");

		testContains(
			gradleProjectDir, "src/main/webapp/foo.ftl", "class=\"foo\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-layout-templates.xml",
			"<layout-template id=\"foo\" name=\"foo\">",
			"<template-path>/foo.ftl</template-path>",
			"<thumbnail-path>/foo.png</thumbnail-path>");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=foo");
		_testEquals(gradleProjectDir, "build.gradle", "apply plugin: \"war\"");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "layout-template", "foo", "com.test",
			mavenExecutor);

		_createNewFiles(
			"src/main/resources/.gitkeep", gradleProjectDir, mavenProjectDir);

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalid62() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "6.2");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalid70test()
		throws Exception {

		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.0test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalid74() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.4");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid70() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid712() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.1.2");
	}

	@Test
	public void testBuildTemplateMVCPortlet70() throws Exception {
		_testBuildTemplatePortlet70(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortlet71() throws Exception {
		_testBuildTemplatePortlet71(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortlet72() throws Exception {
		_testBuildTemplatePortlet72(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	/*@Test
	public void testBuildTemplateMVCPortlet73() throws Exception {
		_testBuildTemplatePortlet73(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}*/

	@Test
	public void testBuildTemplateMVCPortletInWorkspace70() throws Exception {
		_testBuildTemplateWithWorkspace(
			"mvc-portlet", "foo", "build/libs/foo-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplateMVCPortletInWorkspace71() throws Exception {
		_testBuildTemplateWithWorkspace(
			"mvc-portlet", "foo", "build/libs/foo-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");
	}

	@Test
	public void testBuildTemplateMVCPortletInWorkspace72() throws Exception {
		_testBuildTemplateWithWorkspace(
			"mvc-portlet", "foo", "build/libs/foo-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.2.1");
	}

	@Test
	public void testBuildTemplateMVCPortletInWorkspace73() throws Exception {
		_testBuildTemplateWithWorkspace(
			"mvc-portlet", "foo", "build/libs/foo-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPackage70() throws Exception {
		_testBuildTemplatePortletWithPackage70(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPackage71() throws Exception {
		_testBuildTemplatePortletWithPackage71(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPackage72() throws Exception {
		_testBuildTemplatePortletWithPackage72(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPackage73() throws Exception {
		_testBuildTemplatePortletWithPackage73(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletName70()
		throws Exception {

		_testBuildTemplatePortletWithPortletName70(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletName71()
		throws Exception {

		_testBuildTemplatePortletWithPortletName71(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletName72()
		throws Exception {

		_testBuildTemplatePortletWithPortletName72(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletName73()
		throws Exception {

		_testBuildTemplatePortletWithPortletName73(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletSuffix70()
		throws Exception {

		_testBuildTemplatePortletWithPortletSuffix70(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletSuffix71()
		throws Exception {

		_testBuildTemplatePortletWithPortletSuffix71(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletSuffix72()
		throws Exception {

		_testBuildTemplatePortletWithPortletSuffix72(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateMVCPortletWithPortletSuffix73()
		throws Exception {

		_testBuildTemplatePortletWithPortletSuffix73(
			"mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp",
			"META-INF/resources/view.jsp");
	}

	@Test
	public void testBuildTemplateNAPortletWithBOM() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"npm-angular-portlet", "angular-dependency-management",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		testContains(
			gradleProjectDir, "build.gradle", DEPENDENCY_PORTAL_KERNEL + "\n");
	}

	@Test
	public void testBuildTemplateNpmAngularPortlet70() throws Exception {
		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo", "foo", "Foo", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmAngularPortlet71() throws Exception {
		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo", "foo", "Foo", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmAngularPortlet72() throws Exception {
		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo", "foo", "Foo", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmAngularPortlet73() throws Exception {
		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo", "foo", "Foo", "7.3.0");
	}

	@Test
	public void testBuildTemplateNpmAngularPortletWithDashes70()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo-bar", "foo.bar", "FooBar", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmAngularPortletWithDashes71()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo-bar", "foo.bar", "FooBar", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmAngularPortletWithDashes72()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo-bar", "foo.bar", "FooBar", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmAngularPortletWithDashes73()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-angular-portlet", "foo-bar", "foo.bar", "FooBar", "7.3.0");
	}

	@Test
	public void testBuildTemplateNpmReactPortlet70() throws Exception {
		_testBuildTemplateNpm(
			"npm-react-portlet", "foo", "foo", "Foo", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmReactPortlet71() throws Exception {
		_testBuildTemplateNpm(
			"npm-react-portlet", "foo", "foo", "Foo", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmReactPortlet72() throws Exception {
		_testBuildTemplateNpm(
			"npm-react-portlet", "foo", "foo", "Foo", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmReactPortlet73() throws Exception {
		_testBuildTemplateNpm(
			"npm-react-portlet", "foo", "foo", "Foo", "7.3.0");
	}

	@Test
	public void testBuildTemplateNpmReactPortletWithBOM() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"npm-react-portlet", "react-portlet-dependency-management",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		testContains(
			gradleProjectDir, "build.gradle", DEPENDENCY_PORTAL_KERNEL + "\n");
	}

	@Test
	public void testBuildTemplateNpmReactPortletWithDashes70()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-react-portlet", "foo-bar", "foo.bar", "FooBar", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmReactPortletWithDashes71()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-react-portlet", "foo-bar", "foo.bar", "FooBar", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmReactPortletWithDashes72()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-react-portlet", "foo-bar", "foo.bar", "FooBar", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmReactPortletWithDashes73()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-react-portlet", "foo-bar", "foo.bar", "FooBar", "7.3.0");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortlet70() throws Exception {
		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo", "foo", "Foo", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortlet71() throws Exception {
		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo", "foo", "Foo", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortlet72() throws Exception {
		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo", "foo", "Foo", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortlet73() throws Exception {
		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo", "foo", "Foo", "7.3.0");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortletWithBOM() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"npm-vuejs-portlet", "vuejs-portlet-dependency-management",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		testContains(
			gradleProjectDir, "build.gradle", DEPENDENCY_PORTAL_KERNEL + "\n");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortletWithDashes70()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar", "7.0.6");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortletWithDashes71()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar", "7.1.3");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortletWithDashes72()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar", "7.2.1");
	}

	@Test
	public void testBuildTemplateNpmVuejsPortletWithDashes73()
		throws Exception {

		_testBuildTemplateNpm(
			"npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar", "7.3.0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateOnExistingDirectory() throws Exception {
		File destinationDir = temporaryFolder.newFolder("gradle");

		buildTemplateWithGradle(destinationDir, "activator", "dup-activator");
		buildTemplateWithGradle(destinationDir, "activator", "dup-activator");
	}

	@Test
	public void testBuildTemplatePanelApp70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"panel-app", "gradle.test", "--class-name", "Foo",
			"--liferay-version", "7.0.6");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "bnd.bnd",
			"Export-Package: gradle.test.constants");
		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/application/list/FooPanelApp.java",
			"public class FooPanelApp extends BasePanelApp");
		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");
		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/portlet/FooPortlet.java",
			"javax.portlet.display-name=Foo",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.gradle_test_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "panel-app", "gradle.test", "com.test",
			mavenExecutor, "-DclassName=Foo", "-Dpackage=gradle.test",
			"-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/gradle.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePanelApp71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"panel-app", "gradle.test", "--class-name", "Foo",
			"--liferay-version", "7.1.3");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "panel-app", "gradle.test", "com.test",
			mavenExecutor, "-DclassName=Foo", "-Dpackage=gradle.test",
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/gradle.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePanelApp72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"panel-app", "gradle.test", "--class-name", "Foo",
			"--liferay-version", "7.2.1");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "panel-app", "gradle.test", "com.test",
			mavenExecutor, "-DclassName=Foo", "-Dpackage=gradle.test",
			"-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/gradle.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePanelApp73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"panel-app", "gradle.test", "--class-name", "Foo",
			"--liferay-version", "7.3.0");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "panel-app", "gradle.test", "com.test",
			mavenExecutor, "-DclassName=Foo", "-Dpackage=gradle.test",
			"-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/gradle.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePanelAppInWorkspace70() throws Exception {
		_testBuildTemplateWithWorkspace(
			"panel-app", "gradle.test", "build/libs/gradle.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplatePanelAppInWorkspace71() throws Exception {
		_testBuildTemplateWithWorkspace(
			"panel-app", "gradle.test", "build/libs/gradle.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");
	}

	@Test
	public void testBuildTemplatePanelAppInWorkspace72() throws Exception {
		_testBuildTemplateWithWorkspace(
			"panel-app", "gradle.test", "build/libs/gradle.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.2.1");
	}

	@Test
	public void testBuildTemplatePanelAppInWorkspace73() throws Exception {
		_testBuildTemplateWithWorkspace(
			"panel-app", "gradle.test", "build/libs/gradle.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");
	}

	@Test
	public void testBuildTemplatePortlet70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet", "foo.test", "--class-name", "Foo", "--liferay-version",
			"7.0");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO",
			"\"foo_test_FooPortlet\";");
		testContains(
			gradleProjectDir, "src/main/java/foo/test/portlet/FooPortlet.java",
			"package foo.test.portlet;",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet", "foo.test", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.0.6");

		testNotContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortlet71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet", "foo.test", "--class-name", "Foo", "--liferay-version",
			"7.1");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO",
			"\"foo_test_FooPortlet\";");
		testContains(
			gradleProjectDir, "src/main/java/foo/test/portlet/FooPortlet.java",
			"package foo.test.portlet;",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet", "foo.test", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortlet72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet", "foo.test", "--class-name", "Foo", "--liferay-version",
			"7.2");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO",
			"\"foo_test_FooPortlet\";");
		testContains(
			gradleProjectDir, "src/main/java/foo/test/portlet/FooPortlet.java",
			"package foo.test.portlet;",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet", "foo.test", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortlet73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet", "foo.test", "--class-name", "Foo", "--liferay-version",
			"7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/foo/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO",
			"\"foo_test_FooPortlet\";");
		testContains(
			gradleProjectDir, "src/main/java/foo/test/portlet/FooPortlet.java",
			"package foo.test.portlet;",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet", "foo.test", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletConfigurationIcon70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-configuration-icon", "icontest", "--package-name",
			"blade.test", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/blade/test/portlet/configuration/icon" +
				"/IcontestPortletConfigurationIcon.java",
			"public class IcontestPortletConfigurationIcon",
			"extends BasePortletConfigurationIcon");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-configuration-icon", "icontest",
			"com.test", mavenExecutor, "-DclassName=Icontest",
			"-Dpackage=blade.test", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletConfigurationIcon71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-configuration-icon", "icontest", "--package-name",
			"blade.test", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-configuration-icon", "icontest",
			"com.test", mavenExecutor, "-DclassName=Icontest",
			"-Dpackage=blade.test", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletConfigurationIcon72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-configuration-icon", "icontest", "--package-name",
			"blade.test", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-configuration-icon", "icontest",
			"com.test", mavenExecutor, "-DclassName=Icontest",
			"-Dpackage=blade.test", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletConfigurationIcon73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-configuration-icon", "icontest", "--package-name",
			"blade.test", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-configuration-icon", "icontest",
			"com.test", mavenExecutor, "-DclassName=Icontest",
			"-Dpackage=blade.test", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletConfigurationIconInWorkspace()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-configuration-icon", "blade.test",
			"build/libs/blade.test-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplatePortletInWorkspace() throws Exception {
		_testBuildTemplateWithWorkspace(
			"portlet", "foo.test", "build/libs/foo.test-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplatePortletProvider70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-provider", "provider.test", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/provider/test/constants" +
				"/ProviderTestPortletKeys.java",
			"package provider.test.constants;",
			"public class ProviderTestPortletKeys",
			"public static final String PROVIDERTEST",
			"\"provider_test_ProviderTestPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/provider/test/portlet/ProviderTestPortlet.java",
			"javax.portlet.display-name=ProviderTest",
			"javax.portlet.name=\" + ProviderTestPortletKeys.PROVIDERTEST",
			"public class ProviderTestPortlet extends MVCPortlet {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.provider_test_ProviderTestPortlet=" +
				"ProviderTest",
			"providertest.caption=Hello from ProviderTest!");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-provider", "provider.test", "com.test",
			mavenExecutor, "-DclassName=ProviderTest",
			"-Dpackage=provider.test", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/provider.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePortletProvider71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-provider", "provider.test", "--liferay-version", "7.1.3");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-provider", "provider.test", "com.test",
			mavenExecutor, "-DclassName=ProviderTest",
			"-Dpackage=provider.test", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/provider.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePortletProvider72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-provider", "provider.test", "--liferay-version", "7.2.1");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-provider", "provider.test", "com.test",
			mavenExecutor, "-DclassName=ProviderTest",
			"-Dpackage=provider.test", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/provider.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePortletProvider73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-provider", "provider.test", "--liferay-version", "7.3.0");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-provider", "provider.test", "com.test",
			mavenExecutor, "-DclassName=ProviderTest",
			"-Dpackage=provider.test", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/provider.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}
	}

	@Test
	public void testBuildTemplatePortletProviderInWorkspace70()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-provider", "provider.test",
			"build/libs/provider.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplatePortletProviderInWorkspace71()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-provider", "provider.test",
			"build/libs/provider.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");
	}

	@Test
	public void testBuildTemplatePortletProviderInWorkspace72()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-provider", "provider.test",
			"build/libs/provider.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.2.1");
	}

	@Test
	public void testBuildTemplatePortletProviderInWorkspace73()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-provider", "provider.test",
			"build/libs/provider.test-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");
	}

	@Test
	public void testBuildTemplatePortletToolbarContributor70()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-toolbar-contributor", "toolbartest", "--package-name",
			"blade.test", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/blade/test/portlet/toolbar/contributor" +
				"/ToolbartestPortletToolbarContributor.java",
			"public class ToolbartestPortletToolbarContributor",
			"implements PortletToolbarContributor");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-toolbar-contributor", "toolbartest",
			"com.test", mavenExecutor, "-DclassName=Toolbartest",
			"-Dpackage=blade.test", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletToolbarContributor71()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-toolbar-contributor", "toolbartest", "--package-name",
			"blade.test", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-toolbar-contributor", "toolbartest",
			"com.test", mavenExecutor, "-DclassName=Toolbartest",
			"-Dpackage=blade.test", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletToolbarContributor72()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-toolbar-contributor", "toolbartest", "--package-name",
			"blade.test", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-toolbar-contributor", "toolbartest",
			"com.test", mavenExecutor, "-DclassName=Toolbartest",
			"-Dpackage=blade.test", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletToolbarContributor73()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"portlet-toolbar-contributor", "toolbartest", "--package-name",
			"blade.test", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet-toolbar-contributor", "toolbartest",
			"com.test", mavenExecutor, "-DclassName=Toolbartest",
			"-Dpackage=blade.test", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplatePortletToolbarContributorInWorkspace()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"portlet-toolbar-contributor", "blade.test",
			"build/libs/blade.test-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplatePortletWithPortletName() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle("portlet", "portlet");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/PortletPortlet.java",
			"package portlet.portlet;",
			"public class PortletPortlet extends MVCPortlet {");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "portlet", "portlet", "com.test", mavenExecutor,
			"-DclassName=Portlet", "-Dpackage=portlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateRest70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"rest", "my-rest", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"compileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\", " +
				"version: \"2.0.1\"");
		testContains(
			gradleProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testContains(
			gradleProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties",
			"contextPath=/my-rest");
		testContains(
			gradleProjectDir,
			"src/main/resources/configuration/com.liferay.portal.remote.rest." +
				"extender.configuration.RestExtenderConfiguration-rest." +
					"properties",
			"contextPaths=/my-rest",
			"jaxRsApplicationFilterStrings=(component.name=" +
				"my.rest.application.MyRestApplication)");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "rest", "my-rest", "com.test", mavenExecutor,
			"-DclassName=MyRest", "-Dpackage=my.rest",
			"-DliferayVersion=7.0.6");

		testContains(
			mavenProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testContains(
			mavenProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties",
			"contextPath=/my-rest");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateRest71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"rest", "my-rest", "--liferay-version", "7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"compileOnly group: \"org.osgi\", name: " +
				"\"org.osgi.service.jaxrs\", version: \"1.0.0\"");
		testContains(
			gradleProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testNotExists(
			gradleProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties");
		testNotExists(
			gradleProjectDir,
			"src/main/resources/configuration/com.liferay.portal.remote.rest." +
				"extender.configuration.RestExtenderConfiguration-rest." +
					"properties");
		testNotExists(gradleProjectDir, "src/main/resources/configuration");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "rest", "my-rest", "com.test", mavenExecutor,
			"-DclassName=MyRest", "-Dpackage=my.rest",
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testNotExists(
			mavenProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties");
		testNotExists(mavenProjectDir, "src/main/resources/configuration");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateRest72() throws Exception {
		_testBuildTemplateRest("7.2.1");
	}

	@Test
	public void testBuildTemplateRest73() throws Exception {
		_testBuildTemplateRest("7.3.0");
	}

	@Test
	public void testBuildTemplateRestInWorkspace70() throws Exception {
		_testBuildTemplateWithWorkspace(
			"rest", "my-rest", "build/libs/my.rest-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplateRestInWorkspace71() throws Exception {
		_testBuildTemplateWithWorkspace(
			"rest", "my-rest", "build/libs/my.rest-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");
	}

	@Test
	public void testBuildTemplateRestInWorkspace72() throws Exception {
		_testBuildTemplateWithWorkspace(
			"rest", "my-rest", "build/libs/my.rest-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.2.1");
	}

	@Test
	public void testBuildTemplateRestInWorkspace73() throws Exception {
		_testBuildTemplateWithWorkspace(
			"rest", "my-rest", "build/libs/my.rest-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");
	}

	@Test
	public void testBuildTemplateSimulationPanelEntry70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"simulation-panel-entry", "simulator", "--package-name",
			"test.simulator", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.3.0\"",
			"apply plugin: \"com.liferay.plugin\"");
		testContains(
			gradleProjectDir,
			"src/main/java/test/simulator/application/list" +
				"/SimulatorSimulationPanelApp.java",
			"public class SimulatorSimulationPanelApp",
			"extends BaseJSPPanelApp");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "simulation-panel-entry", "simulator", "com.test",
			mavenExecutor, "-DclassName=Simulator", "-Dpackage=test.simulator",
			"-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateSimulationPanelEntry71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"simulation-panel-entry", "simulator", "--package-name",
			"test.simulator", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "simulation-panel-entry", "simulator", "com.test",
			mavenExecutor, "-DclassName=Simulator", "-Dpackage=test.simulator",
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateSimulationPanelEntry72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"simulation-panel-entry", "simulator", "--package-name",
			"test.simulator", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "simulation-panel-entry", "simulator", "com.test",
			mavenExecutor, "-DclassName=Simulator", "-Dpackage=test.simulator",
			"-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateSimulationPanelEntry73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"simulation-panel-entry", "simulator", "--package-name",
			"test.simulator", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "simulation-panel-entry", "simulator", "com.test",
			mavenExecutor, "-DclassName=Simulator", "-Dpackage=test.simulator",
			"-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateSimulationPanelEntryInWorkspace70()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"simulation-panel-entry", "test.simulator",
			"build/libs/test.simulator-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplateSimulationPanelEntryInWorkspace71()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"simulation-panel-entry", "test.simulator",
			"build/libs/test.simulator-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.1.3");
	}

	@Test
	public void testBuildTemplateSimulationPanelEntryInWorkspace72()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"simulation-panel-entry", "test.simulator",
			"build/libs/test.simulator-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.2.1");
	}

	@Test
	public void testBuildTemplateSimulationPanelEntryInWorkspace73()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"simulation-panel-entry", "test.simulator",
			"build/libs/test.simulator-1.0.0.jar",
			"--dependency-management-enabled", "--liferay-version", "7.3.0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateSocialBookmark70() throws Exception {
		_buildTemplateWithGradle(
			"social-bookmark", "foo", "--package-name", "com.liferay.test",
			"--liferay-version", "7.0.6");
	}

	@Test
	public void testBuildTemplateSocialBookmark71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"social-bookmark", "foo", "--package-name", "com.liferay.test",
			"--liferay-version", "7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(gradleProjectDir, "build.gradle");

		testContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/social/bookmark" +
				"/FooSocialBookmark.java",
			"public class FooSocialBookmark implements SocialBookmark");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/page.jsp",
			"<clay:link");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"foo=Foo");

		testNotContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/social/bookmark" +
				"/FooSocialBookmark.java",
			"private ResourceBundleLoader");
		testNotContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/social/bookmark" +
				"/FooSocialBookmark.java",
			"protected ResourceBundleLoader");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "social-bookmark", "foo", "com.test",
			mavenExecutor, "-DclassName=Foo", "-Dpackage=com.liferay.test",
			"-DliferayVersion=7.1.3");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTemplateContextContributor70()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"template-context-contributor", "blade-test", "--liferay-version",
			"7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"",
			"apply plugin: \"com.liferay.plugin\"");

		testContains(
			gradleProjectDir,
			"src/main/java/blade/test/context/contributor" +
				"/BladeTestTemplateContextContributor.java",
			"public class BladeTestTemplateContextContributor",
			"implements TemplateContextContributor");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "template-context-contributor", "blade-test",
			"com.test", mavenExecutor, "-DclassName=BladeTest",
			"-Dpackage=blade.test", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTemplateContextContributor71()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"template-context-contributor", "blade-test", "--liferay-version",
			"7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "template-context-contributor", "blade-test",
			"com.test", mavenExecutor, "-DclassName=BladeTest",
			"-Dpackage=blade.test", "-DliferayVersion=7.1.3");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTemplateContextContributor72()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"template-context-contributor", "blade-test", "--liferay-version",
			"7.2.1");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "template-context-contributor", "blade-test",
			"com.test", mavenExecutor, "-DclassName=BladeTest",
			"-Dpackage=blade.test", "-DliferayVersion=7.2.1");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTemplateContextContributor73()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"template-context-contributor", "blade-test", "--liferay-version",
			"7.3.0");

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "template-context-contributor", "blade-test",
			"com.test", mavenExecutor, "-DclassName=BladeTest",
			"-Dpackage=blade.test", "-DliferayVersion=7.3.0");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTemplateContextContributorInWorkspace()
		throws Exception {

		_testBuildTemplateWithWorkspace(
			"template-context-contributor", "blade-test",
			"build/libs/blade.test-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplateTheme70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"theme", "theme-test", "--liferay-version", "7.0.6");

		testContains(
			gradleProjectDir, "build.gradle",
			"name: \"com.liferay.gradle.plugins.theme.builder\"",
			"apply plugin: \"com.liferay.portal.tools.theme.builder\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=theme-test");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "theme", "theme-test", "com.test", mavenExecutor,
			"-DliferayVersion=7.0.6");

		testContains(
			mavenProjectDir, "pom.xml",
			"com.liferay.portal.tools.theme.builder");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTheme71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"theme", "theme-test", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			"name: \"com.liferay.gradle.plugins.theme.builder\"",
			"apply plugin: \"com.liferay.portal.tools.theme.builder\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=theme-test");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "theme", "theme-test", "com.test", mavenExecutor,
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "pom.xml",
			"com.liferay.portal.tools.theme.builder");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTheme72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"theme", "theme-test", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			"name: \"com.liferay.gradle.plugins.theme.builder\"",
			"apply plugin: \"com.liferay.portal.tools.theme.builder\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=theme-test");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "theme", "theme-test", "com.test", mavenExecutor,
			"-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "pom.xml",
			"com.liferay.portal.tools.theme.builder");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateTheme73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"theme", "theme-test", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			"name: \"com.liferay.gradle.plugins.theme.builder\"",
			"apply plugin: \"com.liferay.portal.tools.theme.builder\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=theme-test");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "theme", "theme-test", "com.test", mavenExecutor,
			"-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "pom.xml",
			"com.liferay.portal.tools.theme.builder");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateThemeInWorkspace70() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("theme", "theme-test", "7.0.6");
	}

	@Test
	public void testBuildTemplateThemeInWorkspace71() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("theme", "theme-test", "7.1.3");
	}

	@Test
	public void testBuildTemplateThemeInWorkspace72() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("theme", "theme-test", "7.2.1");
	}

	@Test
	public void testBuildTemplateThemeInWorkspace73() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("theme", "theme-test", "7.3.0");
	}

	@Test
	public void testBuildTemplateWarCoreExt() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-core-ext", "test-war-core-ext");

		testContains(
			gradleProjectDir, "build.gradle", "buildscript {", "repositories {",
			"group: \"com.liferay\", name: \"com.liferay.gradle.plugins\"",
			"apply plugin: \"com.liferay.ext.plugin\"",
			"apply plugin: \"eclipse\"");
		testContains(
			gradleProjectDir, "src/extImpl/resources/META-INF/ext-spring.xml");
	}

	@Test
	public void testBuildTemplateWarCoreExtInWorkspace() throws Exception {
		File modulesDir = new File(
			buildWorkspace(temporaryFolder, getDefaultLiferayVersion()),
			"modules");

		File projectDir = buildTemplateWithGradle(
			modulesDir, "war-core-ext", "test-war-core-ext");

		testNotContains(
			projectDir, "build.gradle", true, "^repositories \\{.*");
		testNotContains(
			projectDir, "build.gradle", "buildscript",
			"com.liferay.ext.plugin");
	}

	@Test
	public void testBuildTemplateWarHook70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "WarHook", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "src/main/resources/portal.properties");
		testExists(
			gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"");
		testContains(
			gradleProjectDir,
			"src/main/java/warhook/WarHookLoginPostAction.java",
			"public class WarHookLoginPostAction extends Action");
		testContains(
			gradleProjectDir, "src/main/java/warhook/WarHookStartupAction.java",
			"public class WarHookStartupAction extends SimpleAction");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=WarHook");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-hook", "WarHook", "warhook", mavenExecutor,
			"-DclassName=WarHook", "-Dpackage=warhook",
			"-DliferayVersion=7.0.6");

		testContains(mavenProjectDir, "pom.xml");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarHook71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "WarHook", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-hook", "WarHook", "warhook", mavenExecutor,
			"-DclassName=WarHook", "-Dpackage=warhook",
			"-DliferayVersion=7.1.3");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarHook72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "WarHook", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-hook", "WarHook", "warhook", mavenExecutor,
			"-DclassName=WarHook", "-Dpackage=warhook",
			"-DliferayVersion=7.2.1");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarHook73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-hook", "WarHook", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-hook", "WarHook", "warhook", mavenExecutor,
			"-DclassName=WarHook", "-Dpackage=warhook",
			"-DliferayVersion=7.3.0");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarHookInWorkspace70() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("war-hook", "WarHook", "7.0.6");
	}

	@Test
	public void testBuildTemplateWarHookInWorkspace71() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("war-hook", "WarHook", "7.1.3");
	}

	@Test
	public void testBuildTemplateWarHookInWorkspace72() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("war-hook", "WarHook", "7.2.1");
	}

	@Test
	public void testBuildTemplateWarHookInWorkspace73() throws Exception {
		_testBuildTemplateProjectWarInWorkspace("war-hook", "WarHook", "7.3.0");
	}

	@Test
	public void testBuildTemplateWarMVCPortlet70() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "src/main/webapp/init.jsp");
		testExists(gradleProjectDir, "src/main/webapp/view.jsp");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"",
			"apply plugin: \"com.liferay.css.builder\"",
			"apply plugin: \"war\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=WarMVCPortlet");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"warmvcportlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVCPortlet", "-DliferayVersion=7.0.6");

		testContains(
			mavenProjectDir, "pom.xml", "maven-war-plugin",
			"com.liferay.css.builder");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortlet71() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet", "--liferay-version", "7.1.3");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"warmvcportlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVCPortlet", "-DliferayVersion=7.1.3");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortlet72() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet", "--liferay-version", "7.2.1");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"warmvcportlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVCPortlet", "-DliferayVersion=7.2.1");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortlet73() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet", "--liferay-version", "7.3.0");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"warmvcportlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVCPortlet", "-DliferayVersion=7.3.0");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortletInWorkspace70() throws Exception {
		_testBuildTemplateProjectWarInWorkspace(
			"war-mvc-portlet", "WarMVCPortlet", "7.0.6");
	}

	@Test
	public void testBuildTemplateWarMVCPortletInWorkspace71() throws Exception {
		_testBuildTemplateProjectWarInWorkspace(
			"war-mvc-portlet", "WarMVCPortlet", "7.1.3");
	}

	@Test
	public void testBuildTemplateWarMVCPortletInWorkspace72() throws Exception {
		_testBuildTemplateProjectWarInWorkspace(
			"war-mvc-portlet", "WarMVCPortlet", "7.2.1");
	}

	@Test
	public void testBuildTemplateWarMVCPortletInWorkspace73() throws Exception {
		_testBuildTemplateProjectWarInWorkspace(
			"war-mvc-portlet", "WarMVCPortlet", "7.3.0");
	}

	@Test
	public void testBuildTemplateWarMVCPortletWithPackage() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet", "--package-name",
			"com.liferay.test");

		testExists(gradleProjectDir, "src/main/webapp/init.jsp");
		testExists(gradleProjectDir, "src/main/webapp/view.jsp");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.css.builder\"",
			"apply plugin: \"war\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=WarMVCPortlet");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"com.liferay.test", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=com.liferay.test");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortletWithPortletName()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVCPortlet");

		testExists(gradleProjectDir, "src/main/webapp/init.jsp");
		testExists(gradleProjectDir, "src/main/webapp/view.jsp");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.css.builder\"",
			"apply plugin: \"war\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=WarMVCPortlet");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVCPortlet",
			"warmvcportlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVCPortlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWarMVCPortletWithPortletSuffix()
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"war-mvc-portlet", "WarMVC-portlet");

		testExists(gradleProjectDir, "src/main/webapp/init.jsp");
		testExists(gradleProjectDir, "src/main/webapp/view.jsp");

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.css.builder\"",
			"apply plugin: \"war\"");
		testContains(
			gradleProjectDir,
			"src/main/webapp/WEB-INF/liferay-plugin-package.properties",
			"name=WarMVC-portlet");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "war-mvc-portlet", "WarMVC-portlet",
			"warmvc.portlet", mavenExecutor, "-DclassName=WarMVCPortlet",
			"-Dpackage=WarMVC.portlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWithGradle() throws Exception {
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", false, false);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", false, true);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", true, false);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", true, true);
	}

	@Test
	public void testBuildTemplateWithPackageName() throws Exception {
		File gradleProjectDir = _buildTemplateWithGradle(
			"", "barfoo", "--package-name", "foo.bar");

		testExists(
			gradleProjectDir, "src/main/resources/META-INF/resources/init.jsp");
		testExists(
			gradleProjectDir, "src/main/resources/META-INF/resources/view.jsp");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-SymbolicName: foo.bar");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "mvc-portlet", "barfoo", "com.test", mavenExecutor,
			"-DclassName=Barfoo", "-Dpackage=foo.bar");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateWorkspace() throws Exception {
		File workspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "foows");

		testExists(workspaceProjectDir, "configs/dev/portal-ext.properties");
		testExists(workspaceProjectDir, "gradle.properties");
		testExists(workspaceProjectDir, "modules");
		testExists(workspaceProjectDir, "themes");
		testExists(workspaceProjectDir, "wars");

		testNotExists(workspaceProjectDir, "modules/pom.xml");
		testNotExists(workspaceProjectDir, "themes/pom.xml");
		testNotExists(workspaceProjectDir, "wars/pom.xml");

		File moduleProjectDir = buildTemplateWithGradle(
			new File(workspaceProjectDir, "modules"), "", "foo-portlet");

		testNotContains(
			moduleProjectDir, "build.gradle", "buildscript", "repositories");

		if (isBuildProjects()) {
			executeGradle(
				workspaceProjectDir, ProjectTemplatesTest._gradleDistribution,
				":modules:foo-portlet" + GRADLE_TASK_PATH_BUILD);

			testExists(moduleProjectDir, "build/libs/foo.portlet-1.0.0.jar");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateWorkspaceExistingFile() throws Exception {
		File destinationDir = temporaryFolder.newFolder("existing-file");

		_createNewFiles("foo", destinationDir);

		buildTemplateWithGradle(destinationDir, WorkspaceUtil.WORKSPACE, "foo");
	}

	@Test
	public void testBuildTemplateWorkspaceForce() throws Exception {
		File destinationDir = temporaryFolder.newFolder("existing-file");

		_createNewFiles("foo", destinationDir);

		buildTemplateWithGradle(
			destinationDir, WorkspaceUtil.WORKSPACE, "forced", "--force");
	}

	@Test
	public void testBuildTemplateWorkspaceLocalProperties() throws Exception {
		File workspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "foo");

		testExists(workspaceProjectDir, "gradle-local.properties");

		Properties gradleLocalProperties = new Properties();

		String homeDirName = "foo/bar/baz";
		String modulesDirName = "qux/quux";

		gradleLocalProperties.put("liferay.workspace.home.dir", homeDirName);
		gradleLocalProperties.put(
			"liferay.workspace.modules.dir", modulesDirName);

		File gradleLocalPropertiesFile = new File(
			workspaceProjectDir, "gradle-local.properties");

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				gradleLocalPropertiesFile)) {

			gradleLocalProperties.store(fileOutputStream, null);
		}

		buildTemplateWithGradle(
			new File(workspaceProjectDir, modulesDirName), "", "foo-portlet");

		executeGradle(
			workspaceProjectDir, ProjectTemplatesTest._gradleDistribution,
			":" + modulesDirName.replace('/', ':') + ":foo-portlet" +
				BaseProjectTemplatesTestCase.GRADLE_TASK_PATH_DEPLOY);

		testExists(
			workspaceProjectDir, homeDirName + "/osgi/modules/foo.portlet.jar");
	}

	@Test
	public void testBuildTemplateWorkspaceWith70() throws Exception {
		File gradleWorkspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "withportlet", "--liferay-version",
			"7.0.6");

		testContains(
			gradleWorkspaceProjectDir, "gradle.properties", true,
			".*liferay.workspace.bundle.url=.*liferay.com/portal/7.0.*");

		File gradlePropertiesFile = new File(
			gradleWorkspaceProjectDir, "gradle.properties");

		_testPropertyKeyExists(
			gradlePropertiesFile, "liferay.workspace.bundle.url");

		File mavenWorkspaceProjectDir = buildTemplateWithMaven(
			temporaryFolder, WorkspaceUtil.WORKSPACE, "withportlet", "com.test",
			mavenExecutor, "-DliferayVersion=7.0.6");

		testContains(
			mavenWorkspaceProjectDir, "pom.xml",
			"<liferay.workspace.bundle.url>", "liferay.com/portal/7.0.");
	}

	@Test
	public void testBuildTemplateWorkspaceWith71() throws Exception {
		File gradleWorkspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "withportlet", "--liferay-version",
			"7.1.3");

		testContains(
			gradleWorkspaceProjectDir, "gradle.properties", true,
			".*liferay.workspace.bundle.url=.*liferay.com/portal/7.1.*");

		File gradlePropertiesFile = new File(
			gradleWorkspaceProjectDir, "gradle.properties");

		_testPropertyKeyExists(
			gradlePropertiesFile, "liferay.workspace.bundle.url");

		File mavenWorkspaceProjectDir = buildTemplateWithMaven(
			temporaryFolder, WorkspaceUtil.WORKSPACE, "withportlet", "com.test",
			mavenExecutor, "-DliferayVersion=7.1.3");

		testContains(
			mavenWorkspaceProjectDir, "pom.xml",
			"<liferay.workspace.bundle.url>", "liferay.com/portal/7.1");
	}

	@Test
	public void testBuildTemplateWorkspaceWith72() throws Exception {
		File gradleWorkspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "withportlet", "--liferay-version",
			"7.2.1");

		testContains(
			gradleWorkspaceProjectDir, "gradle.properties", true,
			".*liferay.workspace.bundle.url=.*liferay.com/portal/7.2.*");

		File gradlePropertiesFile = new File(
			gradleWorkspaceProjectDir, "gradle.properties");

		_testPropertyKeyExists(
			gradlePropertiesFile, "liferay.workspace.bundle.url");

		File mavenWorkspaceProjectDir = buildTemplateWithMaven(
			temporaryFolder, WorkspaceUtil.WORKSPACE, "withportlet", "com.test",
			mavenExecutor, "-DliferayVersion=7.2.1");

		testContains(
			mavenWorkspaceProjectDir, "pom.xml",
			"<liferay.workspace.bundle.url>", "liferay.com/portal/7.2");
	}

	@Test
	public void testBuildTemplateWorkspaceWithPortlet() throws Exception {
		File gradleWorkspaceProjectDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "withportlet");

		File gradleModulesDir = new File(gradleWorkspaceProjectDir, "modules");

		buildTemplateWithGradle(gradleModulesDir, "mvc-portlet", "foo-portlet");

		File mavenWorkspaceProjectDir = buildTemplateWithMaven(
			temporaryFolder, WorkspaceUtil.WORKSPACE, "withportlet", "com.test",
			mavenExecutor);

		File mavenModulesDir = new File(mavenWorkspaceProjectDir, "modules");

		buildTemplateWithMaven(
			mavenWorkspaceProjectDir.getParentFile(), mavenModulesDir,
			"mvc-portlet", "foo-portlet", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.portlet",
			"-DprojectType=workspace");

		if (isBuildProjects()) {
			executeGradle(
				gradleWorkspaceProjectDir, ProjectTemplatesTest._gradleDistribution,
				":modules:foo-portlet" + GRADLE_TASK_PATH_BUILD);

			testExists(
				gradleModulesDir,
				"foo-portlet/build/libs/foo.portlet-1.0.0.jar");

			executeMaven(mavenModulesDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

			testExists(
				mavenModulesDir, "foo-portlet/target/foo-portlet-1.0.0.jar");
		}
	}

	@Test
	public void testCompareAntBndPluginVersions() throws Exception {
		String template = "mvc-portlet";
		String name = "foo";

		File gradleProjectDir = _buildTemplateWithGradle(template, name);

		Optional<String> gradleResult = executeGradle(
			gradleProjectDir, true, ProjectTemplatesTest._gradleDistribution,
			GRADLE_TASK_PATH_BUILD);

		String gradleAntBndVersion = null;

		Matcher matcher = BaseProjectTemplatesTestCase.antBndPluginVersionPattern.matcher(
			gradleResult.get());

		if (matcher.matches()) {
			gradleAntBndVersion = matcher.group(1);
		}

		File mavenProjectDir = _buildTemplateWithMaven(
			template, name, name, "-DclassName=foo");

		testContains(
			mavenProjectDir, "pom.xml",
			"<artifactId>com.liferay.ant.bnd</artifactId>\n\t\t\t\t\t\t" +
				"<version>" + gradleAntBndVersion);
	}

	@Test
	public void testComparePortalToolsBundleSupportPluginVersions()
		throws Exception {

		File workspaceDir = buildWorkspace(
			temporaryFolder, getDefaultLiferayVersion());

		Optional<String> result = executeGradle(
			workspaceDir, true, ProjectTemplatesTest._gradleDistribution, ":tasks");

		Matcher matcher = BaseProjectTemplatesTestCase.portalToolsBundleSupportVersionPattern.matcher(
			result.get());

		String portalToolsBundleSupportVersion = null;

		if (matcher.matches()) {
			portalToolsBundleSupportVersion = matcher.group(1);
		}

		File mavenWorkspaceDir = _buildTemplateWithMaven(
			"workspace", "mavenWS", "liferayMaven",
			"-DliferayVersion=" + getDefaultLiferayVersion());

		testContains(
			mavenWorkspaceDir, "pom.xml",
			"<artifactId>com.liferay.portal.tools.bundle.support</artifactId>" +
				"\n\t\t\t\t<version>" + portalToolsBundleSupportVersion);
	}

	@Test
	public void testListTemplates() throws Exception {
		final Map<String, String> expectedTemplates = new TreeMap<>();

		try (DirectoryStream<Path> directoryStream =
				FileTestUtil.getProjectTemplatesDirectoryStream()) {

			for (Path path : directoryStream) {
				String fileName = String.valueOf(path.getFileName());

				String template = fileName.substring(
					FileTestUtil.PROJECT_TEMPLATE_DIR_PREFIX.length());

				if (!template.equals(WorkspaceUtil.WORKSPACE)) {
					Properties properties = FileUtil.readProperties(
						path.resolve("bnd.bnd"));

					String bundleDescription = properties.getProperty(
						"Bundle-Description");

					expectedTemplates.put(template, bundleDescription);
				}
			}
		}

		Assert.assertEquals(expectedTemplates, ProjectTemplates.getTemplates());
	}

	@Test
	public void testListTemplatesWithCustomArchetypesDir() throws Exception {
		File customArchetypesDir = temporaryFolder.newFolder();

		Path customArchetypesDirPath = customArchetypesDir.toPath();

		String jarName = "custom.template.jar";

		Files.write(
			customArchetypesDirPath.resolve(jarName),
			FileTestUtil.readAllBytes(
				"com/liferay/project/templates/dependencies/" + jarName));

		Map<String, String> customTemplatesMap = ProjectTemplates.getTemplates(
			Collections.singletonList(customArchetypesDir));

		Map<String, String> templatesMap = ProjectTemplates.getTemplates();

		Assert.assertEquals(
			customTemplatesMap.toString(), templatesMap.size() + 1,
			customTemplatesMap.size());
	}

	@Test
	public void testSassCompilerMavenWorkspace() throws Exception {
		File nativeSassWorkspaceDir = _buildTemplateWithMaven(
			"workspace", "nativeSassMavenWS", "liferayMaven",
			"-DliferayVersion=" + getDefaultLiferayVersion());

		File nativeSassModulesDir = new File(nativeSassWorkspaceDir, "modules");

		File nativeSassProjectDir = buildTemplateWithMaven(
			nativeSassWorkspaceDir.getParentFile(), nativeSassModulesDir,
			"mvc-portlet", "foo-portlet", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.portlet",
			"-DprojectType=workspace");

		String nativeSassOutput = executeMaven(
			nativeSassProjectDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

		Assert.assertTrue(
			nativeSassOutput,
			nativeSassOutput.contains("Using native Sass compiler"));

		File rubySassWorkspaceDir = _buildTemplateWithMaven(
			"workspace", "rubySassMavenWS", "liferayMaven",
			"-DliferayVersion=" + getDefaultLiferayVersion());

		File rubySassModulesDir = new File(rubySassWorkspaceDir, "modules");

		File rubySassProjectDir = buildTemplateWithMaven(
			rubySassWorkspaceDir.getParentFile(), rubySassModulesDir,
			"mvc-portlet", "foo-portlet", "com.test", mavenExecutor,
			"-DclassName=Foo", "-Dpackage=foo.portlet",
			"-DprojectType=workspace");

		File pomXmlFile = new File(rubySassProjectDir, "pom.xml");

		if (pomXmlFile.exists()) {
			editXml(
				pomXmlFile,
				document -> addCssBuilderConfigurationElement(
					document, "sassCompilerClassName", "ruby"));
		}

		String rubySassOutput = executeMaven(
			rubySassProjectDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

		Assert.assertTrue(
			rubySassOutput,
			rubySassOutput.contains("Using Ruby Sass compiler"));

		File nativeSassOutputFile = testExists(
			nativeSassProjectDir, "target/foo-portlet-1.0.0.jar");
		File rubySassOutputFile = testExists(
			rubySassProjectDir, "target/foo-portlet-1.0.0.jar");

		testBundlesDiff(nativeSassOutputFile, rubySassOutputFile);
	}

	@Test
	public void testSassCompilerWorkspace() throws Exception {
		File nativeSassWorkspaceDir = buildWorkspace(
			temporaryFolder, getDefaultLiferayVersion());

		File nativeSassModulesDir = new File(nativeSassWorkspaceDir, "modules");

		File nativeSassProjectDir = buildTemplateWithGradle(
			nativeSassModulesDir, "mvc-portlet", "foo-portlet");

		Optional<String> nativeSassResult = executeGradle(
			nativeSassModulesDir, true, ProjectTemplatesTest._gradleDistribution,
			":modules:foo-portlet" + GRADLE_TASK_PATH_BUILD);

		String nativeSassOutput = nativeSassResult.toString();

		Assert.assertTrue(
			nativeSassOutput,
			nativeSassOutput.contains("Using native Sass compiler"));

		File rubySassWorkspaceDir = _buildTemplateWithGradle(
			WorkspaceUtil.WORKSPACE, "rubySassWorkspace");

		writeGradlePropertiesInWorkspace(
			rubySassWorkspaceDir, "sass.compiler.class.name=ruby");

		File rubySassModulesDir = new File(rubySassWorkspaceDir, "modules");

		File rubySassProjectDir = buildTemplateWithGradle(
			rubySassModulesDir, "mvc-portlet", "foo-portlet");

		Optional<String> rubySassResult = executeGradle(
			rubySassModulesDir, true, ProjectTemplatesTest._gradleDistribution,
			":modules:foo-portlet" + GRADLE_TASK_PATH_BUILD);

		String rubySassOutput = rubySassResult.toString();

		Assert.assertTrue(
			rubySassOutput,
			rubySassOutput.contains("Using Ruby Sass compiler"));

		File nativeSassOutputFile = testExists(
			nativeSassProjectDir, "build/libs/foo.portlet-1.0.0.jar");
		File rubySassOutputFile = testExists(
			rubySassProjectDir, "build/libs/foo.portlet-1.0.0.jar");

		testBundlesDiff(nativeSassOutputFile, rubySassOutputFile);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private static XPathExpression _pomXmlNpmInstallXPathExpression;
	private static URI _gradleDistribution;

	private static void _testPropertyKeyExists(File file, String key)
		throws Exception {

		Properties properties = FileTestUtil.readProperties(file);

		String property = properties.getProperty(key);

		Assert.assertNotNull(
			"Expected key " + key + " to exist in properties " +
				file.getAbsolutePath(),
			property);
	}

	private void _addNpmrc(File projectDir) throws IOException {
		File npmrcFile = new File(projectDir, ".npmrc");

		String content = "sass_binary_site=" + BaseProjectTemplatesTestCase.NODEJS_NPM_CI_SASS_BINARY_SITE;

		Files.write(
			npmrcFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private void _buildProjects(File gradleProjectDir, File mavenProjectDir)
		throws Exception {

		buildProjects(
			ProjectTemplatesTest._gradleDistribution, mavenExecutor, gradleProjectDir,
			mavenProjectDir);
	}

	private File _buildTemplateWithGradle(
			String template, String name, String... args)
		throws Exception {

		return buildTemplateWithGradle(temporaryFolder, template, name, args);
	}

	private File _buildTemplateWithMaven(
			String template, String name, String groupId, String... args)
		throws Exception {

		return buildTemplateWithMaven(
			temporaryFolder, template, name, groupId, mavenExecutor, args);
	}

	private void _configureExecutePackageManagerTask(File projectDir)
		throws Exception {

		File buildGradleFile = testContains(
			projectDir, "build.gradle", "com.liferay.gradle.plugins",
			"com.liferay.plugin");

		StringBuilder sb = new StringBuilder();

		String lineSeparator = System.lineSeparator();

		sb.append(lineSeparator);

		sb.append(
			"import com.liferay.gradle.plugins.node.tasks." +
				"ExecutePackageManagerTask");
		sb.append(lineSeparator);

		sb.append("tasks.withType(ExecutePackageManagerTask) {");
		sb.append(lineSeparator);

		sb.append("\tregistry = '");
		sb.append(BaseProjectTemplatesTestCase.NODEJS_NPM_CI_REGISTRY);
		sb.append('\'');
		sb.append(lineSeparator);

		sb.append('}');
		sb.append(lineSeparator);

		sb.append("node {");
		sb.append(lineSeparator);

		sb.append("\tuseNpm = true");
		sb.append(lineSeparator);

		sb.append("}");

		String executePackageManagerTaskScript = sb.toString();

		Files.write(
			buildGradleFile.toPath(),
			executePackageManagerTaskScript.getBytes(StandardCharsets.UTF_8),
			StandardOpenOption.APPEND);
	}

	private void _configurePomNpmConfiguration(File projectDir)
		throws Exception {

		File pomXmlFile = new File(projectDir, "pom.xml");

		editXml(
			pomXmlFile,
			document -> {
				try {
					NodeList nodeList =
						(NodeList)ProjectTemplatesTest._pomXmlNpmInstallXPathExpression.evaluate(
							document, XPathConstants.NODESET);

					Node executionNode = nodeList.item(0);

					Element configurationElement = document.createElement(
						"configuration");

					executionNode.appendChild(configurationElement);

					Element argumentsElement = document.createElement(
						"arguments");

					configurationElement.appendChild(argumentsElement);

					Text text = document.createTextNode(
						"install --registry=" + BaseProjectTemplatesTestCase.NODEJS_NPM_CI_REGISTRY);

					argumentsElement.appendChild(text);
				}
				catch (XPathExpressionException xPathExpressionException) {
				}
			});
	}

	private void _testBuildTemplateNpm(
			String template, String name, String packageName, String className,
			String liferayVersion)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, name, "--liferay-version", liferayVersion);

		if (liferayVersion.equals("7.0.6")) {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_MODULES_EXTENDER_API + ", version: \"2.0.2",
				DEPENDENCY_PORTAL_KERNEL + ", version: \"2.64.1");
		}
		else if (liferayVersion.equals("7.1.3")) {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_MODULES_EXTENDER_API + ", version: \"2.0.6",
				DEPENDENCY_PORTAL_KERNEL + ", version: \"3.63.1");
		}
		else if (liferayVersion.equals("7.2.1")) {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_MODULES_EXTENDER_API + ", version: \"3.2.4",
				DEPENDENCY_PORTAL_KERNEL + ", version: \"4.35.2");
		}
		else if (liferayVersion.equals("7.3.0")) {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_MODULES_EXTENDER_API + ", version: \"4.0.1",
				DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");
		}

		if (template.equals("npm-angular-portlet")) {
			testContains(
				gradleProjectDir, "package.json", "@angular/animations",
				"liferay-npm-bundler\": \"2.18.2",
				"build\": \"tsc && liferay-npm-bundler");

			testExists(
				gradleProjectDir,
				"src/main/resources/META-INF/resources/lib/angular-loader.ts");
		}
		else {
			testContains(
				gradleProjectDir, "package.json",
				"build/resources/main/META-INF/resources",
				"liferay-npm-bundler\": \"2.18.2",
				"\"main\": \"lib/index.es.js\"");
		}

		testNotContains(
			gradleProjectDir, "package.json",
			"target/classes/META-INF/resources");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, name, "com.test", "-DclassName=" + className,
			"-Dpackage=" + packageName, "-DliferayVersion=" + liferayVersion);

		if (!template.equals("npm-angular-portlet")) {
			testContains(
				mavenProjectDir, "package.json",
				"target/classes/META-INF/resources");
		}

		testNotContains(
			mavenProjectDir, "package.json",
			"build/resources/main/META-INF/resources");

		if (Validator.isNotNull(System.getenv("JENKINS_HOME"))) {
			_addNpmrc(gradleProjectDir);
			_addNpmrc(mavenProjectDir);
			_configureExecutePackageManagerTask(gradleProjectDir);
			_configurePomNpmConfiguration(mavenProjectDir);
		}

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	private File _testBuildTemplatePortlet70(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--liferay-version", "7.0.6");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "bnd.bnd", "Export-Package: foo.constants");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"");
		testContains(
			gradleProjectDir, "src/main/java/foo/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO",
			"\"foo_FooPortlet\";");
		testContains(
			gradleProjectDir, "src/main/java/foo/portlet/FooPortlet.java",
			"javax.portlet.display-name=Foo",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.foo_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo",
			"-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/foo-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortlet71(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--liferay-version", "7.1.3");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "bnd.bnd", "Export-Package: foo.constants");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir, "src/main/java/foo/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");
		testContains(
			gradleProjectDir, "src/main/java/foo/portlet/FooPortlet.java",
			"javax.portlet.display-name=Foo",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.foo_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo",
			"-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/foo-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortlet72(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--liferay-version", "7.2.1");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "bnd.bnd", "Export-Package: foo.constants");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");
		testContains(
			gradleProjectDir, "src/main/java/foo/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");
		testContains(
			gradleProjectDir, "src/main/java/foo/portlet/FooPortlet.java",
			"javax.portlet.display-name=Foo",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			"javax.portlet.title.foo_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo",
			"-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/foo-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPackage70(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception, IOException {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--package-name", "com.liferay.test");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"");
		testContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/portlet/FooPortlet.java",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo",
			"-Dpackage=com.liferay.test");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/com.liferay.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPackage71(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception, IOException {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--package-name", "com.liferay.test",
			"--liferay-version", "7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/portlet/FooPortlet.java",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo",
			"-Dpackage=com.liferay.test", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/com.liferay.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPackage72(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception, IOException {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--package-name", "com.liferay.test",
			"--liferay-version", "7.2.1");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/portlet/FooPortlet.java",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo",
			"-Dpackage=com.liferay.test", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/com.liferay.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPackage73(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception, IOException {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "foo", "--package-name", "com.liferay.test",
			"--liferay-version", "7.3.0");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/com/liferay/test/portlet/FooPortlet.java",
			"javax.portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "foo", "com.test", "-DclassName=Foo",
			"-Dpackage=com.liferay.test", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/com.liferay.test-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletName70(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletName71(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet", "--liferay-version", "7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletName72(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet", "--liferay-version", "7.2.1");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletName73(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet", "--liferay-version", "7.3.0");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletSuffix70(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet-portlet", "--liferay-version", "7.0.6");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet-portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet.portlet", "-DliferayVersion=7.0.6");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet.portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletSuffix71(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet-portlet", "--liferay-version", "7.1.3");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"3.0.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet-portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet.portlet", "-DliferayVersion=7.1.3");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet.portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletSuffix72(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet-portlet", "--liferay-version", "7.2.1");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"4.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet-portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet.portlet", "-DliferayVersion=7.2.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet.portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private File _testBuildTemplatePortletWithPortletSuffix73(
			String template, String portletClassName,
			String... resourceFileNames)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			template, "portlet-portlet", "--liferay-version", "7.3.0");

		testExists(gradleProjectDir, "bnd.bnd");
		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		for (String resourceFileName : resourceFileNames) {
			testExists(
				gradleProjectDir, "src/main/resources/" + resourceFileName);
		}

		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"5.4.0");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/constants/PortletPortletKeys.java",
			"public class PortletPortletKeys",
			"public static final String PORTLET",
			"\"portlet_portlet_PortletPortlet\";");
		testContains(
			gradleProjectDir,
			"src/main/java/portlet/portlet/portlet/PortletPortlet.java",
			"javax.portlet.name=\" + PortletPortletKeys.PORTLET",
			"public class PortletPortlet extends " + portletClassName + " {");

		File mavenProjectDir = _buildTemplateWithMaven(
			template, "portlet-portlet", "com.test", "-DclassName=Portlet",
			"-Dpackage=portlet.portlet", "-DliferayVersion=7.3.0");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		_buildProjects(gradleProjectDir, mavenProjectDir);

		if (isBuildProjects()) {
			File gradleOutputFile = new File(
				gradleProjectDir, "build/libs/portlet.portlet-1.0.0.jar");

			_testCssOutput(gradleOutputFile);
		}

		return gradleProjectDir;
	}

	private void _testBuildTemplateProjectWarInWorkspace(
			String template, String name, String liferayVersion)
		throws Exception {

		File workspaceDir;

		if (liferayVersion.equals("7.0.6")) {
			workspaceDir = buildWorkspace(temporaryFolder, liferayVersion);

			enableTargetPlatformInWorkspace(workspaceDir, liferayVersion);
		}
		else if (liferayVersion.equals("7.1.3")) {
			workspaceDir = buildWorkspace(temporaryFolder, liferayVersion);

			enableTargetPlatformInWorkspace(workspaceDir, liferayVersion);
		}
		else if (liferayVersion.equals("7.2.1")) {
			workspaceDir = buildWorkspace(temporaryFolder, liferayVersion);

			enableTargetPlatformInWorkspace(workspaceDir, liferayVersion);
		}
		else {
			workspaceDir = buildWorkspace(temporaryFolder, liferayVersion);

			enableTargetPlatformInWorkspace(workspaceDir, liferayVersion);
		}

		File warsDir = new File(workspaceDir, "wars");

		File workspaceProjectDir = buildTemplateWithGradle(
			warsDir, template, name, "--dependency-management-enabled",
			"--liferay-version", liferayVersion);

		if (!template.equals("war-hook") && !template.equals("theme")) {
			testContains(
				workspaceProjectDir, "build.gradle", "buildscript {",
				"cssBuilder group", "portalCommonCSS group");
		}

		if (template.equals("theme")) {
			testContains(
				workspaceProjectDir, "build.gradle", "buildscript {",
				"apply plugin: \"com.liferay.portal.tools.theme.builder\"",
				"repositories {");
		}

		testNotContains(
			workspaceProjectDir, "build.gradle", "apply plugin: \"war\"");
		testNotContains(
			workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
		testNotContains(
			workspaceProjectDir, "build.gradle", "version: \"[0-9].*");

		if (isBuildProjects()) {
			executeGradle(
				workspaceDir, ProjectTemplatesTest._gradleDistribution, ":wars:" + name + ":build");

			testExists(workspaceProjectDir, "build/libs/" + name + ".war");
		}
	}

	private void _testBuildTemplateRest(String liferayVersion)
		throws Exception {

		File gradleProjectDir = _buildTemplateWithGradle(
			"rest", "my-rest", "--liferay-version", liferayVersion);

		testExists(gradleProjectDir, "bnd.bnd");

		testContains(
			gradleProjectDir, "build.gradle",
			"compileOnly group: \"org.osgi\", name: " +
				"\"org.osgi.service.jaxrs\", version: \"1.0.0\"");
		testContains(
			gradleProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testNotExists(
			gradleProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties");
		testNotExists(
			gradleProjectDir,
			"src/main/resources/configuration/com.liferay.portal.remote.rest." +
				"extender.configuration.RestExtenderConfiguration-rest." +
					"properties");
		testNotExists(gradleProjectDir, "src/main/resources/configuration");

		File mavenProjectDir = buildTemplateWithMaven(
			temporaryFolder, "rest", "my-rest", "com.test", mavenExecutor,
			"-DclassName=MyRest", "-Dpackage=my.rest",
			"-DliferayVersion=" + liferayVersion);

		testContains(
			mavenProjectDir,
			"src/main/java/my/rest/application/MyRestApplication.java",
			"public class MyRestApplication extends Application");
		testNotExists(
			mavenProjectDir,
			"src/main/resources/configuration" +
				"/com.liferay.portal.remote.cxf.common.configuration." +
					"CXFEndpointPublisherConfiguration-cxf.properties");
		testNotExists(mavenProjectDir, "src/main/resources/configuration");

		_buildProjects(gradleProjectDir, mavenProjectDir);
	}

	private void _testBuildTemplateWithWorkspace(
			String template, String name, String jarFilePath, String... args)
		throws Exception {

		testBuildTemplateWithWorkspace(
			temporaryFolder, ProjectTemplatesTest._gradleDistribution, template, name, jarFilePath,
			args);
	}

	private void _testCssOutput(File outputFile) throws IOException {
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(outputFile);

			testExists(zipFile, "META-INF/resources/css/main.css");
			testExists(zipFile, "META-INF/resources/css/main_rtl.css");
		}
		finally {
			ZipFile.closeQuietly(zipFile);
		}
	}

	private File _testEquals(File dir, String fileName, String expectedContent)
		throws IOException {

		File file = testExists(dir, fileName);

		Assert.assertEquals(
			"Incorrect " + fileName, expectedContent,
			FileUtil.read(file.toPath()));

		return file;
	}

}