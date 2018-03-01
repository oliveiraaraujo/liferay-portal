import State, {Config} from 'metal-state';
import {addClasses, dom, hasClass, removeClasses} from 'metal-dom';
import {Drag, DragDrop} from 'metal-drag-drop';
import position from 'metal-position';

/**
 *	Site navigation menu editor component.
 */

class SiteNavigationMenuEditor extends State {

	/**
	 * @inheritDoc
	 */

	constructor(config, ...args) {
		super(config, ...args);

		this.setState(config);

		this._dragDrop = new DragDrop(
			{
				dragPlaceholder: Drag.Placeholder.CLONE,
				handles: '.sticker',
				sources: this.menuItemSelector,
				targets: `${this.menuContainerSelector} ${this.menuItemSelector}`
			});

		this._dragDrop.on(
			DragDrop.Events.DRAG,
			this._handleDragItem.bind(this)
		);
		this._dragDrop.on(Drag.Events.START, this._handleDragStart.bind(this));
		this._dragDrop.on(DragDrop.Events.END, this._handleDropItem.bind(this));

		dom.on(
			this.menuItemSelector,
			'click',
			this._handleItemClick.bind(this)
		);

		dom.on(
			'.site-navigation-menu-container .container-item',
			'keyup',
			this._handleItemKeypUp.bind(this)
		);
	}

	/**
	 * @inheritDoc
	 */

	dispose(...args) {
		this._dragDrop.dispose();

		super.dispose(...args);
	}

	/**
	 * This is called when user drags the item across the container.
	 *
	 * @param {!object} data Drag event data
	 * @param {!Event} event Drag event
	 * @private
	 */

	_handleDragItem(data, event) {
		const placeholder = data.placeholder;
		const source = data.source;
		const target = data.target;

		if (target &&
			target !== source &&
			!source.parentNode.contains(target) &&
			target.dataset.siteNavigationMenuItemId) {

			const placeholderRegion = position.getRegion(placeholder);
			const targetRegion = position.getRegion(target);

			const nested = placeholderRegion.right - targetRegion.right > placeholderRegion.width / 3;

			removeClasses(source.parentNode, 'container-item--nested');

			let newParentId = target.dataset.parentSiteNavigationMenuItemId;

			if (placeholderRegion.top < targetRegion.top) {
				target.parentNode.parentNode.insertBefore(source.parentNode, target.parentNode);
			}
			else if (!nested && (placeholderRegion.bottom > targetRegion.bottom)) {
				target.parentNode.insertBefore(
					source.parentNode,
					target.nextSibling
				);
			}
			else if (nested && placeholderRegion.bottom > targetRegion.bottom) {
				target.parentNode.insertBefore(
					source.parentNode,
					target.nextSibling
				);

				newParentId = target.dataset.siteNavigationMenuItemId;

				addClasses(source.parentNode, 'container-item--nested');
			}

			source.dataset.parentId = newParentId;

			const parent = document.querySelector(`[data-site-navigation-menu-item-id="${newParentId}"]`).parentNode;

			const children = Array.from(parent.querySelectorAll('.container-item'))
				.filter(
					(node) =>
						(node === source.parentNode) ||
						(Array.from(parent.children).indexOf(node) != -1)
				);

			const order = children.reduce(
				(previousValue, currentValue, index) => {
					return currentValue === source.parentNode ? index : previousValue;
				},
				0
			);

			source.dataset.dragOrder = order;
		}
	}

	/**
	 * This is called when user starts to drag the item across the container.
	 *
	 * @param {!object} data Drag event data
	 * @param {!Event} event Drag event
	 * @private
	 */

	_handleDragStart(data, event) {
		const item = event.target.getActiveDrag();

		addClasses(item.parentNode, 'item-dragging');
	}

	/**
	 * This is called when user drops the item on the container.
	 *
	 * @param {!object} data Drop event data
	 * @param {!Event} event Drop event
	 * @private
	 */

	_handleDropItem(data, event) {
		event.preventDefault();

		if (data.source && data.source.dataset.siteNavigationMenuItemId) {
			this._updateParentAndOrder(
				data.source.dataset,
				() => {
					if (Liferay.SPA) {
						Liferay.SPA.app.navigate(window.location.href);
					}
					else {
						window.location.reload();
					}
				}
			);
		}
		else {
			removeClasses(data.source.parentNode, 'item-dragging');
		}
	}

	/**
	 * This is called when user clicks on menu item.
	 *
	 * @param {!Event} event Click event data
	 * @private
	 */

	_handleItemClick(event) {
		removeClasses(document.querySelectorAll('.selected'), 'selected');

		addClasses(event.delegateTarget, 'selected');

		event.delegateTarget.parentNode.focus();
	}

	/**
	 * This is called when user presses a key on menu item.
	 *
	 * @param {!Event} event KeyUp event data
	 * @private
	 */

	_handleItemKeypUp(event) {
		const menuItem = event.delegateTarget.querySelector('.site-navigation-menu-item');

		if (event.keyCode === 13 || event.keyCode === 32) {
			menuItem.click();
		}

		if (!hasClass(menuItem, 'selected')) {
			return;
		}

		let parentItem = document.querySelector(
			'[data-site-navigation-menu-item-id="0"]');

		if (menuItem.dataset.parentSiteNavigationMenuItemId > 0) {
			parentItem = parentItem.parentNode.querySelector(
				`[data-site-navigation-menu-item-id="${menuItem.dataset.parentSiteNavigationMenuItemId}"]`);
		}

		let menuItems = Array.prototype.slice.call(
			parentItem.parentNode.querySelectorAll(this.menuItemSelector));

		menuItems = menuItems.filter(
			item => item.parentNode.parentNode === parentItem.parentNode
		);

		let newIndex = -1;
		let parentItems = [];

		if (event.keyCode === 37) {
			if (menuItem.dataset.parentSiteNavigationMenuItemId > 0) {
				parentItem.parentNode.parentNode.insertBefore(
					menuItem.parentNode, parentItem.parentNode.nextSibling);

				menuItem.dataset.parentSiteNavigationMenuItemId =
					parentItem.dataset.parentSiteNavigationMenuItemId;

				if (parentItem.dataset.parentSiteNavigationMenuItemId == 0) {
					removeClasses(
						menuItem.parentNode, 'container-item--nested');
				}

				parentItems = Array.prototype.slice.call(
					parentItem.parentNode.parentNode.querySelectorAll(
						this.menuItemSelector));

				parentItems = parentItems.filter(
					item => item.parentNode.parentNode ===
								parentItem.parentNode.parentNode
				);

				menuItem.dataset.order = parentItems.indexOf(menuItem);
			}
		}
		else if (event.keyCode === 38) {
			newIndex = menuItems.indexOf(menuItem) - 1;

			if (newIndex < 0) {
				return;
			}

			menuItems[newIndex].parentNode.parentNode.insertBefore(
				menuItem.parentNode, menuItems[newIndex].parentNode);

			menuItem.dataset.order = newIndex;
		}
		else if (event.keyCode === 39) {
			newIndex = menuItems.indexOf(menuItem) - 1;

			if (newIndex < 0) {
				return;
			}

			menuItems[newIndex].parentNode.appendChild(menuItem.parentNode);

			if (!hasClass(menuItem.parentNode, 'container-item--nested')) {
				addClasses(menuItem.parentNode, 'container-item--nested');
			}

			menuItem.dataset.parentSiteNavigationMenuItemId =
				menuItems[newIndex].dataset.siteNavigationMenuItemId;

			parentItems = Array.prototype.slice.call(
				menuItems[newIndex].parentNode.querySelectorAll(
					this.menuItemSelector));

			parentItems = parentItems.filter(
				item => item.parentNode.parentNode ===
							menuItems[newIndex].parentNode);

			menuItem.dataset.order = parentItems.indexOf(menuItem);
		}
		else if (event.keyCode === 40) {
			newIndex = menuItems.indexOf(menuItem) + 1;

			if (newIndex < menuItems.length - 1) {
				menuItems[newIndex].parentNode.parentNode.insertBefore(
					menuItem.parentNode,
					menuItems[newIndex].parentNode.nextSibling);
			}
			else if (newIndex === menuItems.length - 1) {
				parentItem.parentNode.appendChild(menuItem.parentNode);
			}

			menuItem.dataset.order = newIndex;
		}

		if ((event.keyCode > 36) && (event.keyCode < 41)) {
			this._updateParentAndOrder(
				{
					dragOrder: menuItem.dataset.order,
					parentId: menuItem.dataset.parentSiteNavigationMenuItemId,
					siteNavigationMenuItemId: menuItem.dataset.siteNavigationMenuItemId

				},
				() => {});
		}

		menuItem.parentNode.focus();
	}

	_updateParentAndOrder(data, callback) {
		const formData = new FormData();

		formData.append(
			`${this.namespace}siteNavigationMenuItemId`,
			data.siteNavigationMenuItemId
		);

		formData.append(
			`${this.namespace}parentSiteNavigationMenuItemId`,
			data.parentId
		);

		formData.append(
			`${this.namespace}order`,
			data.dragOrder
		);

		fetch(
			this.editSiteNavigationMenuItemParentURL,
			{
				body: formData,
				credentials: 'include',
				method: 'POST'
			}
		).then(callback);
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */

SiteNavigationMenuEditor.STATE = {

	/**
	 * URL for edit site navigation menu item parent action.
	 *
	 * @default undefined
	 * @instance
	 * @memberOf SiteNavigationMenuEditor
	 * @type {!string}
	 */

	editSiteNavigationMenuItemParentURL: Config.string().required(),

	/**
	 * Selector to get site navigation menu container.
	 *
	 * @default undefined
	 * @instance
	 * @memberOf SiteNavigationMenuEditor
	 * @type {!string}
	 */

	menuContainerSelector: Config.string().required(),

	/**
	 * Selector to get all site navigation menu items.
	 *
	 * @default undefined
	 * @instance
	 * @memberOf SiteNavigationMenuEditor
	 * @type {!string}
	 */

	menuItemSelector: Config.string().required(),

	/**
	 * Portlet namespace to use in edit action.
	 *
	 * @default undefined
	 * @instance
	 * @memberOf SiteNavigationMenuEditor
	 * @type {!string}
	 */

	namespace: Config.string().required(),

	/**
	 * Internal DragDrop instance.
	 *
	 * @default null
	 * @instance
	 * @memberOf SiteNavigationMenuEditor
	 * @type {State}
	 */

	_dragDrop: Config.internal().value(null)
};

export {SiteNavigationMenuEditor};
export default SiteNavigationMenuEditor;