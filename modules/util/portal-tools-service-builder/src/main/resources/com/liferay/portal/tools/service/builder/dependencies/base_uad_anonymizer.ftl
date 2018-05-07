package ${entity.UADPackagePath}.uad.anonymizer;

import ${apiPackagePath}.model.${entity.name};
import ${apiPackagePath}.service.${entity.name}LocalService;
import ${entity.UADPackagePath}.uad.constants.${entity.UADApplicationName}UADConstants;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.user.associated.data.anonymizer.DynamicQueryUADAnonymizer;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the ${entity.humanName} UAD anonymizer.
 *
 * <p>
 * This implementation exists only as a container for the default methods
 * generated by ServiceBuilder. All custom service methods should be put in
 * {@link ${entity.UADPackagePath}.uad.anonymizer.${entity.name}UADAnonymizer}.
 * </p>
 *
 * @author ${author}
 * @generated
 */
public abstract class Base${entity.name}UADAnonymizer extends DynamicQueryUADAnonymizer<${entity.name}> {

	@Override
	public void autoAnonymize(${entity.name} ${entity.varName}, long userId, User anonymousUser) throws PortalException {
		<#list entity.UADUserIdColumnNames as uadUserIdColumnName>
			<#assign uadUserIdEntityColumn = entity.getEntityColumn(uadUserIdColumnName) />

					if (${entity.varName}.get${uadUserIdEntityColumn.methodName}() == userId) {
			<#list entity.UADAnonymizableEntityColumnsMap[uadUserIdColumnName] as uadAnonymizableEntityColumn>
				${entity.varName}.set${uadAnonymizableEntityColumn.methodName}(anonymousUser.get${textFormatter.format(uadAnonymizableEntityColumn.UADAnonymizeFieldName, 6)}());
			</#list>
					}
		</#list>

		${entity.varName}LocalService.update${entity.name}(${entity.varName});
	}

	@Override
	public void delete(${entity.name} ${entity.varName}) throws PortalException {
		${entity.varName}LocalService.${deleteUADEntityMethodName}(${entity.varName});
	}

	@Override
	public String getApplicationName() {
		return ${entity.UADApplicationName}UADConstants.APPLICATION_NAME;
	}

	@Override
	public List<String> getNonanonymizableFieldNames() {
		return Arrays.asList(<#list entity.UADNonanonymizableEntityColumns as uadNonanonymizableEntityColumn>"${uadNonanonymizableEntityColumn.name}"<#sep>, </#sep></#list>);
	}

	@Override
	public Class<${entity.name}> getTypeClass() {
		return ${entity.name}.class;
	}

	@Override
	protected ActionableDynamicQuery doGetActionableDynamicQuery() {
		return ${entity.varName}LocalService.getActionableDynamicQuery();
	}

	@Override
	protected String[] doGetUserIdFieldNames() {
		return ${entity.UADApplicationName}UADConstants.USER_ID_FIELD_NAMES_${entity.constantName};
	}

	@Reference
	protected ${entity.name}LocalService ${entity.varName}LocalService;

}