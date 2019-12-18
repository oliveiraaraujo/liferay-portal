/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.index.contributor;

import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.spi.model.index.contributor.IndexContributor;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFilterHelper;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexDefinition;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = IndexContributor.class)
public class SynonymSetIndexContributor implements IndexContributor {

	@Override
	public void onAfterCreate(String indexName) {
		if (isSynonymIndexAvailable()) {
			_synonymSetFilterHelper.updateFilters(indexName);
		}
	}

	protected boolean isSynonymIndexAvailable() {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(SynonymSetIndexDefinition.INDEX_NAME);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SynonymSetFilterHelper _synonymSetFilterHelper;

}