/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.script;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@RunWith(JUnit3RunnerWithInners.class)
public class ScriptConfigurationHighlightingTestGenerated extends AbstractScriptConfigurationHighlightingTest {
    @TestMetadata("idea/testData/script/definition/highlighting")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Highlighting extends AbstractScriptConfigurationHighlightingTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, TargetBackend.ANY, testDataFilePath);
        }

        @TestMetadata("acceptedAnnotations")
        public void testAcceptedAnnotations() throws Exception {
            runTest("idea/testData/script/definition/highlighting/acceptedAnnotations/");
        }

        @TestMetadata("additionalImports")
        public void testAdditionalImports() throws Exception {
            runTest("idea/testData/script/definition/highlighting/additionalImports/");
        }

        public void testAllFilesPresentInHighlighting() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/script/definition/highlighting"), Pattern.compile("^([^\\.]+)$"), TargetBackend.ANY, false);
        }

        @TestMetadata("asyncResolver")
        public void testAsyncResolver() throws Exception {
            runTest("idea/testData/script/definition/highlighting/asyncResolver/");
        }

        @TestMetadata("conflictingModule")
        public void testConflictingModule() throws Exception {
            runTest("idea/testData/script/definition/highlighting/conflictingModule/");
        }

        @TestMetadata("customBaseClass")
        public void testCustomBaseClass() throws Exception {
            runTest("idea/testData/script/definition/highlighting/customBaseClass/");
        }

        @TestMetadata("customExtension")
        public void testCustomExtension() throws Exception {
            runTest("idea/testData/script/definition/highlighting/customExtension/");
        }

        @TestMetadata("customJavaHome")
        public void testCustomJavaHome() throws Exception {
            runTest("idea/testData/script/definition/highlighting/customJavaHome/");
        }

        @TestMetadata("customLibrary")
        public void testCustomLibrary() throws Exception {
            runTest("idea/testData/script/definition/highlighting/customLibrary/");
        }

        @TestMetadata("customLibraryInModuleDeps")
        public void testCustomLibraryInModuleDeps() throws Exception {
            runTest("idea/testData/script/definition/highlighting/customLibraryInModuleDeps/");
        }

        @TestMetadata("doNotSpeakAboutJava")
        public void testDoNotSpeakAboutJava() throws Exception {
            runTest("idea/testData/script/definition/highlighting/doNotSpeakAboutJava/");
        }

        @TestMetadata("doNotSpeakAboutJavaLegacy")
        public void testDoNotSpeakAboutJavaLegacy() throws Exception {
            runTest("idea/testData/script/definition/highlighting/doNotSpeakAboutJavaLegacy/");
        }

        @TestMetadata("emptyAsyncResolver")
        public void testEmptyAsyncResolver() throws Exception {
            runTest("idea/testData/script/definition/highlighting/emptyAsyncResolver/");
        }

        @TestMetadata("errorResolver")
        public void testErrorResolver() throws Exception {
            runTest("idea/testData/script/definition/highlighting/errorResolver/");
        }

        @TestMetadata("javaNestedClass")
        public void testJavaNestedClass() throws Exception {
            runTest("idea/testData/script/definition/highlighting/javaNestedClass/");
        }

        @TestMetadata("multiModule")
        public void testMultiModule() throws Exception {
            runTest("idea/testData/script/definition/highlighting/multiModule/");
        }

        @TestMetadata("noResolver")
        public void testNoResolver() throws Exception {
            runTest("idea/testData/script/definition/highlighting/noResolver/");
        }

        @TestMetadata("propertyAccessor")
        public void testPropertyAccessor() throws Exception {
            runTest("idea/testData/script/definition/highlighting/propertyAccessor/");
        }

        @TestMetadata("propertyAccessorFromModule")
        public void testPropertyAccessorFromModule() throws Exception {
            runTest("idea/testData/script/definition/highlighting/propertyAccessorFromModule/");
        }

        @TestMetadata("simple")
        public void testSimple() throws Exception {
            runTest("idea/testData/script/definition/highlighting/simple/");
        }

        @TestMetadata("throwingResolver")
        public void testThrowingResolver() throws Exception {
            runTest("idea/testData/script/definition/highlighting/throwingResolver/");
        }
    }

    @TestMetadata("idea/testData/script/definition/complex")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Complex extends AbstractScriptConfigurationHighlightingTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doComplexTest, TargetBackend.ANY, testDataFilePath);
        }

        public void testAllFilesPresentInComplex() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/script/definition/complex"), Pattern.compile("^([^\\.]+)$"), TargetBackend.ANY, false);
        }

        @TestMetadata("errorResolver")
        public void testErrorResolver() throws Exception {
            runTest("idea/testData/script/definition/complex/errorResolver/");
        }
    }
}
