package org.openscience.cdk.libio.cml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.StringWriter;

/**
 * @author John May
 */
class PDBAtomCustomizerTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(PDBAtomCustomizerTest.class);

    @Test
    void testPDBAtomCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IPDBAtom atom = new PDBAtom("C");
        atom.setName("CA");
        atom.setResName("PHE");
        molecule.addAtom(atom);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new PDBAtomCustomizer());
        cmlWriter.write(molecule);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testPDBAtomCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assertions.assertTrue(cmlContent.contains("<scalar dictRef=\"pdb:resName"));
    }
}
