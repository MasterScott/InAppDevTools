/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.sources.nodes;

public class RootNodeReader extends AbstractNodeReader {

    public RootNodeReader() {
        super();
        root = new StandardNode("root", "/", true);
    }

    @Override
    public AbstractNode populate() {
        return null;
    }

    public void addNode(String name) {
        addEntry(name, name + "/", true);
    }


    protected AbstractNode addEntry(String entryName, String entryPath, boolean isDirectory) {
        AbstractNode node = collected.get(entryPath);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new StandardNode(entryName, entryPath, isDirectory);
        collected.put(entryPath, node);
        findParent(node);
        return node;
    }

    @Override
    protected AbstractNode createParentNode(String parentName) {
        String shortName = parentName.substring(parentName.lastIndexOf("/"),
                parentName.length()-1);
        return addEntry(shortName, parentName, true);
    }
}
