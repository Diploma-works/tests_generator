package org.dasxunya.diploma.generator;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import org.dasxunya.diploma.constants.Constants;
import org.dasxunya.diploma.constants.TestType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PsiMethodGeneratorTests extends BaseTest {

    //region Тесты

    @Test
    void testGenerateTypeAssert() {
        //region Типы
        PsiType[] types = this.getTypesList();
        //endregion
        //region Параметры
        PsiParameter[] parameters = {mockPsiParameterString, mockPsiParameterInt, mockPsiParameterBoolean, mockPsiParameterByte, mockPsiParameterChar, mockPsiParameterShort, mockPsiParameterLong, mockPsiParameterFloat, mockPsiParameterDouble};
        //endregion
        StringBuilder stringBuilder = new StringBuilder();
        for (PsiType type : types) {
            stringBuilder.append(String.format("Возвращаемый тип '%s'", type.getPresentableText()));
            stringBuilder.append("\n");
            mockReturnMethod = createPsiMethod(type, String.format("%sMethod", type.getPresentableText()), parameters);
            stringBuilder.append(generator.generateTypeAssert(mockReturnMethod.getReturnType(), generator.getMethodCallString(mockReturnMethod)));
            stringBuilder.append("\n");
        }
        String actualFileName = "testGenerateTypeAssert";
        String expectedFileName = "testGenerateTypeAssert";
        this.saveFile(stringBuilder.toString(), this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt);
        try {
            this.compareFilesByPath(
                    combinePath(this.expectedFolderPath, expectedFileName, Constants.Strings.Extensions.txt),
                    combinePath(this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt)
            );
            if (this.isDeleteActualFiles)
                deleteFile(this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt);
        } catch (Exception e) {
            println(e.getMessage());
        }
    }

    @SuppressWarnings("ConstantValue")
    @Test
    void testGenerate_NullMethod() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            PsiMethod testMethod = null;
            generator.generate(testMethod, TestType.UNIT);
        });
        // Проверяем, что сообщение исключения соответствует ожидаемому
        assertEquals(Constants.Strings.Release.Errors.NULL_POINTER, exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> {
            PsiMethod testMethod = null;
            generator.generate(testMethod, TestType.PARAMETERIZED);

        });

        // Проверяем, что сообщение исключения соответствует ожидаемому
        assertEquals(Constants.Strings.Release.Errors.NULL_POINTER, exception.getMessage());
    }

    @Test
    void testGetInfo() {
        this.generator.setDebug(true);
        PsiMethod[] methods = {mockConstructor, mockVoidMethod, mockReturnMethod, mockNoParamMethod};
        StringBuilder stringBuilder = new StringBuilder();
        for (PsiMethod method : methods) {
            stringBuilder.append(generator.getInfo(method));
            stringBuilder.append("\n");
        }
        String actualFileName = "testGetInfo";
        String expectedFileName = "testGetInfo";
        this.saveFile(stringBuilder.toString(), this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt);
        try {
            this.compareFilesByName(actualFileName, expectedFileName, Constants.Strings.Extensions.txt);
            if (this.isDeleteActualFiles)
                deleteFile(this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt);
        } catch (Exception e) {
            println(e.getMessage());
        }

    }

    @Test
    void testGetClassHeader() {
        String actualFileName = "testGetClassHeader";
        String psiClassHeader = this.generator.getClassHeader(this.mockPsiClass, TestType.PARAMETERIZED);
        System.out.println(psiClassHeader);
        this.saveFile(psiClassHeader, this.actualFolderPath, "testGetClassHeader", Constants.Strings.Extensions.txt);
        try {
            this.compareFilesByName(actualFileName, "testGetClassHeader", Constants.Strings.Extensions.txt);
            if (this.isDeleteActualFiles)
                deleteFile(this.actualFolderPath, actualFileName, Constants.Strings.Extensions.txt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //region Тестирование генерации юнит тестов
    @Test
    void testGenerateMethod_UnitTest() {
//        this.generator.setDebug(true);
//        String parameterizedTestStr = generator.generate(mockVoidMethod, TestType.PARAMETERIZED);
//        System.out.println(parameterizedTestStr);
        PsiParameter[] parameters = {
                mockPsiParameterString, mockPsiParameterInt,
                mockPsiParameterBoolean, mockPsiParameterByte,
                mockPsiParameterChar, mockPsiParameterShort,
                mockPsiParameterLong, mockPsiParameterFloat,
                mockPsiParameterDouble
        };
        String testNamePrefix = "testGenerateMethod_UnitTest";
        for (PsiType type : this.getTypesList()) {
            String fileName = String.format("%s_%s", testNamePrefix, type.getPresentableText());
            mockReturnMethod = setupReturnMethod(type);
            String returnTypeName = type.getCanonicalText().toLowerCase();

            //region Генерация ожидаемого файла
            StringBuilder stringBuilder = new StringBuilder();
            // Импорты и название класса
            stringBuilder.append(this.generator.getClassHeader(this.mockPsiClass, TestType.UNIT));
            stringBuilder.append("\n");
            stringBuilder.append("{\n");
            stringBuilder.append(this.generateJavaMethod(mockReturnMethod));
            stringBuilder.append("\n");

            stringBuilder.append("@Test");
            stringBuilder.append("\n");

            //region Тестирующий метод
            stringBuilder.append(String.format("public void test%s() {", this.generator.capitalize(mockReturnMethod.getName())));
            stringBuilder.append("\n");
            stringBuilder.append("\t// TODO: Тестирование логики");
            stringBuilder.append("\n");
            String expectedValueVarName = "expectedValue";
            switch (returnTypeName) {
                case Constants.Strings.Types.stringType:
                    stringBuilder.append(String.format("\tAssertions.assertNotNull(%s);",
                            this.generator.getMethodCallString(mockReturnMethod)));
                    stringBuilder.append("\n");
                    break;
                case Constants.Strings.Types.booleanType:
                    stringBuilder.append(String.format("\tAssertions.assertTrue(%s);",
                            this.generator.getMethodCallString(mockReturnMethod)));
                    stringBuilder.append("\n");
                    stringBuilder.append(String.format("\tAssertions.assertFalse(%s);",
                            this.generator.getMethodCallString(mockReturnMethod)));
                    stringBuilder.append("\n");
                    break;
                case Constants.Strings.Types.intType:
                case Constants.Strings.Types.longType:
                case Constants.Strings.Types.shortType:
                case Constants.Strings.Types.byteType:
                case Constants.Strings.Types.charType:
                    stringBuilder.append(String.format("\t%s %s = 0; // Укажите ожидаемое значение", type.getPresentableText(), expectedValueVarName));
                    stringBuilder.append("\n");
                    stringBuilder.append(String.format("\tAssertions.assertEquals(%s, %s);",
                            expectedValueVarName, this.generator.getMethodCallString(mockReturnMethod)));
                    stringBuilder.append("\n");
                    break;
                case Constants.Strings.Types.doubleType:
                case Constants.Strings.Types.floatType:
                    stringBuilder.append(String.format("\t%s %s = 0; // Укажите ожидаемое значение", type.getPresentableText(), expectedValueVarName));
                    stringBuilder.append("\n");
                    stringBuilder.append(String.format("\tAssertions.assertEquals(%s, %s, 0.01); // Укажите дельту для float и double",
                            expectedValueVarName, this.generator.getMethodCallString(mockReturnMethod)));
                    stringBuilder.append("\n");
                    break;
                case Constants.Strings.Types.voidType:
                    UnitTestsGenerator g = new UnitTestsGenerator();
                    for (PsiParameter psiParameter : parameters) {
                        String exampleDataStr = g.generateExampleData(psiParameter.getType());
                        String dataStr = psiParameter.getType().getPresentableText().equalsIgnoreCase(Constants.Strings.Types.stringType) ? String.format("\"%s\"", exampleDataStr) : exampleDataStr;
                        g.append(stringBuilder, Constants.Strings.Code.tabulation, String.format("Assertions.assertEquals(%s, %s);", psiParameter.getName(), dataStr), Constants.Strings.Code.newLine);
                    }
                default:
                    break;
            }
            stringBuilder.append("\t// TODO: Добавить другие проверки");
            stringBuilder.append("\n");
            stringBuilder.append("}\n");

            stringBuilder.append("\n");
            stringBuilder.append("}\n");
            //endregion

            // Сохранение
            this.saveFile(stringBuilder.toString(), this.expectedFolderPath, fileName, Constants.Strings.Extensions.txt);
            //endregion

            this.startGeneratorTest(fileName,
                    Constants.Strings.Extensions.txt, mockReturnMethod, TestType.UNIT, this.isDebug);
            this.deleteFile(this.expectedFolderPath, fileName, Constants.Strings.Extensions.txt);
        }
    }
    //endregion

    //region Тестирование генерации тестов с параметрами
    @Test
    void testGenerateMethod_ParameterizedTest() {
        this.startGeneratorTest("testGenerateMethod_ParameterizedTest",
                Constants.Strings.Extensions.txt, mockVoidMethod, TestType.PARAMETERIZED, this.isDebug, true);
    }

    @Test
    public void testGenerateConstructor_ParameterizedTest() {
        this.startGeneratorTest("testGenerateConstructor_ParameterizedTest",
                Constants.Strings.Extensions.txt, mockConstructor, TestType.PARAMETERIZED, this.isDebug, true);
    }

    @Test
    public void testGenerateReturnMethod_ParameterizedTest() {
        this.startGeneratorTest("testGenerateReturnMethod_ParameterizedTest",
                Constants.Strings.Extensions.txt, mockReturnMethod, TestType.PARAMETERIZED, this.isDebug, true);
    }

    @Test
    public void testGenerateNoParamMethod_ParameterizedTest() {
        this.startGeneratorTest("testGenerateNoParamMethod_ParameterizedTest",
                Constants.Strings.Extensions.txt, mockNoParamMethod, TestType.PARAMETERIZED, this.isDebug, true);
    }
    //endregion
    //endregion

}
