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

package com.liferay.oauth2.provider.web.internal.util;

import com.liferay.oauth2.provider.scope.internal.spi.scope.matcher.ChunkScopeMatcherFactory;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.oauth2.provider.web.internal.tree.Tree;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marta Medio
 */
public class ScopeTreeUtilTest {

	@Before
	public void setUp() {
		_scopeMatcherFactory = new ChunkScopeMatcherFactory();
	}

	@Test
	public void testMultipleLevelsScopeTree() {
		List<String> scopesList = Arrays.asList(
			"everything.read", "everything.write", "everything",
			"everything.read.user", "everything.read.user.documents");

		Tree.Node<String> rootTreeNode = ScopeTreeUtil.getScopeTreeNode(
			new TreeSet<>(scopesList), _scopeMatcherFactory);

		Assert.assertEquals(StringPool.BLANK, rootTreeNode.getValue());

		Tree<String> tree = _getTree(rootTreeNode, 0);

		Assert.assertEquals("everything", tree.getValue());
		Assert.assertFalse(tree instanceof Tree.Leaf);

		Tree<String> firstChildTree = _getTree((Tree.Node<String>)tree, 0);

		Assert.assertEquals("everything.read", firstChildTree.getValue());
		Assert.assertFalse(firstChildTree instanceof Tree.Leaf);

		Tree<String> firstGrandChildTree = _getTree(
			(Tree.Node<String>)firstChildTree, 0);

		Assert.assertEquals(
			"everything.read.user", firstGrandChildTree.getValue());
		Assert.assertFalse(firstGrandChildTree instanceof Tree.Leaf);
		Tree<String> greatGrandChildTree = _getTree(
			(Tree.Node<String>)firstGrandChildTree, 0);

		Assert.assertEquals(
			"everything.read.user.documents", greatGrandChildTree.getValue());

		Tree<String> lastChildTree = _getLastTree((Tree.Node<String>)tree);

		Assert.assertEquals("everything.write", lastChildTree.getValue());
		Assert.assertTrue(lastChildTree instanceof Tree.Leaf);
	}

	@Test
	public void testMultipleParentsScopeTree() {
		List<String> scopesList = Arrays.asList(
			"everything.read", "everything.write", "everything",
			"everything.read.user", "analytics.read", "analytics");

		Tree.Node<String> rootTreeNode = ScopeTreeUtil.getScopeTreeNode(
			new TreeSet<>(scopesList), _scopeMatcherFactory);

		Assert.assertEquals(StringPool.BLANK, rootTreeNode.getValue());

		Tree<String> firstTree = _getTree(rootTreeNode, 0);
		Tree<String> lastTree = _getLastTree(rootTreeNode);

		Assert.assertEquals("analytics", firstTree.getValue());
		Assert.assertFalse(firstTree instanceof Tree.Leaf);

		Tree<String> childFirstTree = _getTree((Tree.Node<String>)firstTree, 0);

		Assert.assertEquals("analytics.read", childFirstTree.getValue());

		Assert.assertTrue(childFirstTree instanceof Tree.Leaf);

		Assert.assertEquals("everything", lastTree.getValue());
		Assert.assertFalse(lastTree instanceof Tree.Leaf);

		Tree<String> childLastTree = _getTree((Tree.Node<String>)lastTree, 0);

		Assert.assertEquals("everything.read", childLastTree.getValue());
		Assert.assertFalse(childLastTree instanceof Tree.Leaf);
	}

	@Test
	public void testOneLevelScopeTree() {
		List<String> scopesList = Arrays.asList(
			"everything.read", "everything.write", "everything");

		Tree.Node<String> rootTreeNode = ScopeTreeUtil.getScopeTreeNode(
			new TreeSet<>(scopesList), _scopeMatcherFactory);

		Assert.assertEquals(StringPool.BLANK, rootTreeNode.getValue());

		final Tree<String> tree = _getTree(rootTreeNode, 0);

		Assert.assertEquals("everything", tree.getValue());
		Assert.assertFalse(tree instanceof Tree.Leaf);

		Tree<String> firstChildTree = _getTree((Tree.Node<String>)tree, 0);

		Assert.assertEquals("everything.read", firstChildTree.getValue());
		Assert.assertTrue(firstChildTree instanceof Tree.Leaf);

		Tree<String> lastChildTree = _getLastTree((Tree.Node<String>)tree);

		Assert.assertEquals("everything.write", lastChildTree.getValue());
		Assert.assertTrue(lastChildTree instanceof Tree.Leaf);
	}

	private Tree<String> _getLastTree(Tree.Node<String> node) {
		final Collection<Tree<String>> trees = node.getTrees();

		return _getTree(node, trees.size() - 1);
	}

	private List<Tree<String>> _getSortedTrees(Tree.Node<String> treeNode) {
		return ListUtil.sort(
			treeNode.getTrees(),
			Comparator.comparing(
				Tree::getValue, String.CASE_INSENSITIVE_ORDER));
	}

	private Tree<String> _getTree(Tree.Node<String> node, int indexItem) {
		final List<Tree<String>> trees = _getSortedTrees(node);

		return trees.get(indexItem);
	}

	private ScopeMatcherFactory _scopeMatcherFactory;

}