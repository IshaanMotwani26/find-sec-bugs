package com.h3xstream.findsecbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;

public class MyXmlDecoderDetector extends OpcodeStackDetector {

    private static final String MY_XML_DECODER_TYPE = "MY_XML_DECODER";

    private final BugReporter bugReporter;

    public MyXmlDecoderDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        // We're looking for the moment a new XMLDecoder is constructed.
        if (seen == Const.INVOKESPECIAL
                && getClassConstantOperand().equals("java/beans/XMLDecoder")
                && getNameConstantOperand().equals("<init>")) {

            bugReporter.reportBug(
                    new BugInstance(this, MY_XML_DECODER_TYPE, NORMAL_PRIORITY)
                            .addClassAndMethod(this)
                            .addSourceLine(this));
        }
    }
}
