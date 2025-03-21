/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@slists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.debug.DebugAtomContainer;
import org.openscience.cdk.debug.DebugChemFile;
import org.openscience.cdk.debug.DebugChemModel;
import org.openscience.cdk.debug.DebugReaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.IRGroupQuery;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;

/**
 * TestCase for CDK IO classes.
 *
 */
public abstract class ChemObjectIOTest extends CDKTestCase {

    private static IChemObjectIO chemObjectIO;

    static void setChemObjectIO(IChemObjectIO aChemObjectIO) {
        chemObjectIO = aChemObjectIO;
    }

    @Test
    void testChemObjectIOSet() {
        Assertions.assertNotNull(chemObjectIO, "You must use setChemObjectIO() to set the IChemObjectIO object.");
    }

    @Test
    void testGetFormat() {
        IResourceFormat format = chemObjectIO.getFormat();
        Assertions.assertNotNull(format, "The IChemObjectIO.getFormat method returned null.");
    }

    private static final IChemObject[] acceptableNNChemObjects = {new ChemFile(), new ChemModel(), SilentChemObjectBuilder.getInstance().newAtomContainer(),
            new Reaction()                               };

    @Test
    void testAcceptsAtLeastOneNonotifyObject() {
        boolean oneAccepted = false;
        for (IChemObject object : acceptableNNChemObjects) {
            if (chemObjectIO.accepts(object.getClass())) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction");
    }

    private static final IChemObject[] acceptableDebugChemObjects = {new DebugChemFile(), new DebugChemModel(),
            new DebugAtomContainer(), new DebugReaction()   };

    @Test
    void testAcceptsAtLeastOneDebugObject() {
        boolean oneAccepted = false;
        for (IChemObject object : acceptableDebugChemObjects) {
            if (chemObjectIO.accepts(object.getClass())) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction");
    }

    /** static objects, shared between tests - difficult to locate bugs. */
    @Deprecated
    protected static final IChemObject[] acceptableChemObjects = {new ChemFile(), new ChemModel(), SilentChemObjectBuilder.getInstance().newAtomContainer(),
            new Reaction(), new RGroupQuery(DefaultChemObjectBuilder.getInstance())};

    static IChemObject[] acceptableChemObjects() {
        return new IChemObject[]{new ChemFile(), new ChemModel(), SilentChemObjectBuilder.getInstance().newAtomContainer(), new Reaction(),
                new RGroupQuery(DefaultChemObjectBuilder.getInstance())};
    }

    @Test
    void testAcceptsAtLeastOneChemObject() {
        boolean oneAccepted = false;
        for (IChemObject object : acceptableChemObjects) {
            if (chemObjectIO.accepts(object.getClass())) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction, IRGroupQuery");
    }

    @SuppressWarnings("rawtypes")
    private static final Class[] acceptableChemObjectClasses = {IChemFile.class, IChemModel.class, IAtomContainer.class,
            IReaction.class, IRGroupQuery.class          };

    /**
     * @cdk.bug 3553780
     */
    @SuppressWarnings("unchecked")
    @Test
    void testAcceptsAtLeastOneChemObjectClass() {
        boolean oneAccepted = false;
        for (Class<? extends IChemObject> clazz : acceptableChemObjectClasses) {
            if (chemObjectIO.accepts(clazz)) {
                oneAccepted = true;
            }
        }
        Assertions.assertTrue(oneAccepted, "At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IAtomContainer, IReaction, IRGroupQuery");
    }

    @Test
    void testClose() throws Exception {
        chemObjectIO.close();
    }

    @Test
    void testGetIOSetting() {
        IOSetting[] settings = chemObjectIO.getIOSettings();
        for (IOSetting setting : settings) {
            Assertions.assertNotNull(setting);
            Assertions.assertNotNull(setting.getDefaultSetting());
            Assertions.assertNotNull(setting.getName());
            Assertions.assertNotNull(setting.getQuestion());
            Assertions.assertNotNull(setting.getLevel());
        }
    }

    @Test
    void testAddChemObjectIOListener() {
        MyListener listener = new MyListener();
        chemObjectIO.addChemObjectIOListener(listener);
    }

    class MyListener implements IChemObjectIOListener {

        private int timesCalled = 0;

        @Override
        public void processIOSettingQuestion(IOSetting setting) {
            timesCalled++;
        }
    }

    @Test
    void testRemoveChemObjectIOListener() {
        MyListener listener = new MyListener();
        chemObjectIO.addChemObjectIOListener(listener);
        chemObjectIO.removeChemObjectIOListener(listener);
    }

}
