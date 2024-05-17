package org.dasxunya.diploma.generator;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import org.dasxunya.diploma.constants.TestType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {

    //region Поля

    //region Основные
    /**
     * Флаг удаления выходных файлов тестов
     */
    boolean isDeleteActualFiles = true;
    /**
     * Флаг отладкиы
     */
    boolean isDebug = false;
    MockedStatic<ApplicationManager> mockedApplicationManager;
    /**
     * Абсолютный путь до папки с полученными значениями тестов
     */
    String actualFolderPath = "src\\test\\java\\org\\dasxunya\\diploma\\generator\\actualTests";
    /**
     * Абсолютный путь до папки с ожидаемыми значениями тестов
     */
    String expectedFolderPath = "src\\test\\java\\org\\dasxunya\\diploma\\generator\\expectedTests";
    /**
     * Генератор тестов
     */
    UnitTestsGenerator generator;
    //endregion

    //region Макеты Psi элементов
    @Mock
    public PsiClass mockPsiClass;
    @Mock
    public PsiFile mockPsiFile;
    @Mock
    public PsiDirectory mockPsiDirectory;
    @Mock
    public PsiPackage mockPsiPackage;
    //endregion

    //region Макеты для методов
    /**
     * Контсруктор с параметрами
     */
    @Mock
    public PsiMethod mockConstructor;
    /**
     * Метод с параметрами без возвращаемого значения
     */
    @Mock
    public PsiMethod mockVoidMethod;
    /**
     * Метод с параметрами и возвращаемым значением
     */
    @Mock
    public PsiMethod mockReturnMethod;
    /**
     * Метод без параметров с возвращаемым значением
     */
    @Mock
    public PsiMethod mockNoParamMethod;
    //endregion

    //region Примитивные типы
    @Mock
    public PsiType mockPsiTypeInt, mockPsiTypeDouble, mockPsiTypeBoolean, mockPsiTypeByte, mockPsiTypeChar, mockPsiTypeShort, mockPsiTypeLong, mockPsiTypeFloat, mockPsiTypeString;
    @Mock
    public PsiParameter mockPsiParameterInt, mockPsiParameterDouble, mockPsiParameterBoolean, mockPsiParameterByte, mockPsiParameterChar, mockPsiParameterShort, mockPsiParameterLong, mockPsiParameterFloat, mockPsiParameterString;
    //endregion

    //endregion

    //region Вспомогательные методы

    public void print(String message) {
        System.out.print(message);
    }

    public void println(String message) {
        System.out.println(message);
    }

    public String combinePath(String folderPath, String fileName, String extension) {
        return String.format("%s\\%s.%s", folderPath, fileName, extension);
    }

    public void saveFile(String fileContent, String basePath, String fileName, String extension) {
        // Создание пути к файлу
        Path path = Paths.get(basePath, fileName + "." + extension);
        try {
            // Создание директории, если она не существует
            Files.createDirectories(path.getParent());
            // Запись строки в файл, если файл не существует, он будет создан
            Files.write(path, fileContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.printf("Файл '%s' успешно сохранен\n", path);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Удаляет файл по указанному пути.
     *
     * @param basePath  базовый путь к директории файла
     * @param fileName  имя файла для удаления
     * @param extension расщирение файла для удаления
     */
    public void deleteFile(String basePath, String fileName, String extension) {
        Path path = Paths.get(basePath, fileName + "." + extension);
        try {
            if (Files.exists(path)) {  // Проверка существования файла
                Files.delete(path);    // Удаление файла
                System.out.println("File was successfully deleted: " + path);
            } else {
                System.out.println("File does not exist, no need to delete: " + path);
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }

    /**
     * Сравнивает два текстовых файла построчно.
     *
     * @param pathToFile1 путь к первому файлу
     * @param pathToFile2 путь ко второму файлу
     * @throws IOException если произошла ошибка при чтении файлов
     */
    public void compareFilesByPath(String pathToFile1, String pathToFile2) throws Exception {
        compareFilesByPath(pathToFile1, pathToFile2, false);
    }

    public void compareFilesByPath(String pathToFile1, String pathToFile2, boolean ignoreTabs) throws Exception {
        Path file1 = Paths.get(pathToFile1);
        Path file2 = Paths.get(pathToFile2);

        assertFalse(Files.notExists(file1), "Файл " + pathToFile1 + " не существует");
        assertFalse(Files.notExists(file2), "Файл " + pathToFile2 + " не существует");

        if (file1.equals(file2)) {
            throw new Exception("Сравнивается один и тот же файл!");
        }

        List<String> linesOfFile1 = Files.readAllLines(file1);
        List<String> linesOfFile2 = Files.readAllLines(file2);

        // Проверяем, что количество строк одинаково
        assertEquals(linesOfFile1.size(), linesOfFile2.size(), "Количество строк в файлах различается.");

        // Сравниваем строки одна за другой с учетом или без учета табуляции
        for (int i = 0; i < linesOfFile1.size(); i++) {
            String line1 = linesOfFile1.get(i);
            String line2 = linesOfFile2.get(i);
            if (ignoreTabs) {
                line1 = line1.trim();
                line2 = line2.trim();
            }
            assertEquals(line1, line2, "Строка " + (i + 1) + " различается.");
        }

        println(String.format("Файлы '%s' и '%s' полностью совпадают", pathToFile1, pathToFile2));
    }

    /**
     * Сравнивает файлы построчно
     *
     * @param fileNameExpected Имя файла с ожмдаемыми результатами
     * @param fileNameActual   Имя файла с полученными результатами
     * @param extension        расширение файлов
     * @throws IOException если произошла ошибка при чтении файлов
     */
    public void compareFilesByName(String fileNameExpected, String fileNameActual, String extension, boolean isIgnoreTabs) throws Exception {
        String expectedPath = this.combinePath(this.expectedFolderPath, fileNameExpected, extension);
        String actualPath = this.combinePath(this.actualFolderPath, fileNameActual, extension);
        this.compareFilesByPath(expectedPath, actualPath, isIgnoreTabs);
    }

    public void compareFilesByName(String fileNameExpected, String fileNameActual, String extension) throws Exception {
        this.compareFilesByName(fileNameExpected, fileNameActual, extension, false);
    }

    /**
     * Создание сигнатуры метода с учетом всех типов
     *
     * @param psiMethod  Мок-метод для задания сигнатуры
     * @param parameters Параметры мок-метода
     */
    public String generateSignature(@Mock PsiMethod psiMethod, PsiParameter[] parameters) {
        StringBuilder signatureBuilder = new StringBuilder(psiMethod.getName()).append("(");
        signatureBuilder.append("[");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) signatureBuilder.append(", ");
            PsiParameter param = parameters[i];
            PsiType type = param.getType();
            String typeName = type.getPresentableText();
            // Отображение типа в квадратных скобках без имени параметра
            signatureBuilder.append("PsiType:").append(typeName);
        }
        signatureBuilder.append("]");
        signatureBuilder.append(")");
        return signatureBuilder.toString();
    }

    /**
     * Генерирует исходный код Java метода на основе данных PsiMethod.
     *
     * @param psiMethod метод для генерации исходного кода
     * @return строка с исходным кодом метода
     */
    public String generateJavaMethod(PsiMethod psiMethod) {
        if (psiMethod == null) throw new NullPointerException("PsiMethod is null");

        StringBuilder methodBuilder = new StringBuilder();
        PsiType returnType = psiMethod.getReturnType();
        String methodName = psiMethod.getName();
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
        StringJoiner parameterList = new StringJoiner(", ");

        // Составление списка параметров
        for (PsiParameter parameter : parameters) {
            String parameterType = parameter.getType().getPresentableText();
            String parameterName = parameter.getName();
            parameterList.add(parameterType + " " + parameterName);
        }

        // Определение возвращаемого типа
        String returnTypeName = (returnType != null) ? returnType.getCanonicalText() : "void";
        methodBuilder.append(returnTypeName).append(" ").append(methodName).append("(");
        methodBuilder.append(parameterList).append(") {\n");

        // Добавление тела метода в зависимости от возвращаемого типа
        if ("void".equals(returnTypeName)) {
            methodBuilder.append("    // TODO: Реализация метода\n");
        } else {
            methodBuilder.append("    return ").append(getDefaultValue(returnTypeName)).append(";\n");
        }
        methodBuilder.append("}\n");

        return methodBuilder.toString();
    }

    /**
     * Возвращает значение по умолчанию для типа данных.
     *
     * @param typeName имя типа данных
     * @return строковое представление значения по умолчанию
     */
    public String getDefaultValue(String typeName) {
        if (typeName == null) {
            return "null";
        }
        return switch (typeName) {
            case "boolean" -> "false";
            case "int", "short", "long", "byte" -> "0";
            case "double", "float" -> "0.0";
            case "char" -> "'\\0'";
            default -> "null";
        };
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public PsiType[] getTypesList() {
        //region Типы
        return new PsiType[]{
                PsiType.INT,
                PsiType.BOOLEAN,
                PsiType.BYTE,
                PsiType.CHAR,
                PsiType.SHORT,
                PsiType.LONG,
                PsiType.FLOAT,
                PsiType.DOUBLE,
                PsiType.VOID,
                mockPsiTypeString
        };
    }

    public PsiParameter createPsiParameter(PsiType type, String name) {
        PsiParameter parameter = mock(PsiParameter.class);
        when(parameter.getType()).thenReturn(type);
        when(parameter.getName()).thenReturn(name);
        return parameter;
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public PsiMethod createPsiMethod(PsiType returnType, String name, PsiParameter[] parameters) {
        PsiMethod method = mock(PsiMethod.class);
        when(method.getName()).thenReturn(name);
        when(method.getReturnType()).thenReturn(Objects.requireNonNullElse(returnType, PsiType.VOID));
        PsiParameterList parameterList = mock(PsiParameterList.class);
        // Мокирование PsiParameterList для возвращения списка параметров
        when(parameterList.getParameters()).thenReturn(Objects.requireNonNullElseGet(parameters, () -> new PsiParameter[]{}));
        // Настройка метода getParameterList для возвращения mockVoidParameterList
        when(method.getParameterList()).thenReturn(parameterList);

        // Сигнатура конструктора
        if (parameters == null) parameters = new PsiParameter[]{};
        String signature = this.generateSignature(method, parameters);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(methodSignature.toString()).thenReturn(signature);
        when(method.getSignature(PsiSubstitutor.EMPTY)).thenReturn(methodSignature);
        return method;
    }

    /**
     * Запускает тестирование генератора для указанного макета метода
     *
     * @param testName            Название теста, совпадает с именем выходного файла
     * @param actualFileExtension Расширение выходного файла
     * @param psiMethod           Тестируемый макет метода
     * @param testType            Тип теста
     * @param isDebug             Флаг режима отладки
     */
    public void startGeneratorTest(String testName, String actualFileExtension, PsiMethod psiMethod, TestType testType, boolean isDebug, boolean isIgnoreTabs) {
        this.generator.setDebug(isDebug);

        //region Генерация тестируемого метода и тестирующего метода
        String methodImplementationStr = generateJavaMethod(psiMethod);
        String parameterizedTestStr = generator.generate(psiMethod, testType);
        if (isDebug) {
            System.out.println(methodImplementationStr);
            System.out.println(parameterizedTestStr);
        }
        //endregion

        //region Тестирование
        try {
            //region Генерация тела теста
            String testBodyStr = this.generator.getClassHeader(mockPsiClass, testType) +
                                 "\n" +
                                 "{\n" +

                                 // Сохранение содержимого в файл
                                 methodImplementationStr + "\n" +
                                 parameterizedTestStr + "\n" +
                                 "}\n";
            //endregion
            // Сохранение теста
            saveFile(testBodyStr, this.actualFolderPath, testName, actualFileExtension);
            // Сравнение результатов
            compareFilesByName(testName, testName, actualFileExtension, isIgnoreTabs);
            // Удаление файла после тестирования
            if (this.isDeleteActualFiles)
                deleteFile(this.actualFolderPath, testName, actualFileExtension);
        } catch (Exception e) {
            if (isDebug)
                throw new RuntimeException(e);
            else
                println(e.getMessage());
        }
        //endregion

    }

    public void startGeneratorTest(String testName, String actualFileExtension, PsiMethod psiMethod, TestType testType, boolean isDebug) {
        this.startGeneratorTest(testName, actualFileExtension, psiMethod, testType, isDebug, false);
    }
    //endregion

    //region Инициализация перед тестом

    /**
     * Настройка типов
     */
    public void setupPrimitiveParameters() {

        //region getPresentableText
        when(mockPsiTypeBoolean.getPresentableText()).thenReturn("boolean");
        when(mockPsiTypeByte.getPresentableText()).thenReturn("byte");
        when(mockPsiTypeChar.getPresentableText()).thenReturn("char");
        when(mockPsiTypeShort.getPresentableText()).thenReturn("short");
        when(mockPsiTypeInt.getPresentableText()).thenReturn("int");
        when(mockPsiTypeLong.getPresentableText()).thenReturn("long");
        when(mockPsiTypeFloat.getPresentableText()).thenReturn("float");
        when(mockPsiTypeDouble.getPresentableText()).thenReturn("double");
        when(mockPsiTypeString.getPresentableText()).thenReturn("String");
        //endregion

        //region getCanonicalText
        when(mockPsiTypeBoolean.getCanonicalText()).thenReturn("boolean");
        when(mockPsiTypeByte.getCanonicalText()).thenReturn("byte");
        when(mockPsiTypeChar.getCanonicalText()).thenReturn("char");
        when(mockPsiTypeShort.getCanonicalText()).thenReturn("short");
        when(mockPsiTypeInt.getCanonicalText()).thenReturn("int");
        when(mockPsiTypeLong.getCanonicalText()).thenReturn("long");
        when(mockPsiTypeFloat.getCanonicalText()).thenReturn("float");
        when(mockPsiTypeDouble.getCanonicalText()).thenReturn("double");
        when(mockPsiTypeString.getCanonicalText()).thenReturn("String");
        //endregion

        //region Создание параметров с помощью вспомогательного метода
        mockPsiParameterBoolean = createPsiParameter(mockPsiTypeBoolean, "flag");
        mockPsiParameterByte = createPsiParameter(mockPsiTypeByte, "b");
        mockPsiParameterChar = createPsiParameter(mockPsiTypeChar, "c");
        mockPsiParameterShort = createPsiParameter(mockPsiTypeShort, "s");
        mockPsiParameterInt = createPsiParameter(mockPsiTypeInt, "i");
        mockPsiParameterLong = createPsiParameter(mockPsiTypeLong, "l");
        mockPsiParameterFloat = createPsiParameter(mockPsiTypeFloat, "f");
        mockPsiParameterDouble = createPsiParameter(mockPsiTypeDouble, "d");
        mockPsiParameterString = createPsiParameter(mockPsiTypeString, "str");
        //endregion

    }

    public void setUpConstructor() {
        PsiParameter[] parameters = {createPsiParameter(mockPsiTypeString, "brand"), createPsiParameter(mockPsiTypeString, "model"), createPsiParameter(mockPsiTypeInt, "year"), createPsiParameter(mockPsiTypeDouble, "price")};
        mockConstructor = createPsiMethod(null, "Car", parameters);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public void setupVoidMethod() {
        PsiParameter[] parameters = {mockPsiParameterString, mockPsiParameterInt, mockPsiParameterBoolean, mockPsiParameterByte, mockPsiParameterChar, mockPsiParameterShort, mockPsiParameterLong, mockPsiParameterFloat, mockPsiParameterDouble};
        mockVoidMethod = createPsiMethod(PsiType.VOID, "voidMethod", parameters);
    }

    public void setupReturnMethod() {
        // Подготовка всех параметров
        PsiParameter[] parameters = {mockPsiParameterString, mockPsiParameterInt, mockPsiParameterBoolean, mockPsiParameterByte, mockPsiParameterChar, mockPsiParameterShort, mockPsiParameterLong, mockPsiParameterFloat, mockPsiParameterDouble};
        mockReturnMethod = createPsiMethod(mockPsiTypeBoolean, "returnMethod", parameters);
    }

    public PsiMethod setupReturnMethod(PsiType returnType) {
        // Подготовка всех параметров
        PsiParameter[] parameters = {mockPsiParameterString, mockPsiParameterInt, mockPsiParameterBoolean, mockPsiParameterByte, mockPsiParameterChar, mockPsiParameterShort, mockPsiParameterLong, mockPsiParameterFloat, mockPsiParameterDouble};
        return createPsiMethod(returnType, String.format("returnMethod_%s", returnType.getPresentableText()), parameters);
    }

    public void setupNoParamMethod() {
        mockNoParamMethod = createPsiMethod(mockPsiTypeString, "noParamMethod", null);
    }

    @BeforeEach
    void setUp() {
        mockedApplicationManager = Mockito.mockStatic(ApplicationManager.class);
        MockitoAnnotations.openMocks(this);
        generator = new UnitTestsGenerator(true);

        //region Настройка приложения для JavaDirectoryService
        Application mockApplication = mock(Application.class);
        JavaDirectoryService mockJavaDirectoryService = Mockito.mock(JavaDirectoryService.class);
        mockedApplicationManager.when(ApplicationManager::getApplication).thenReturn(mockApplication);
        Mockito.when(mockApplication.getService(JavaDirectoryService.class)).thenReturn(mockJavaDirectoryService);
        when(mockJavaDirectoryService.getPackage(mockPsiDirectory)).thenReturn(mockPsiPackage);
        //endregion
        //region Настройка моков для PsiFile
        when(mockPsiFile.getContainingDirectory()).thenReturn(mockPsiDirectory);
        //endregion
        //region Настройка моков для PsiPackage
        when(mockPsiPackage.getQualifiedName()).thenReturn("org.dasxunya.diploma.generator.actualTests");
        //endregion
        //region Настройка параметров
        setupPrimitiveParameters();
        //endregion
        //region Настройка для конструктора
        setUpConstructor();
        //endregion
        //region Настройка для других методов
        setupVoidMethod();
        setupReturnMethod();
        setupNoParamMethod();
        //endregion
        //region Настройка моков для PsiClass
        when(mockPsiClass.getName()).thenReturn("Car");
        when(mockPsiClass.getContainingFile()).thenReturn(mockPsiFile);
        when(mockPsiClass.getAllMethods()).thenReturn(new PsiMethod[]{
                this.mockConstructor,
                this.mockVoidMethod,
                this.mockNoParamMethod,
                this.mockReturnMethod
        });
        when(mockPsiClass.getMethods()).thenReturn(new PsiMethod[]{
                this.mockConstructor,
                this.mockVoidMethod,
                this.mockNoParamMethod,
                this.mockReturnMethod
        });
        when(mockPsiClass.getConstructors()).thenReturn(new PsiMethod[]{
                this.mockConstructor
        });
        //endregion
    }

    @AfterEach
    void tearDown() {
        // Очистка мокирования статического метода
        mockedApplicationManager.close();
    }
    //endregion
}
