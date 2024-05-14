package org.dasxunya.diploma.generator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
//import lombok.Getter;
import org.dasxunya.diploma.constants.Constants;
import org.dasxunya.diploma.constants.TestType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Генератор юнит тестов для классов и методов
 */
//@Getter
public class UnitTestsGenerator {

    //region Поля
    private boolean isDebug;
    //endregion

    //region Сеттеры/Геттеры
    public void setDebug(boolean debug) {
        isDebug = debug;
    }
    //endregion

    //region Конструкторы
    public UnitTestsGenerator() {
        this.setDebug(false);
    }

    public UnitTestsGenerator(boolean isDebug) {
        this.setDebug(isDebug);
    }
    //endregion

    //region Методы

    //region Вывод ошибок и отладочной инофрмации
    private void print(String message) {
        System.out.print(message);
    }

    private void printLn(String message) {
        System.out.println(message);
    }

    private <T> void throwNullPointerException(Class<T> type) throws NullPointerException {
        if (this.isDebug) this.printLn(Constants.Strings.Debug.Errors.NULL_POINTER + type.getSimpleName());
        throw new NullPointerException(Constants.Strings.Release.Errors.NULL_POINTER);
    }

    private <T> void throwIllegalArgumentException(Class<T> type) throws IllegalArgumentException {
        if (this.isDebug) this.printLn(Constants.Strings.Debug.Errors.ILLEGAL_ARGUMENT + type.getSimpleName());
        throw new IllegalArgumentException(Constants.Strings.Release.Errors.ILLEGAL_ARGUMENT);
    }

    private void throwException(String messageRelease, String messageDebug) throws Exception {
        if (this.isDebug && messageDebug != null && !messageDebug.isEmpty()) this.printLn(messageDebug);
        throw new Exception(messageRelease);
    }
    //endregion

    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Добавляет содержимое в StringBuilder с возможностью добавления префикса и суффикса.
     *
     * @param sb      StringBuilder, к которому будет добавлено содержимое. Не должен быть null.
     * @param prefix  Опциональный префикс, который будет добавлен после содержимого. Может быть null.
     * @param content Содержимое, которое будет добавлено. Не должно быть null.
     * @param suffix  Опциональный суффикс, который будет добавлен после содержимого. Может быть null.
     */
    public void append(StringBuilder sb, String prefix, String content, String suffix) {
        // Проверяем входные параметры на null
        Objects.requireNonNull(sb, "StringBuilder не инициализирован");
        Objects.requireNonNull(content, "Content не инициализирован");

        // Строим строку на основе префикса, содержимого и суффикса
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append(content);
        if (suffix != null) {
            sb.append(suffix);
        }
    }

    public void append(StringBuilder sb, String prefix, String[] lines, String suffix) {
        for (String line : lines) {
            this.append(sb, prefix, line, suffix);
        }
    }

    /**
     * Добавляет содержимое в StringBuilder с возможностью добавления префикса и суффикса.
     *
     * @param sb      StringBuilder, к которому будет добавлено содержимое. Не должен быть null.
     * @param content Содержимое, которое будет добавлено. Не должно быть null.
     * @param suffix  Опциональный суффикс, который будет добавлен после содержимого. Может быть null.
     */
    public void append(StringBuilder sb, String content, String suffix) {
        this.append(sb, null, content, suffix);
    }

    public String generateExampleData(PsiType type) {
        String typeName = type.getPresentableText();
        switch (typeName) {
            case "int":
                return "0";
            case "double":
                return "0.0";
            case "String":
                return "exampleString";
            case "boolean":
                return "true";
            case "byte":
                return "0";
            case "char":
                return "'a'";
            case "short":
                return "0";
            case "long":
                return "0";
            case "float":
                return "0.0f";
            default:
                return "null";  // По умолчанию для всех неизвестных или сложных типов
        }
    }

    public String getMethodCallString(PsiMethod psiMethod) throws NullPointerException {
        if (psiMethod == null)
            throwNullPointerException(PsiMethod.class);
        if (psiMethod != null) {
            String methodName = psiMethod.getName();
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            StringJoiner parameterCalls = new StringJoiner(", ");
            for (PsiParameter parameter : parameters) {
                String name = parameter.getName();
                parameterCalls.add(name);
            }
            return String.format("%s(%s)", methodName, parameterCalls);
        }
        return null;
    }

    @SuppressWarnings("DataFlowIssue")
    public String generateTypeAssert(PsiType psiType, String actualExpression) throws NullPointerException {
        if (psiType == null)
            throwNullPointerException(PsiType.class);
        StringBuilder stringBuilder = new StringBuilder();
        if (psiType != null) {
            String returnTypeText = psiType.getCanonicalText();
            switch (returnTypeText.toLowerCase()) {
                case Constants.Strings.Types.booleanType:
                    stringBuilder.append(String.format("%s(%s);\n", Constants.Strings.Tests.Assertions.assertTrue, actualExpression));
                    stringBuilder.append(String.format("%s(%s);\n", Constants.Strings.Tests.Assertions.assertFalse, actualExpression));
                    break;
                case Constants.Strings.Types.intType:
                case Constants.Strings.Types.longType:
                case Constants.Strings.Types.shortType:
                case Constants.Strings.Types.byteType:
                case Constants.Strings.Types.charType:
                    stringBuilder.append(String.format("%s expectedValue = 0; // Укажите ожидаемое значение\n", returnTypeText));
                    stringBuilder.append(String.format("%s(expectedValue, %s);\n", Constants.Strings.Tests.Assertions.assertEqual, actualExpression));
                    break;
                case Constants.Strings.Types.doubleType:
                case Constants.Strings.Types.floatType:
                    stringBuilder.append(String.format("%s expectedValue = 0; // Укажите ожидаемое значение\n", returnTypeText));
                    stringBuilder.append(String.format("%s(expectedValue, %s, 0.01); // Укажите дельту для float и double\n", Constants.Strings.Tests.Assertions.assertEqual, actualExpression));
                    break;
                case Constants.Strings.Types.voidType:
                    break;
                default:
                    stringBuilder.append(String.format("Assertions.assertNotNull(%s);\n", actualExpression));
                    break;
            }
        }
        return stringBuilder.toString();
    }

    private String generateMethodAssert(PsiMethod psiMethod) throws NullPointerException {
        if (psiMethod == null) throwNullPointerException(PsiMethod.class);
        StringBuilder sb = new StringBuilder();
        if (psiMethod != null) {
            //region Получение данных метода
            String methodName = psiMethod.getName();
            PsiType returnType = psiMethod.getReturnType();
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            StringJoiner parameterCalls = new StringJoiner(", ");
            for (PsiParameter parameter : parameters) {
                String name = parameter.getName();
                parameterCalls.add(name);
            }
            //endregion
            if (returnType == null)
                throwNullPointerException(PsiType.class);
            //region Выбор утверждения в зависимости от типа возвращаемого значения
            String methodCallText = String.format("%s(%s)", methodName, parameterCalls);
            if (returnType != null) {
                String returnTypeText = returnType.getCanonicalText();
                switch (returnTypeText.toLowerCase()) {
                    case Constants.Strings.Types.booleanType:
                        this.append(sb, String.format("%s(%s)", Constants.Strings.Tests.Assertions.assertTrue, methodCallText), Constants.Strings.Code.semiColonNewLine);
                        this.append(sb, String.format("%s(%s)", Constants.Strings.Tests.Assertions.assertFalse, methodCallText), Constants.Strings.Code.semiColonNewLine);
                        break;
                    case Constants.Strings.Types.intType:
                    case Constants.Strings.Types.longType:
                    case Constants.Strings.Types.shortType:
                    case Constants.Strings.Types.byteType:
                    case Constants.Strings.Types.charType:
                        this.append(sb, String.format("%s expectedValue = 0; // Укажите ожидаемое значение", returnTypeText), Constants.Strings.Code.newLine);
                        this.append(sb, String.format("%s(expectedValue, %s)", Constants.Strings.Tests.Assertions.assertEqual, methodCallText), Constants.Strings.Code.semiColonNewLine);
                        break;
                    case Constants.Strings.Types.doubleType:
                    case Constants.Strings.Types.floatType:
                        this.append(sb, String.format("%s expectedValue = 0; // Укажите ожидаемое значение", returnTypeText), Constants.Strings.Code.newLine);
                        this.append(sb, String.format("%s(expectedValue, %s, 0.01); // Укажите дельту для float и double",
                                Constants.Strings.Tests.Assertions.assertEqual, methodCallText), Constants.Strings.Code.newLine);
                        break;
                    case Constants.Strings.Types.voidType:
                        for (PsiParameter psiParameter : parameters) {
                            String exampleDataStr = this.generateExampleData(psiParameter.getType());
                            String dataStr = psiParameter.getType().getPresentableText().equalsIgnoreCase(Constants.Strings.Types.stringType) ? String.format("\"%s\"", exampleDataStr) : exampleDataStr;
                            this.append(sb, String.format("Assertions.assertEquals(%s, %s)", psiParameter.getName(), dataStr), Constants.Strings.Code.semiColonNewLine);
                        }
                        break;
                    default:
                        this.append(sb, String.format("Assertions.assertNotNull(%s)", methodCallText), Constants.Strings.Code.semiColonNewLine);
                        break;
                }
            }
            //endregion
        }

        return sb.toString();
    }

    @SuppressWarnings({"StringBufferReplaceableByString", "DataFlowIssue"})
    public String getInfo(PsiMethod psiMethod) throws NullPointerException {
        //region Проверка ссылки на объект
        if (psiMethod == null) this.throwNullPointerException(PsiMethod.class);
        //endregion
        StringBuilder info = new StringBuilder();
        info.append("Название: ").append(psiMethod.getName()).append("\n");
        //region Возвращаемый тип
        PsiType returnType = psiMethod.getReturnType();
        String returnTypeName = (returnType != null) ? returnType.getPresentableText() : "void";
        info.append("Возвращаемый тип: ").append(returnTypeName).append("\n");
        //endregion
        info.append("Сигнатура: ").append(psiMethod.getSignature(PsiSubstitutor.EMPTY)).append("\n");
        //region Получение и вывод параметров метода
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        if (parameters.length == 0) {
            info.append("Параметры: Нет параметров");
        } else {
            info.append("Параметры: ");
            StringBuilder parametersInfo = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                PsiParameter parameter = parameters[i];
                String typeName = parameter.getType().getPresentableText(); // Получаем текстовое представление типа параметра
                String parameterName = parameter.getName(); // Получаем имя параметра
                if (i > 0) parametersInfo.append(", "); // Добавляем запятую между параметрами
                parametersInfo.append(typeName).append(" ").append(parameterName);
            }
            info.append(parametersInfo.toString());
        }
        //endregion
        return info.toString();
    }

    /**
     * Возвращает строку, представляющую собой пустой тестирующий класс
     *
     * @param psiClass
     * @return
     */
    public String getClassHeader(PsiClass psiClass, TestType testType) {
        if (psiClass == null) this.throwNullPointerException(PsiClass.class);
        StringBuilder stringBuilder = new StringBuilder();
        //region Список добавляемых библиотек и сборок
        ArrayList<String> imports = new ArrayList<>();
        // Получение имени пакета класса
        PsiFile psiFile = psiClass.getContainingFile();
        PsiDirectory psiDirectory = psiFile.getContainingDirectory();
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
        if (psiPackage == null)
            throw new NullPointerException("Не удалось получить пакет, которому принадлежит класс");
        // Импорт пакета
        stringBuilder.append(String.format("package %s;\n", psiPackage.getQualifiedName()));
        // Добавление библиотек
        imports.add(Constants.Strings.Imports.orgJunitJupiterAll);
        imports.add(Constants.Strings.Imports.orgJunitJupiterParamsAll);
        imports.add(Constants.Strings.Imports.orgJunitJupiterParamsProviderAll);
        //endregion
        //region Добавление бибилотек в код тестирующего класса
        for (String importStr : imports)
            stringBuilder.append(String.format("import %s;\n", importStr));
        //endregion
        //region Формирование имени класса
        stringBuilder.append(String.format("class %sTests", psiClass.getName()));
        //endregion
        return stringBuilder.toString();
    }

    // Метод для генерации тестового класса для всех методов в классе
    public String generate(PsiClass psiClass, TestType testType) {
        return generate(psiClass, null, testType); // null означает все методы
    }

    // Перегруженный метод для генерации тестового класса для одного конкретного метода
    public String generate(PsiClass psiClass, PsiMethod psiMethod, TestType testType) {
        if (psiClass == null) this.throwNullPointerException(PsiClass.class);
        StringBuilder stringBuilder = new StringBuilder();
        //region Формирование заголовкка класса с импортами
        stringBuilder.append(this.getClassHeader(psiClass, testType));
        //endregion
        //region Формирование тела тестирующего класса
        stringBuilder.append("{");
        stringBuilder.append("\n");
        if (this.isDebug) stringBuilder.append("// Методы класса:\n");
        if (psiMethod != null) {
            stringBuilder.append(this.generate(psiMethod, testType));
        } else {
            for (PsiMethod method : psiClass.getMethods())
                stringBuilder.append(this.generate(method, testType));
        }
        stringBuilder.append("}");
        stringBuilder.append("\n");
        //endregion
        return stringBuilder.toString();
    }

    @SuppressWarnings("DataFlowIssue")
    public String generate(PsiMethod psiMethod, TestType testType) {
        //region Проверка ссылки на объект
        if (psiMethod == null) this.throwNullPointerException(PsiMethod.class);
        //endregion
        StringBuilder sb = new StringBuilder();
        //region Вывод отладной информации о методе
        if (this.isDebug) this.printLn(this.getInfo(psiMethod));
        //endregion

        //region Основные свойства метода
        String methodName = psiMethod.getName();

        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        String testMethodName = "test" + capitalize(methodName);
        StringJoiner parameterListWithTypes = new StringJoiner(", ");

        StringJoiner parameterNames = new StringJoiner(", ");
        StringJoiner parameterList = new StringJoiner(", ");
        for (PsiParameter parameter : parameters) {
            String parameterType = parameter.getType().getPresentableText();
            String name = parameter.getName();
            parameterListWithTypes.add(parameterType + " " + name);
        }
        //endregion

        if (testType == TestType.PARAMETERIZED && parameters.length > 0) {
            sb.append("@ParameterizedTest\n");
            sb.append("@CsvSource({\n");
            //region Добавление тестовых строк данных
            StringJoiner csvData = new StringJoiner("\",\n    \"", "    \"", "\"\n");
            StringBuilder testData = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) testData.append(", ");
                PsiType type = parameters[i].getType();
                testData.append(generateExampleData(type));
            }
            csvData.add(testData.toString());
            csvData.add(testData.toString());
            sb.append(csvData.toString());
            //endregion
            sb.append("})\n");
            //region Генерация тела параметризованного теста
            sb.append("public void ").append(testMethodName).append("(");
            sb.append(parameterListWithTypes.toString());
            sb.append(") {\n");
            this.append(sb, Constants.Strings.Code.tabulation, "// TODO: Тестирование логики", Constants.Strings.Code.newLine);
            this.append(sb, Constants.Strings.Code.tabulation, this.generateMethodAssert(psiMethod).split(Constants.Strings.Code.newLine), Constants.Strings.Code.newLine);
            this.append(sb, Constants.Strings.Code.tabulation, "// TODO: Добавить другие проверки", Constants.Strings.Code.newLine);
            sb.append("}\n");
            //endregion
        } else {
            //region Генерация тела юнит теста
            for (PsiParameter parameter : parameters) {
                String parameterType = parameter.getType().getPresentableText();
                String name = parameter.getName();
                // Здесь мы используем просто имена переменных,
                // в реальном случае может потребоваться инициализация с дефолтными значениями
                parameterList.add(parameterType + " " + name);
            }
            this.append(sb, "@Test", Constants.Strings.Code.newLine);
            this.append(sb, String.format("public void %s() ", testMethodName), Constants.Strings.Code.openBrace);
//            sb.append(Constants.Strings.Code.openBrace);
            this.append(sb, Constants.Strings.Code.tabulation, "// TODO: Тестирование логики", Constants.Strings.Code.newLine);
            //this.append(sb, Constants.Strings.Code.tabulation, this.generateMethodAssert(psiMethod), Constants.Strings.Code.newLine);
            this.append(sb, Constants.Strings.Code.tabulation, this.generateMethodAssert(psiMethod).split(Constants.Strings.Code.newLine), Constants.Strings.Code.newLine);
            this.append(sb, Constants.Strings.Code.tabulation, "// TODO: Добавить другие проверки", Constants.Strings.Code.newLine);
            sb.append(Constants.Strings.Code.closeBrace);
            //endregion
        }
        String result = sb.toString();
        sb.setLength(0);
        return result;
    }

    public String generate(PsiElement element, TestType testType) {
        // Элемент является классом
        if (element instanceof PsiClass) {
            return generate((PsiClass) element, testType);
        }
        // Элемент является методом
        if (element instanceof PsiMethod) {
            return generate((PsiMethod) element, testType);
        }
        // Элемент не является ни классом, ни методом
        throw new IllegalArgumentException("Неподдерживаемый тип элемента: " + element.getClass().getName());
    }

    public void generate(Project project, PsiElement element, PsiDirectory directory, TestType testType) {
        if (!(element instanceof PsiClass) && !(element instanceof PsiMethod))
            throw new IllegalArgumentException(Constants.Strings.Release.Errors.ILLEGAL_ARGUMENT);
        //"Плагин поддерживает работу толь"

        if (directory == null) {
            System.out.println("PsiDirectory is null");
            return;
        }

        // Генерация содержимого файла в зависимости от типа теста
        String fileContent = this.generate(element, testType);
        String fileName = ((PsiNameIdentifierOwner) element).getName() + "Tests.java";//generateFileName(testType);

        // Создание PsiFile
        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(fileName, fileContent);

        // Добавление файла в директорию
        directory.add(file);
    }
    //endregion
}
