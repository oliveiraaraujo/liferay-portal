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

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.converter.TaxonomyCategoryDTOConverter;
import com.liferay.headless.admin.taxonomy.internal.odata.entity.v1_0.CategoryEntityModel;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.common.spi.service.context.ServiceContextUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.asset.model.impl.AssetCategoryImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-category.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxonomyCategoryResource.class
)
public class TaxonomyCategoryResourceImpl
	extends BaseTaxonomyCategoryResourceImpl implements EntityModelResource {

	@Override
	public void deleteTaxonomyCategory(String taxonomyCategoryId)
		throws Exception {

		long assetCategoryId = _getAssetCategoryId(taxonomyCategoryId);

		_assetCategoryService.deleteCategory(assetCategoryId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public TaxonomyCategory getTaxonomyCategory(String taxonomyCategoryId)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(taxonomyCategoryId);

		ContentLanguageUtil.addContentLanguageHeader(
			assetCategory.getAvailableLanguageIds(),
			assetCategory.getDefaultLanguageId(), contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toTaxonomyCategory(assetCategory);
	}

	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoryRankedPage(
		Long siteId, Pagination pagination) {

		DynamicQuery dynamicQuery = _assetCategoryLocalService.dynamicQuery();

		if (siteId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", siteId));
		}

		dynamicQuery.addOrder(OrderFactoryUtil.desc("count"));
		dynamicQuery.setLimit(
			pagination.getStartPosition(), pagination.getEndPosition());
		dynamicQuery.setProjection(_getProjectionList(), true);

		return Page.of(
			transform(
				transform(
					_assetCategoryLocalService.dynamicQuery(dynamicQuery),
					this::_toAssetCategory),
				this::_toTaxonomyCategory));
	}

	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoryTaxonomyCategoriesPage(
			String parentTaxonomyCategoryId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			parentTaxonomyCategoryId);

		return _getCategoriesPage(
			HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				addAction(
					"ADD_CATEGORY", assetCategory.getCategoryId(),
					AssetCategory.class.getName(), assetCategory.getUserId(),
					"postTaxonomyCategoryTaxonomyCategory",
					assetCategory.getGroupId())
			).put(
				"get",
				addAction(
					"VIEW", assetCategory.getCategoryId(),
					AssetCategory.class.getName(), assetCategory.getUserId(),
					"getTaxonomyCategoryTaxonomyCategoriesPage",
					assetCategory.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						Field.ASSET_PARENT_CATEGORY_ID,
						String.valueOf(assetCategory.getCategoryId())),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts);
	}

	@Override
	public Page<TaxonomyCategory> getTaxonomyVocabularyTaxonomyCategoriesPage(
			Long taxonomyVocabularyId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _getCategoriesPage(
			HashMapBuilder.<String, Map<String, String>>put(
				"add-category",
				addAction(
					"ADD_CATEGORY", assetVocabulary,
					"postTaxonomyVocabularyTaxonomyCategory")
			).put(
				"get",
				addAction(
					"VIEW", assetVocabulary,
					"getTaxonomyVocabularyTaxonomyCategoriesPage")
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(
						Field.ASSET_PARENT_CATEGORY_ID,
						String.valueOf(
							AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID)),
					BooleanClauseOccur.MUST);
				booleanFilter.add(
					new TermFilter(
						Field.ASSET_VOCABULARY_ID,
						String.valueOf(taxonomyVocabularyId)),
					BooleanClauseOccur.MUST);
			},
			filter, search, pagination, sorts);
	}

	@Override
	public TaxonomyCategory patchTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(taxonomyCategoryId);

		if (!ArrayUtil.contains(
				assetCategory.getAvailableLanguageIds(),
				contextAcceptLanguage.getPreferredLanguageId())) {

			throw new BadRequestException(
				StringBundler.concat(
					"Unable to patch taxonomy category with language ",
					LocaleUtil.toW3cLanguageId(
						contextAcceptLanguage.getPreferredLanguageId()),
					" because it is only available in the following languages ",
					LocaleUtil.toW3cLanguageIds(
						assetCategory.getAvailableLanguageIds())));
		}

		assetCategory.setDescriptionMap(
			LocalizedMapUtil.patch(
				assetCategory.getDescriptionMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getDescription(),
				taxonomyCategory.getDescription_i18n()));
		assetCategory.setExternalReferenceCode(
			taxonomyCategory.getExternalReferenceCode());
		assetCategory.setTitleMap(
			LocalizedMapUtil.patch(
				assetCategory.getTitleMap(),
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getName(), taxonomyCategory.getName_i18n()));

		AssetCategoryPermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			assetCategory.getCategoryId(), ActionKeys.UPDATE);

		return _toTaxonomyCategory(
			_assetCategoryLocalService.updateAssetCategory(assetCategory));
	}

	@Override
	public TaxonomyCategory postTaxonomyCategoryTaxonomyCategory(
			String parentTaxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(
			parentTaxonomyCategoryId);

		return _addTaxonomyCategory(
			assetCategory.getGroupId(), assetCategory.getDefaultLanguageId(),
			taxonomyCategory, assetCategory.getCategoryId(),
			assetCategory.getVocabularyId());
	}

	@Override
	public TaxonomyCategory postTaxonomyVocabularyTaxonomyCategory(
			Long taxonomyVocabularyId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			taxonomyVocabularyId);

		return _addTaxonomyCategory(
			assetVocabulary.getGroupId(),
			assetVocabulary.getDefaultLanguageId(), taxonomyCategory, 0,
			assetVocabulary.getVocabularyId());
	}

	@Override
	public TaxonomyCategory putTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception {

		AssetCategory assetCategory = _getAssetCategory(taxonomyCategoryId);

		_validateI18n(
			false, assetCategory.getDefaultLanguageId(), taxonomyCategory);

		assetCategory.setDescriptionMap(
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getDescription(),
				taxonomyCategory.getDescription_i18n(),
				assetCategory.getTitleMap()));
		assetCategory.setExternalReferenceCode(
			taxonomyCategory.getExternalReferenceCode());
		assetCategory.setTitleMap(
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getName(), taxonomyCategory.getName_i18n(),
				assetCategory.getTitleMap()));

		AssetCategoryPermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			assetCategory.getCategoryId(), ActionKeys.UPDATE);

		return _toTaxonomyCategory(
			_assetCategoryLocalService.updateAssetCategory(assetCategory));
	}

	private TaxonomyCategory _addTaxonomyCategory(
			long groupId, String languageId, TaxonomyCategory taxonomyCategory,
			long taxonomyCategoryId, long taxonomyVocabularyId)
		throws Exception {

		_validateI18n(true, languageId, taxonomyCategory);

		AssetCategory assetCategory = _assetCategoryService.addCategory(
			groupId, taxonomyCategoryId,
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getName(), taxonomyCategory.getName_i18n()),
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				taxonomyCategory.getDescription(),
				taxonomyCategory.getDescription_i18n()),
			taxonomyVocabularyId, null,
			ServiceContextUtil.createServiceContext(
				groupId, taxonomyCategory.getViewableByAsString()));

		if (taxonomyCategory.getExternalReferenceCode() != null) {
			assetCategory.setExternalReferenceCode(
				taxonomyCategory.getExternalReferenceCode());

			assetCategory = _assetCategoryLocalService.updateAssetCategory(
				assetCategory);
		}

		return _toTaxonomyCategory(assetCategory);
	}

	private AssetCategory _getAssetCategory(String taxonomyCategoryId)
		throws Exception {

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategoryByReferenceCode(
				contextCompany.getCompanyId(), taxonomyCategoryId);

		if (assetCategory == null) {
			assetCategory = _assetCategoryService.getCategory(
				GetterUtil.getLong(taxonomyCategoryId));
		}

		return assetCategory;
	}

	private long _getAssetCategoryId(String taxonomyCategoryId) {
		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategoryByReferenceCode(
				contextCompany.getCompanyId(), taxonomyCategoryId);

		if (assetCategory == null) {
			return GetterUtil.getLong(taxonomyCategoryId);
		}

		return assetCategory.getCategoryId();
	}

	private Page<TaxonomyCategory> _getCategoriesPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			Filter filter, String keywords, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer, filter, AssetCategory.class,
			keywords, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ASSET_CATEGORY_ID),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toTaxonomyCategory(
				_assetCategoryService.getCategory(
					GetterUtil.getLong(
						document.get(Field.ASSET_CATEGORY_ID)))));
	}

	private ProjectionList _getProjectionList() {
		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(
			ProjectionFactoryUtil.alias(
				ProjectionFactoryUtil.sqlProjection(
					"(select count(entryId) AS count from " +
						"AssetEntries_AssetCategories where categoryId = " +
							"this_.categoryId group by categoryId) AS count",
					new String[] {"count"}, new Type[] {Type.INTEGER}),
				"count"));
		projectionList.add(ProjectionFactoryUtil.property("categoryId"));
		projectionList.add(ProjectionFactoryUtil.property("companyId"));
		projectionList.add(ProjectionFactoryUtil.property("createDate"));
		projectionList.add(ProjectionFactoryUtil.property("groupId"));
		projectionList.add(ProjectionFactoryUtil.property("modifiedDate"));
		projectionList.add(ProjectionFactoryUtil.property("name"));
		projectionList.add(ProjectionFactoryUtil.property("userId"));

		return projectionList;
	}

	private AssetCategory _toAssetCategory(Object[] assetCategory) {
		return new AssetCategoryImpl() {
			{
				setCategoryId((long)assetCategory[1]);
				setCompanyId((long)assetCategory[2]);
				setCreateDate(_toDate((Timestamp)assetCategory[3]));
				setGroupId((long)assetCategory[4]);
				setModifiedDate(_toDate((Timestamp)assetCategory[5]));
				setName((String)assetCategory[6]);
				setUserId((long)assetCategory[7]);
			}
		};
	}

	private Date _toDate(Timestamp timestamp) {
		return new Date(timestamp.getTime());
	}

	private TaxonomyCategory _toTaxonomyCategory(AssetCategory assetCategory)
		throws Exception {

		return _taxonomyCategoryDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.<String, Map<String, String>>put(
					"add-category",
					addAction(
						"ADD_CATEGORY", assetCategory,
						"postTaxonomyCategoryTaxonomyCategory")
				).put(
					"delete",
					addAction("DELETE", assetCategory, "deleteTaxonomyCategory")
				).put(
					"get",
					addAction("VIEW", assetCategory, "getTaxonomyCategory")
				).put(
					"replace",
					addAction("UPDATE", assetCategory, "putTaxonomyCategory")
				).put(
					"update",
					addAction("UPDATE", assetCategory, "patchTaxonomyCategory")
				).build(),
				_dtoConverterRegistry, assetCategory.getCategoryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			assetCategory);
	}

	private void _validateI18n(
		boolean add, String languageId, TaxonomyCategory taxonomyCategory) {

		Locale defaultLocale = LocaleUtil.fromLanguageId(languageId);

		if (LocaleUtil.equals(
				defaultLocale, contextAcceptLanguage.getPreferredLocale())) {

			return;
		}

		Map<String, String> localizedNames = taxonomyCategory.getName_i18n();

		if ((add && (localizedNames == null)) ||
			(!add && (localizedNames != null) &&
			 !localizedNames.containsKey(
				 LocaleUtil.toBCP47LanguageId(defaultLocale)))) {

			String w3cLanguageId = LocaleUtil.toW3cLanguageId(defaultLocale);

			throw new BadRequestException(
				"Taxonomy categories must include the default language " +
					w3cLanguageId);
		}
	}

	private static final EntityModel _entityModel = new CategoryEntityModel();

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private TaxonomyCategoryDTOConverter _taxonomyCategoryDTOConverter;

}