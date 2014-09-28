/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.test;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.ArtifactGenerator;
import org.pentaho.aggdes.output.CreateScriptGenerator;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.OutputFactory;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.output.PopulateScriptGenerator;
import org.pentaho.aggdes.output.impl.OutputServiceImpl;
import org.pentaho.aggdes.test.algorithm.impl.AggregateStub;
import org.pentaho.aggdes.test.algorithm.impl.SchemaLoaderStub;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;

import junit.framework.TestCase;

public class OutputServiceImplTest extends TestCase {

    public static class OutputTestImpl implements Output {
        public Schema schema;
        public Aggregate aggregate;
        public String uniqueName = "output-00";

        public OutputTestImpl(Schema schema, Aggregate aggregate) {
            this.schema = schema;
            this.aggregate = aggregate;
        }

        public Schema getSchema() {
            return schema;
        }

        public Aggregate getAggregate() {
           return aggregate;
        }
    }

    public static class SecondOutputTestImpl implements Output {
        public Schema schema;
        public Aggregate aggregate;
        public String uniqueName = "output-00";

        public SecondOutputTestImpl(Schema schema, Aggregate aggregate) {
            this.schema = schema;
            this.aggregate = aggregate;
        }

        public Schema getSchema() {
            return schema;
        }

        public Aggregate getAggregate() {
           return aggregate;
        }
    }


    public static class OutputFactoryTestImpl implements OutputFactory {
        boolean canCreate = false;

        public boolean canCreateOutput(Schema schema) {
            // TODO Auto-generated method stub
            return canCreate;
        }

        public Output createOutput(Schema schema, Aggregate aggregate) {
            return new OutputTestImpl(schema, aggregate);
        }

        public Class<? extends Output> getOutputClass() {
            return OutputTestImpl.class;
        }

        public List<Output> createOutputs(Schema schema, List<Aggregate> aggregates) {
            List<Output> outputs = new ArrayList<Output>();
            for (Aggregate aggregate : aggregates) {
                outputs.add(createOutput(schema, aggregate));
            }
            return outputs;
        }
    }

    public static class ArtifactGeneratorTestImpl implements CreateScriptGenerator {

        public boolean canGenerate = false;
        public Class[] classes = new Class[] {OutputTestImpl.class};

        public boolean canGenerate(Schema schema, Output output) {
            return canGenerate;
        }

        public String generate(Schema schema, Output output) {
            return "generated " + ((OutputTestImpl)output).uniqueName;
        }

        public String generateFull(Schema schema, List<? extends Output> outputs) {
            StringBuilder sb = new StringBuilder();
            for (Output output : outputs) {
                sb.append(generate(schema, output)).append("\n");
            }
            return sb.toString();
        }

        public Class[] getSupportedOutputClasses() {
            return classes;
        }


    }

    public static class SmartArtifactGeneratorTestImpl implements CreateScriptGenerator {

      public Class[] classes = new Class[] {OutputTestImpl.class};

      public boolean canGenerate(Schema schema, Output output) {
          return output instanceof OutputTestImpl;
      }

      public String generate(Schema schema, Output output) {
          return "generated " + ((OutputTestImpl)output).uniqueName;
      }

      public String generateFull(Schema schema, List<? extends Output> outputs) {
          StringBuilder sb = new StringBuilder();
          for (Output output : outputs) {
              sb.append(generate(schema, output)).append("\n");
          }
          return sb.toString();
      }

      public Class[] getSupportedOutputClasses() {
          return classes;
      }


  }

    public void testInit() throws OutputValidationException {
        Schema schema = new SchemaStub();

        OutputServiceImpl outputServiceImpl = new OutputServiceImpl();

        // wipe out standard factories and generators
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        OutputFactoryTestImpl outputFactoryTestImpl = new OutputFactoryTestImpl();
        outputServiceImpl.getOutputFactories().add(outputFactoryTestImpl);

        AggregateStub aggregate = new AggregateStub();

        outputFactoryTestImpl.canCreate = true;

        Output output = outputServiceImpl.generateDefaultOutput(aggregate);

        assertEquals(((OutputTestImpl)output).getSchema(), null);

        outputServiceImpl.init(schema);

        output = outputServiceImpl.generateDefaultOutput(aggregate);

        assertEquals(((OutputTestImpl)output).getSchema(), schema);

        outputServiceImpl = new OutputServiceImpl(schema);
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        outputServiceImpl.getOutputFactories().add(outputFactoryTestImpl);

        output = outputServiceImpl.generateDefaultOutput(aggregate);



        assertEquals(((OutputTestImpl)output).getSchema(), schema);

    }

    public void testGenerateDefaultOutput() throws OutputValidationException {
        Schema schema = new SchemaStub();

        OutputServiceImpl outputServiceImpl = new OutputServiceImpl();

        // wipe out standard factories and generators
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        OutputFactoryTestImpl outputFactoryTestImpl = new OutputFactoryTestImpl();
        List<OutputFactory> outputFactories = new ArrayList<OutputFactory>();
        outputFactories.add(outputFactoryTestImpl);

        // add testing objects
        outputServiceImpl.setOutputFactories(outputFactories);

        AggregateStub aggregate = new AggregateStub();

        // test happy path

        outputFactoryTestImpl.canCreate = true;

        Output output = outputServiceImpl.generateDefaultOutput(aggregate);
        assertTrue(output instanceof OutputTestImpl);

        // test output factory search

        outputFactoryTestImpl.canCreate = false;
        try {
            outputServiceImpl.generateDefaultOutput(aggregate);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(e.getMessage().indexOf("Failed to locate Output Factory.") >= 0);
        }

        // test null object

        try {
            outputServiceImpl.generateDefaultOutput(null);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(e.getMessage().indexOf("No Aggregate Provided.") >= 0);
        }
    }

    public void testGenerateArtifact() throws OutputValidationException {
        Schema schema = new SchemaStub();

        OutputServiceImpl outputServiceImpl = new OutputServiceImpl();

        // wipe out standard factories and generators
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        OutputFactoryTestImpl outputFactoryTestImpl = new OutputFactoryTestImpl();

        // add testing objects
        outputServiceImpl.getOutputFactories().add(outputFactoryTestImpl);
        ArtifactGeneratorTestImpl artgen = new ArtifactGeneratorTestImpl();
        List<ArtifactGenerator> artgens = new ArrayList<ArtifactGenerator>();
        artgens.add(artgen);
        outputServiceImpl.setArtifactGenerators(artgens);

        AggregateStub aggregate = new AggregateStub();

        outputFactoryTestImpl.canCreate = true;
        artgen.canGenerate = true;

        Output output = outputServiceImpl.generateDefaultOutput(aggregate);
        ((OutputTestImpl)output).uniqueName = "output-01";

        // abstract generator

        String results = outputServiceImpl.getArtifact(output, CreateScriptGenerator.class);
        assertEquals("generated output-01", results);

        // concrete generator
        ((OutputTestImpl)output).uniqueName = "output-02";

        results = outputServiceImpl.getArtifact(output, ArtifactGeneratorTestImpl.class);
        assertEquals("generated output-02", results);

        // null script gen
        try {
            outputServiceImpl.getArtifact(output, null);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Generator Provided", e.getMessage());
        }

        // null output
        try {
            outputServiceImpl.getArtifact(null, ArtifactGeneratorTestImpl.class);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Output Provided", e.getMessage());
        }

        // both null
        try {
            outputServiceImpl.getArtifact(null, null);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Output Provided", e.getMessage());
        }

        // invalid generator type
        try {
            outputServiceImpl.getArtifact(output, PopulateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                e.getMessage().indexOf(
                    "Failed to locate generator of type interface org.pentaho.aggdes.output.PopulateScriptGenerator"
                        ) >= 0
            );
        }

        // invalid output type
        try {
            outputServiceImpl.getArtifact(new SecondOutputTestImpl(schema, aggregate), CreateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                    e.getMessage().indexOf(
                        "Failed to locate generator of type interface org.pentaho.aggdes.output.CreateScriptGenerator"
                            ) >= 0
                );
        }

        artgen.canGenerate = false;
        //
        try {
            outputServiceImpl.getArtifact(output, CreateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                    e.getMessage().indexOf(
                        "Failed to locate generator of type interface org.pentaho.aggdes.output.CreateScriptGenerator"
                            ) >= 0
                );
        }
    }

    public void testGenerateFullArtifact() throws OutputValidationException {
        Schema schema = new SchemaStub();

        OutputServiceImpl outputServiceImpl = new OutputServiceImpl();

        // wipe out standard factories and generators
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        OutputFactoryTestImpl outputFactoryTestImpl = new OutputFactoryTestImpl();

        // add testing objects
        outputServiceImpl.getOutputFactories().add(outputFactoryTestImpl);
        ArtifactGeneratorTestImpl artgen = new ArtifactGeneratorTestImpl();
        outputServiceImpl.getArtifactGenerators().add(artgen);

        AggregateStub aggregate = new AggregateStub();

        outputFactoryTestImpl.canCreate = true;
        artgen.canGenerate = true;
        List<Output> outputs = new ArrayList<Output>();
        Output output1 = outputServiceImpl.generateDefaultOutput(aggregate);
        outputs.add(output1);
        Output output2 = outputServiceImpl.generateDefaultOutput(aggregate);
        outputs.add(output2);
        ((OutputTestImpl)output1).uniqueName = "output-01";
        ((OutputTestImpl)output2).uniqueName = "output-02";

        // abstract generator

        String results = outputServiceImpl.getFullArtifact(outputs, CreateScriptGenerator.class);
        assertEquals("generated output-01\ngenerated output-02\n", results);

        // concrete generator
        ((OutputTestImpl)output1).uniqueName = "output-03";
        ((OutputTestImpl)output2).uniqueName = "output-04";

        results = outputServiceImpl.getFullArtifact(outputs, ArtifactGeneratorTestImpl.class);
        assertEquals("generated output-03\ngenerated output-04\n", results);

        // null script gen
        try {
            outputServiceImpl.getFullArtifact(outputs, null);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Generator Provided", e.getMessage());
        }

        // null output
        try {
            outputServiceImpl.getFullArtifact(null, ArtifactGeneratorTestImpl.class);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Output Provided", e.getMessage());
        }

        // both null
        try {
            outputServiceImpl.getArtifact(null, null);
            fail();
        } catch (OutputValidationException e) {
            assertEquals("No Output Provided", e.getMessage());
        }

        // invalid generator type
        try {
            outputServiceImpl.getFullArtifact(outputs, PopulateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                e.getMessage().indexOf(
                    "Failed to locate generator of type interface org.pentaho.aggdes.output.PopulateScriptGenerator"
                        ) >= 0
            );
        }

        // invalid output type

        List<Output> secondOutputs = new ArrayList<Output>();
        secondOutputs.add(new SecondOutputTestImpl(schema, aggregate));

        try {
            outputServiceImpl.getFullArtifact(secondOutputs, CreateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                    e.getMessage().indexOf(
                        "Failed to locate generator of type interface org.pentaho.aggdes.output.CreateScriptGenerator"
                            ) >= 0
                );
        }

        artgen.canGenerate = false;
        //
        try {
            outputServiceImpl.getFullArtifact(outputs, CreateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                    e.getMessage().indexOf(
                        "Failed to locate generator of type interface org.pentaho.aggdes.output.CreateScriptGenerator"
                            ) >= 0
                );
        }

        artgen.canGenerate = false;
        outputServiceImpl.getArtifactGenerators().add(new SmartArtifactGeneratorTestImpl());

        outputs.add(new SecondOutputTestImpl(null, null));

        // test mixed output type errors
        try {
            outputServiceImpl.getFullArtifact(outputs, CreateScriptGenerator.class);
            fail();
        } catch (OutputValidationException e) {
            assertTrue(
                    e.getMessage().indexOf(
                        "Generator org.pentaho.aggdes.test.OutputServiceImplTest$SmartArtifactGeneratorTestImpl cannot generate output"
                            ) >= 0
                );
        }
    }

    @SuppressWarnings("unchecked")
    public void testClassList() {
        OutputServiceImpl outputServiceImpl = new OutputServiceImpl();
        outputServiceImpl.getOutputFactories().clear();
        outputServiceImpl.getArtifactGenerators().clear();

        ArtifactGeneratorTestImpl artgen = new ArtifactGeneratorTestImpl();
        outputServiceImpl.getArtifactGenerators().add(artgen);

        // without correct output factory

        Class[] classes = outputServiceImpl.getSupportedArtifactGeneratorClasses();
        assertNotNull(classes);
        assertEquals(classes.length, 0);

        // with output factory, but can't create
        OutputFactoryTestImpl outputFactoryTestImpl = new OutputFactoryTestImpl();
        outputServiceImpl.getOutputFactories().add(outputFactoryTestImpl);

        classes = outputServiceImpl.getSupportedArtifactGeneratorClasses();
        assertNotNull(classes);
        assertEquals(classes.length, 0);

        // with output factory, can create
        outputFactoryTestImpl.canCreate = true;
        classes = outputServiceImpl.getSupportedArtifactGeneratorClasses();
        assertNotNull(classes);
        assertEquals(classes.length, 1);
        assertEquals(classes[0], ArtifactGeneratorTestImpl.class);

        // with output factory, but artifact generator
        artgen.classes = new Class[] {String.class}; // bogus class

        classes = outputServiceImpl.getSupportedArtifactGeneratorClasses();
        assertNotNull(classes);
        assertEquals(classes.length, 0);
    }
}
