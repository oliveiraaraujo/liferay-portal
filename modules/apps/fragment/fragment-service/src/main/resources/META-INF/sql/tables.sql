create table FragmentCollection (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	fragmentCollectionId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	fragmentCollectionKey VARCHAR(75) null,
	name VARCHAR(75) null,
	description STRING null,
	lastPublishDate DATE null
);

create table FragmentEntry (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	fragmentEntryId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	fragmentCollectionId LONG,
	fragmentEntryKey VARCHAR(75) null,
	name VARCHAR(75) null,
	css TEXT null,
	html TEXT null,
	js TEXT null,
	configuration TEXT null,
	previewFileEntryId LONG,
	readOnly BOOLEAN,
	type_ INTEGER,
	lastPublishDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null
);

create table FragmentEntryLink (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	fragmentEntryLinkId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	originalFragmentEntryLinkId LONG,
	fragmentEntryId LONG,
	classNameId LONG,
	classPK LONG,
	css TEXT null,
	html TEXT null,
	js TEXT null,
	configuration TEXT null,
	editableValues TEXT null,
	namespace VARCHAR(75) null,
	position INTEGER,
	rendererKey VARCHAR(200) null,
	lastPropagationDate DATE null,
	lastPublishDate DATE null
);