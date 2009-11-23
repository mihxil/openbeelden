/*

This file is part of the Open Images Platform.

The Open Images Platform is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Open Images Platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with The Open Images Platform.  If not, see <http://www.gnu.org/licenses/>.

*/

package eu.openimages;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;


/**
 * This commit-processor is used on nodes of the type 'mediafragments' and
 * deletes associated 'mediasources' when a 'mediafragments' node is deleted.
 * To a 'mediasources' belonging nodes of type 'streamsources' in Streams will be 
 * deleted by org.mmbase.streams.DeleteCachesProcessor.
 *
 * @author André van Toly
 * @version $Id$
 */

public class DeleteTranslationsProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    public static String NOT = DeleteTranslationsProcessor.class.getName() + ".DONOT";

    private static final Logger LOG = Logging.getLoggerInstance(DeleteTranslationsProcessor.class);
    
    
    public void commit(final Node node, final Field field) {

        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        
        if (node.getNumber() > 0) {
            NodeList translations = SearchUtil.findRelatedNodeList(node, 
                node.getNodeManager().getProperty("translations.builders"), "langrel");
            LOG.info("Deleting " + translations.size() + " translations of #" + node.getNumber());
            for (Node tr : translations) {
                if (tr.mayDelete()) {
                    tr.delete(true);
                } else {
                    LOG.warn("May not delete #" + tr);
                }
            }            
        }
    }

}