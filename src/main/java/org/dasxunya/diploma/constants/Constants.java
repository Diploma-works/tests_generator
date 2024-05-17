package org.dasxunya.diploma.constants;

public class Constants {
    public static class Strings {
        /**
         * Названия библиотек для иморта
         */
        public static class Imports {
            public static String orgJunitJupiterAll = "org.junit.jupiter.api.*";
            public static String orgJunitJupiterParamsAll = "org.junit.jupiter.params.*";
            public static String orgJunitJupiterParamsProviderAll = "org.junit.jupiter.params.provider.*";
        }

        /**
         * Отладочные строки
         */
        public static class Debug {
            public static class Errors {
                public static final String NULL_POINTER = "Передана пустая ссылка на объект, ожидаемый тип:";
                public static final String ILLEGAL_ARGUMENT = "Передан неподдерживаемый тип элемента, ожидаемый тип:";
            }
        }

        /**
         * Релизные строки
         */
        public static class Release {
            public static class Errors {
                public static final String NULL_POINTER = "Передана пустая ссылка на объект";
                public static final String ILLEGAL_ARGUMENT = "Передан неподдерживаемый тип элемента";
            }
        }

        public static class Code {
            public static String space = " ";
            public static String semiColon = ";";
            public static String openBrace = "{\n";
            public static String closeBrace = "}\n";
            public static final String newLine = "\n";
            public static String semiColonNewLine = ";\n";
            public static final String tabulation = "\t";
            public static final String regionOpen = "//region ";
            public static final String regionClose = "// endregion";
            public static final String annotationBeforeEach = "@BeforeEach";
            public static final String annotationAfterEach = "@AfterEach";
        }

        public static class Types {
            public static final String booleanType = "boolean";
            public static final String intType = "int";
            public static final String longType = "long";
            public static final String shortType = "short";
            public static final String byteType = "byte";
            public static final String doubleType = "double";
            public static final String floatType = "float";
            public static final String charType = "char";
            public static final String stringType = "string";
            public static final String voidType = "void";
        }

        public static class Tests {
            public static class Assertions {
                public static final String assertEqual = "Assertions.assertEquals";
                public static final String assertNotEqual = "Assertions.assertNotEquals";
                public static final String assertTrue = "Assertions.assertTrue";
                public static final String assertFalse = "Assertions.assertFalse";
            }
        }

        public static class Extensions {
            public static final String txt = "txt";
            public static final String java = "java";
        }
    }
}
