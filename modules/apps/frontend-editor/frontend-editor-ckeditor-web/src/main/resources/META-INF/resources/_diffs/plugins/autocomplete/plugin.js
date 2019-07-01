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

(function() {
	CKEDITOR.plugins.add('autocomplete', {
		init: function(editor) {
			var instance = this;

			AUI().use('aui-debounce', 'liferay-autocomplete-input', function(
				A
			) {
				var path = instance.path;

				var dependencies = [CKEDITOR.getUrl(path + 'autocomplete.js')];

				CKEDITOR.scriptLoader.load(dependencies, function() {
					var liferayAutoCompleteCKEditor = new Liferay.AutoCompleteCKEditor(
						A.merge(editor.config.autocomplete, {
							editor: editor,
							width: 300
						})
					);

					liferayAutoCompleteCKEditor.render();

					liferayAutoCompleteCKEditor.detach('valueChange');
				});
			});
		}
	});
})();
