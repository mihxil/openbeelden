/*

This file is part of the MMBase MMSite application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly

MMBase MMSite is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase MMSite is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.mmsite;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Utility methods for url's and pagestructure.
 *
 * @author Andr&eacute; van Toly
 * @version $Id$
 */
public final class UrlUtils {
    private static final Logger log = Logging.getLoggerInstance(UrlUtils.class);

    /**
     * Nodes starting form this node to the root, these require a field 'path'.
     *
     * @param  node	A node of some type with a field 'path'
     * @return list with all the nodes leading to the homepage including the present node
     */
    public static NodeList listNodes2Root(Node node) {
        NodeManager nm = node.getNodeManager();
        return listNodes2Root(node, nm);
    }

    /**
     * Generate a crumbpath of nodes of the same type, like for example pages, 
     * which means you get the 'most root' node first.
     * Nodes start form this node to the root, these require a field 'path'.
     *
     * @param  node	A node of some type with a field 'path'
     * @return list with all the nodes from the homepage or 'most root' node to the present node
     */
    public static NodeList crumbs(Node node) {
        NodeList l = listNodes2Root(node);
        return l;
    }
    
    /**
     * The parent node in the hierarchy.
     *
     * @param  node	A node of some type with a field 'path'
     * @return parent node
     */
    public static Node parent(Node node) {
        NodeList l = listNodes2Root(node);
        if (l.size() > 1) {
            return l.get(l.size() - 2);
        } else {
            return l.get(0);
        }
    }

    /**
     * Get the '(most) root' node, being the (grand)parent of all the nodes in the crumbpath.
     *
     * @param  node	A node of some type with a field 'path'
     * @return the node highest to the top, often the homepage
     */
    public static Node root(Node node) {
        NodeList l = listNodes2Root(node);
        return l.get(0);
    }
    
    /**
     * Retrieve a pages node with a certain path.
     *
     * @param   cloud   MMBase cloud
     * @param   path    Value of field path, f.e. '/news/new'
     * @return  a 'pages' node or null if not found
     */
    protected static Node getPagebyPath(Cloud cloud, String path) {
        Node node = null;
        if (path == null || "".equals(path)) {
            log.warn("No path '" + path + "', returning null.");
            return node;
        }
        NodeManager nm = cloud.getNodeManager("pages");
        NodeList nl = nm.getList("LOWER(path) = '" + path + "'", null, null);
        nl.addAll(nm.getList("LOWER(path) = '" + path + "/'", null, null));

        if (nl.size() > 0)  node = nl.getNode(0);
        return node;
    }

    /**
     * Nodes from here to the root while examining the field 'path'.
     * The parent of a node with path '/news/article/some' is the one
     * with '/news/article', then '/news'. It contains the node from which you 
     * want to resolve the (crumb)path.
     *
     * @param  node	A node of certain type with field path
     * @return nodes leading to homepage/root of the site including the present node
     */
    protected static NodeList listNodes2Root(Node node, NodeManager nm) {
        NodeList list = nm.createNodeList();

        String path = node.getStringValue("path");
        if (path.startsWith("/")) path = path.substring(1, path.length());
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (log.isDebugEnabled()) log.debug("path from field: " + path);

        String[] pieces = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pieces.length - 1; i++) {
            if (i > 0) sb.append("/");
            sb.append(pieces[i]);
            String ppath = sb.toString();
            if (log.isDebugEnabled()) log.debug("testing: " + ppath);

            NodeList nl = nm.getList("LOWER(path) = '" + ppath + "'", null, null);
            nl.addAll(nm.getList("LOWER(path) = '/" + ppath + "'", null, null));

            // results
            if (nl.size() > 0) {
                Node n = nl.getNode(0);
                list.add(n);
            }
        }

        list.add(node);
        return list;
    }

    /**
     * Does this url link to an external site or not.
     *
     * @param  req HttpServletRequest
     * @param  url Some link
     * @return true if external link
     */
    public Boolean externalLink(HttpServletRequest req, String url) {
        String servername = req.getServerName();
        if (url.startsWith("http://")
            && url.indexOf(servername) < 0
            ) {

            return true;
        }
        return false;
    }


}
