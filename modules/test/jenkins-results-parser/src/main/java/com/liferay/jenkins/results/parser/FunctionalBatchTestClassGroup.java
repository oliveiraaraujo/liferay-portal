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

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Yi-Chen Tsai
 */
public class FunctionalBatchTestClassGroup extends BatchTestClassGroup {

	public String getRelevantTestBatchRunPropertyQuery() {
		return _relevantTestBatchRunPropertyQuery;
	}

	protected FunctionalBatchTestClassGroup(
		String batchName, GitWorkingDirectory gitWorkingDirectory,
		String testSuiteName) {

		super(batchName, gitWorkingDirectory, testSuiteName);

		_setRelevantTestBatchRunPropertyQuery();
	}

	private String _getDefaultTestBatchRunPropertyQuery() {
		List<String> propertyNames = new ArrayList<>();

		if (testSuiteName != null) {
			propertyNames.add(
				getWildcardPropertyName(
					portalTestProperties, "test.batch.run.property.query",
					testSuiteName));

			propertyNames.add(
				JenkinsResultsParserUtil.combine(
					"test.batch.run.property.query[", testSuiteName, "]"));
		}

		propertyNames.add(
			getWildcardPropertyName(
				portalTestProperties, "test.batch.run.property.query"));

		propertyNames.add(
			JenkinsResultsParserUtil.combine(
				"test.batch.run.property.query[", batchName, "]"));

		propertyNames.add("test.batch.run.property.query");

		return getFirstPropertyValue(portalTestProperties, propertyNames);
	}

	private void _setRelevantTestBatchRunPropertyQuery() {
		if (testRelevantChanges) {
			PortalGitWorkingDirectory portalGitWorkingDirectory =
				(PortalGitWorkingDirectory)gitWorkingDirectory;

			List<File> modifiedModuleDirsList = null;

			try {
				modifiedModuleDirsList =
					portalGitWorkingDirectory.getModifiedModuleDirsList();
			}
			catch (IOException ioe) {
				File workingDirectory =
					gitWorkingDirectory.getWorkingDirectory();

				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to get module directories in ",
						workingDirectory.getPath()),
					ioe);
			}

			StringBuilder sb = new StringBuilder();

			for (File modifiedModuleDir : modifiedModuleDirsList) {
				File modifiedModuleTestProperties = new File(
					modifiedModuleDir, "test.properties");

				if (!modifiedModuleTestProperties.exists()) {
					continue;
				}

				Properties testProperties =
					JenkinsResultsParserUtil.getProperties(
						modifiedModuleTestProperties);

				String testBatchRunPropertyQuery = testProperties.getProperty(
					JenkinsResultsParserUtil.combine(
						"test.batch.run.property.query[", batchName, "][",
						testSuiteName, "]"));

				if (testBatchRunPropertyQuery != null) {
					if (sb.length() > 0) {
						sb.append(" OR (");
					}
					else {
						sb.append("(");
					}

					sb.append(testBatchRunPropertyQuery);
					sb.append(")");
				}
			}

			if (sb.length() > 0) {
				_relevantTestBatchRunPropertyQuery = sb.toString();
			}
		}

		if (_relevantTestBatchRunPropertyQuery == null) {
			_relevantTestBatchRunPropertyQuery =
				_getDefaultTestBatchRunPropertyQuery();
		}
	}

	private String _relevantTestBatchRunPropertyQuery;

}